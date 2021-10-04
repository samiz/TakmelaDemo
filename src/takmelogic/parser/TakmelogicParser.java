package takmelogic.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import takmela.lexer.LexerError;
import takmela.lexer.Token;
import takmelogic.ast.Atom;
import takmelogic.ast.Call;
import takmelogic.ast.Comparison;
import takmelogic.ast.ComparisonOp;
import takmelogic.ast.Fact;
import takmelogic.ast.Int;
import takmelogic.ast.IsEqual;
import takmelogic.ast.NotEqual;
import takmelogic.ast.Rule;
import takmelogic.ast.Str;
import takmelogic.ast.SubGoal;
import takmelogic.ast.Symbol;
import takmelogic.ast.Term;
import takmelogic.ast.TopLevel;
import takmelogic.ast.Var;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class TakmelogicParser implements TakmelogicTokens
{
	int pos;
	List<Token> tokens;
	Token lookAhead;
	Map<Integer, String> tokenNames;

	public TakmelogicParser(Map<Integer, String> tokenNames)
	{
		this.tokenNames = tokenNames;
	}

	public takmelogic.ast.Module parseDatalog(List<Token> _tokens)
			throws LexerError, UniquenessException, IOException, DatalogParsingException
	{
		tokens = _tokens;
		if (tokens.size() == 0)
		{
			throw new RuntimeException("Empty token stream when parsing Datalog");
		}
		lookAhead = tokens.get(0);
		pos = 0;
		return module();
	}

	private takmelogic.ast.Module module() throws DatalogParsingException
	{
		List<TopLevel> defs = new ArrayList<>();

		while (true)
		{
			if (la_rule_or_fact())
			{
				defs.add(rule_or_fact());
			}
			else
			{
				break;
			}
		}

		matchEof();

		return new takmelogic.ast.Module(defs);
	}

	private boolean la_rule_or_fact()
	{
		return la(IDSmall);
	}

	private TopLevel rule_or_fact() throws DatalogParsingException
	{
		Atom head = atom();

		if (la(IF))
		{
			match(IF);

			List<SubGoal> ruleContents = new ArrayList<>();

			SubGoal sg = subGoal();
			ruleContents.add(sg);

			while (la(COMMA))
			{
				match(COMMA);
				sg = subGoal();
				ruleContents.add(sg);
			}
			match(DOT);
			return new Rule(head, ruleContents);
		}
		else
		{
			match(DOT);
			return new Fact(head);
		}
	}

	private Atom atom() throws DatalogParsingException
	{
		String ruleName = consumeText(IDSmall);
		List<Term> args = new ArrayList<>();
		match(OPAREN);
		if (la_term())
		{
			Term t = term();
			args.add(t);
			while (la(COMMA))
			{
				match(COMMA);
				t = term();
				args.add(t);
			}
		}
		match(CPAREN);
		return new Atom(ruleName, args);
	}

	private boolean la_term()
	{
		return la(IDSmall) || la(IDCap) || la(STRING) || la(INT);
	}

	private Term term() throws DatalogParsingException
	{
		if (la(IDSmall))
		{
			String s = consumeText();
			return new Symbol(s);
		}
		else if (la(IDCap))
		{
			String v = consumeText();
			return new Var(v);
		}
		else if (la(STRING))
		{
			String val = consumeText();
			val = stripQuotes(val);
			val = applyStringUnEscape(val);

			return new Str(val);
		}
		else if (la(INT))
		{
			String val = consumeText();
			val = stripQuotes(val);
			val = applyStringUnEscape(val);

			return new Int(Integer.parseInt(val));
		}
		else
		{
			fail("Expected a term (symbol, variable, string or integer)");
			return null; // unreachable since fail always throws
		}
	}

	private SubGoal subGoal() throws DatalogParsingException
	{
		if (la(IDSmall, OPAREN))
		{
			String callee = consumeText();
			List<Term> args = new ArrayList<>();
			match(OPAREN);
			if (la_term())
			{
				Term t = term();
				args.add(t);
				while (la(COMMA))
				{
					match(COMMA);
					t = term();
					args.add(t);
				}
			}
			match(CPAREN);
			Atom a = new Atom(callee, args);
			return new Call(a);
		}
		else if (la_term())
		{
			Term t1 = term();
			if (la(EQ))
			{
				match(EQ);
				Term t2 = term();
				return new IsEqual(t1, t2);
			}
			else if (la(NE))
			{
				match(NE);
				Term t2 = term();
				return new NotEqual(t1, t2);
			}
			else if (la(LT) || la(LE) || la(GT) || la(GE))
			{
				ComparisonOp op = comparisonOpFromLookAhead();
				consumeText();
				Term t2 = term();
				return new Comparison(op, t1, t2);
			}
			else
			{
				fail(String.format("Encountered %s while attempting to parse a subgoal", tokenName()));
				return null; // unreachable since fail always throws
			}
		}
		else
		{
			fail(String.format("Encountered %s while attempting to parse a subgoal", tokenName()));
			return null; // unreachable since fail always throws
		}
	}

	private ComparisonOp comparisonOpFromLookAhead() throws DatalogParsingException
	{
		if(la(LT))
		{
			return ComparisonOp.Lt; 
		}
		else if(la(LE))
		{
			return ComparisonOp.Le; 
		}
		else if(la(GT))
		{
			return ComparisonOp.Gt; 
		}
		else if(la(GE))
		{
			return ComparisonOp.Ge; 
		}
		else
		{
			fail(String.format("Encountered %s while attempting to parse a comparison operator", tokenName()));
			return null; // unreachable since fail always throws
		}
	}

	private String stripQuotes(String val)
	{
		return val.substring(1, val.length() - 1);
	}

	private String applyStringUnEscape(String str)
	{
		// A poor person's escaped string interpreter
		// not tested for security or unicode
		int i = 0;
		StringBuilder sb = new StringBuilder(str.length());
		while (true)
		{
			if (i == str.length())
			{
				break;
			}
			char c = str.charAt(i++);
			if (c != '\\')
			{
				sb.append(c);
			}
			else
			{
				if (i >= str.length())
				{
					throw new RuntimeException("Escape character \\ must be followed by an escape code");
				}
				char c2 = str.charAt(i++);
				switch (c2)
				{
				case '\\':
					sb.append("\\");
					break;
				case '"':
					sb.append("\"");
					break;
				case 'r':
					sb.append("\r");
					break;
				case 'n':
					sb.append("\n");
					break;
				case 't':
					sb.append("\t");
					break;
				case '\'':
					sb.append("'");
					break;
				default:
					throw new RuntimeException(String.format("Unrecognized escape code \\%s", c2));
				}
			}
		} // while
		return sb.toString();
	}

	private String consumeText()
	{
		String ret = lookAhead.text();
		pos++;
		if (pos < tokens.size())
		{
			lookAhead = tokens.get(pos);
		}
		return ret;
	}

	private String consumeText(int tokenType) throws DatalogParsingException
	{
		String ret = lookAhead.text();
		match(tokenType);
		return ret;
	}

	private String tokenName()
	{
		return tokenName(lookAhead.id());
	}

	private String tokenName(int id)
	{
		return Utils.mustGet(tokenNames, id);
	}

	private boolean eof()
	{
		return pos == tokens.size();
	}

	private boolean la(int tokenType)
	{
		return !eof() && lookAhead.id() == tokenType;
	}

	private boolean la(int t1, int t2)
	{
		return ((pos + 1) < tokens.size()) && lookAhead.id() == t1 && tokens.get(pos + 1).id() == t2;
	}

	private void match(int tokenType) throws DatalogParsingException
	{
		if (!eof() && lookAhead.id() == tokenType)
		{
			++pos;
			if (!eof())
			{
				lookAhead = tokens.get(pos);
			}
		}
		else
		{
			String tokType = tokenName(lookAhead.id());
			String msg = String.format("Expected: %s, got %s @line %s", tokenName(tokenType), tokType,
					lookAhead.line());
			throw new DatalogParsingException(msg);
		}
	}

	private void matchEof()
	{
		if (!eof())
		{
			throw new RuntimeException(String.format("Extra token at end of parsing after position %s", pos));
		}
	}

	private void fail(String msg) throws DatalogParsingException
	{
		throw new DatalogParsingException(
				String.format("Error: %s at token # %s, line %s", msg, pos, lookAhead.line()));
	}
}
