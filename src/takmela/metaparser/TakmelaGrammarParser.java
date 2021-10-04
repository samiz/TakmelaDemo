package takmela.metaparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import takmela.ast.*;
import takmela.ast.Module;
import takmela.ast.Plus;
import takmela.ast.Question;
import takmela.ast.Star;
import takmela.lexer.LexerError;
import takmela.lexer.Token;
import takmela.lexer.ast.*;
import utils_takmela.Gensym;
import utils_takmela.Pair;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class TakmelaGrammarParser implements TakmelaTokens
{
	int pos;
    List<Token> tokens;
    Token lookAhead;
    Map<Integer, String> tokenNames;
	    
	public TakmelaGrammarParser(Map<Integer, String> tokenNames)
	{
		this.tokenNames = tokenNames;
	}

	public takmela.ast.Module parseGrammar(List<Token> _tokens) throws LexerError, UniquenessException, IOException, GrammarParsingException
	{
		tokens = _tokens;
	    if(tokens.size() ==0)
	    {
	        throw new RuntimeException("Empty token stream when parsing grammar");
	    }
	    lookAhead = tokens.get(0);
	    pos = 0;
	    return module();
	}

	private takmela.ast.Module module() throws GrammarParsingException
	{
	    List<Rule> parserRules = new ArrayList<>();
	    List<LexerRule> lexerRules = new ArrayList<>();

	    while(true)
	    {
	        if(la_parser_rule())
	        {
	            parserRules.add(parser_rule());
	        }
	        else
	        {
	            break;
	        }
	    }

	    while(la_lexer_rule())
	    {
	        lexerRules.add(lexer_rule());
	    }
	    
	    String lexerErrorRule = null;

	    if(la(IDCap, KW_ERROR))
	    {
	        lexerErrorRule = consumeText(IDCap);
	        match(KW_ERROR);
	        match(SEMI);
	    }
	    
	    matchEof();

	    return new takmela.ast.Module(parserRules, lexerRules, lexerErrorRule);
	}

	private boolean la_parser_rule()
	{
	    return la(IDSmall);
	}

	private Rule parser_rule() throws GrammarParsingException
	{
	    String ruleName = text();
	    match(IDSmall);
	    match(COLON);

	    List<List<Expr>> ruleContents = new ArrayList<>();
	    List<String> labels = new ArrayList<>();

	    ruleContents(ruleContents, labels);

	    match(SEMI);
	    return new Rule(ruleName, ruleContents, labels);
	}

	private void ruleContents(List<List<Expr>> outRuleContents, List<String> outLabels) throws GrammarParsingException
	{
		List<Expr> exprs = seq();
	    outRuleContents.add(exprs);
	    if(la(LABEL))
	    {
	        String _label = consumeText().substring(1);
	        outLabels.add(_label);
	    }

	    while(la(BAR))
	    {
	        match(BAR);
	        exprs = seq();
	        outRuleContents.add(exprs);
	        if(la(LABEL))
	        {
	            String _label = consumeText().substring(1);
	            outLabels.add(_label);
	        }
	    }		
	}

	private List<Expr> seq() throws GrammarParsingException
	{
	    List<Expr> result = new ArrayList<>();
	    while(la_expr())
	    {
	        result.add(expr());
	    }
	    return result;
	}

	private boolean la_expr()
	{
	    return la(STRING) || la(IDSmall) || la(IDCap) || la(OPAREN);
	}

	private Expr expr() throws GrammarParsingException
	{
		Expr e = basic_expr();
		
		while(la(STAR) || la(PLUS) || la(QUESTION))
		{
			if(la(STAR))
	        {
	            match(STAR);
	            e = new Star(makeInnerSeq(e));
	        }
	        else if(la(PLUS))
	        {
	            match(PLUS);
	            e = new Plus(makeInnerSeq(e));
	        }
	        else if(la(QUESTION))
	        {
	            match(QUESTION);
	            e = new Question(makeInnerSeq(e));
	        }
		}
		return e;
	}

	private Expr basic_expr() throws GrammarParsingException
	{
	    if(la(STRING))
	    {
	        String val = consumeText();
	        val = stripQuotes(val);
	        return new Terminal(val);
	    }
	    else if(la(IDSmall))
	    {
	        String val = consumeText();
	        return new Call(val);
	    }
	    else if(la(IDCap))
	    {
	        String val = consumeText();
	        return new Tokenz(val);
	    }
	    else if(la(OPAREN))
	    {
	        match(OPAREN);
	        
	        List<List<Expr>> ruleContents = new ArrayList<>();
		    List<String> labels = new ArrayList<>();
		    ruleContents(ruleContents, labels);
		    
	        match(CPAREN);
	        
        	return new Nested(ruleContents, labels);
	    }
	    else
	    {
	        throw new RuntimeException(String.format("Encountered %s while attempting to parse 'expr'", tokenName()));
	    }
	}

	private List<Expr> makeInnerSeq(Expr e)
	{
		return Utils.list(e);
	}
	
	private String stripQuotes(String val)
	{
		return val.substring(1, val.length()-1);
	}

	private boolean la_lexer_rule()
	{
	    return la(IDCap);
	}

	private LexerRule lexer_rule() throws GrammarParsingException
	{
	    String ruleName = consumeText(IDCap);
	    List<String> _within = new ArrayList<>(), _pushes = new ArrayList<>(), _pops = new ArrayList<>();
	    String _class = "";
	    boolean _skip = false;
	    if(la(KW_WITHIN))
	    {
	        match(KW_WITHIN);
	        _within.add(consumeText(IDCap));
	        while(la(COMMA))
	        {
	            match(COMMA);
	            _within.add(consumeText(IDCap));
	        }
	    }

	    if(la(KW_PUSHES))
	    {
	        match(KW_PUSHES);
	        _pushes.add(consumeText(IDCap));
	        while(la(COMMA))
	        {
	            match(COMMA);
	            _pushes.add(consumeText(IDCap));
	        }
	    }

	    if(la(KW_POPS))
	    {
	        match(KW_POPS);
	        _pops.add(consumeText(IDCap));
	        while(la(COMMA))
	        {
	            match(COMMA);
	            _pops.add(consumeText(IDCap));
	        }
	    }

	    if(la(KW_CLASS))
	    {
	        match(KW_CLASS);
	        _class = consumeText(IDCap);
	    }
	    if(la(KW_SKIP))
	    {
	        match(KW_SKIP);
	        _skip = true;
	    }

	    match(COLON);

	    RExpr expr = r_expr();
	    match(SEMI);

	    return new LexerRule(ruleName, _within, _pushes, _pops, _class, _skip, expr);
	}

	private RExpr r_expr() throws GrammarParsingException
	{
	    RExpr exp = r_seq();
	    if(la(BAR))
	    {
	        List<RExpr> orr = new ArrayList<>();
	        orr.add(exp);
	        while(la(BAR))
	        {
	            match(BAR);
	            RExpr exp2 = r_seq();
	            orr.add(exp2);
	        }
	        exp = new Oring(orr);
	    }
	    return exp;
	}

	private RExpr r_seq() throws GrammarParsingException
	{
	    RExpr exp = r_piece();
	    if(la_rx_piece())
	    {
	        List<RExpr> seq = new ArrayList<>();
	        seq.add(exp);
	        while(la_rx_piece())
	        {
	        	RExpr exp2 = r_piece();
	            seq.add(exp2);
	        }
	        exp = new RXSeq(seq);
	    }
	    return exp;
	}

	private boolean la_rx_piece()
	{
	    return la(STRING) || la(DOLLAR) || la(START_CHARCLASS) || la(START_CHARCLASS_NOT) || la(OPAREN);
	}

	private RExpr r_piece() throws GrammarParsingException
	{
	    RExpr ret = r_basic_piece();
	    while(true)
	    {
	        if(la(STAR))
	        {
	            match(STAR);
	            ret = new takmela.lexer.ast.Star(ret);
	        }
	        else if(la(PLUS))
	        {
	            match(PLUS);
	            ret = new takmela.lexer.ast.Plus(ret);
	        }
	        else if(la(QUESTION))
	        {
	            match(QUESTION);
	            ret = new takmela.lexer.ast.Question(ret);
	        }
	        else
	        {
	            break;
	        }
	    }
	    return ret;
	}

	private RExpr r_basic_piece() throws GrammarParsingException
	{
	    if(la(STRING))
	    {
	        String val = consumeText();
	        val = stripQuotes(val);
	        val = applyStringUnEscape(val);

	        return new Str(val);
	    }
	    else if(la(DOLLAR))
	    {
	        match(DOLLAR);
	        String val = consumeText();
	        return new ByName(val);
	    }
	    else if(la(OPAREN))
	    {
	        match(OPAREN);
	        RExpr val = r_expr();
	        match(CPAREN);
	        return val;
	    }
	    else if(la(START_CHARCLASS) || la(START_CHARCLASS_NOT))
	    {
	        List<CharClassPart> parts = new ArrayList<>();
	        boolean charClassNot = false;
	        if(la(START_CHARCLASS))
	        {
	            match(START_CHARCLASS);
	        }
	        else
	        {
	            charClassNot = true;
	            match(START_CHARCLASS_NOT);
	        }
	        while(la(CHAR))
	        {
	            if(la(CHAR, DASH))
	            {
	                String _a = consumeText();
	                match(DASH);
	                String _b = consumeText();
	                _a = interpretChar(_a);
	                _b = interpretChar(_b);

	                // TODO: those will need changing if we support full unicode
	                char a = _a.charAt(0);
	                char b = _b.charAt(0);

	                parts.add(new CharPartRange(a, b));
	            }
	            else
	            {
	                String _a = consumeText();
	                _a = interpretChar(_a);
	                // TODO: those will need changing if we support full unicode
	                char a = _a.charAt(0);
	                parts.add(new CharPartSingle(a));
	            }
	        }
	        match(END_CHARCLASS);
	        if(charClassNot)
	        {
	            return new NotCharClass(parts);
	        }
	        return new CharClass(parts);
	    }
	    else
	    {
	    	throw new RuntimeException(String.format("Expected the start of a basic regular expression, got %s", lookAhead.text()));
	    }
	}

	private String applyStringUnEscape(String str)
	{
		// A poor person's escaped string interpreter
		// not tested for security or Unicode
		int i = 0;
		StringBuilder sb = new StringBuilder(str.length());
		while(true)
		{
			if(i == str.length())
			{
				break;
			}
			char c = str.charAt(i++);
			if(c != '\\')
			{
				sb.append(c);
			}
			else
			{
				if(i >= str.length())
				{
					throw new RuntimeException("Escape character \\ must be followed by an escape code");
				}
				char c2 = str.charAt(i++);
				switch(c2)
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

	private String interpretChar(String s)
	{
	    if(s.equals("\\n"))
	    {
	        return "\n";
	    }
	    else if(s.equals("\\r"))
	    {
	        return "\r";
	    }
	    else if(s.equals("\\t"))
	    {
	        return "\t";
	    }
	    else if(s.equals("\\\\"))
	    {
	        return "\\";
	    }
	    else if(s.equals("\\'"))
	    {
	        return "'";
	    }
	    else if(s.equals("\\-"))
	    {
	        return "-";
	    }
	    else if(s.equals("\\\""))
	    {
	        return "\"";
	    }
	    else
	    {
	        return s;
	    }
	}

	private String text()
	{
	    return lookAhead.text();
	}

	private String consumeText()
	{
	    String ret = lookAhead.text();
	    pos++;
	    if(pos < tokens.size())
	    {
	        lookAhead = tokens.get(pos);
	    }
	    return ret;
	}

	private String consumeText(int tokenType) throws GrammarParsingException
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
	    return ((pos+1) < tokens.size()) && lookAhead.id() == t1 && tokens.get(pos+1).id()==t2;
	}

	private void match(int tokenType) throws GrammarParsingException
	{
	    if(!eof() && lookAhead.id() == tokenType)
	    {
	        ++pos;
	        if(!eof())
	        {
	            lookAhead = tokens.get(pos);
	        }
	    }
	    else
	    {
	        String tokType = tokenName(lookAhead.id());
	        String msg = String.format("Expected: %s, got %s @line %s",
	                tokenName(tokenType),
	                tokType,
	                lookAhead.line());
	        throw new GrammarParsingException(msg);
	    }
	}

	private void matchEof()
	{
	    if(!eof())
	    {
	        throw new RuntimeException(String.format("Extra token at end of parsing after position %s", pos));
	    }
	}

	public Module transformGrammar(Module mod)
	{
		Gensym sym = new Gensym();
		List<Rule> data = mod.Rules;
		data = Utils.fixedPoint(data, (d)-> {
			boolean changes = false;
			List<Rule> newRules = new ArrayList<>();
			
			for(Rule r: d)
			{
				List<Rule> createdRules = new ArrayList<>();
				r = transformStars(r, createdRules, sym);
				if(createdRules.size() !=0 )
				{
					changes = true;
				}
				newRules.add(r);
				newRules.addAll(createdRules);
			}
			
			return new Pair<>(newRules, changes);
		});
		return new takmela.ast.Module(data, mod.LexerRules, mod.LexerErrorRule);
	}

	private Rule transformStars(Rule r, List<Rule> createdRules, Gensym sym)
	{
		List<List<Expr>> options = Utils.map(r.Options, a->transformStars(a, createdRules, sym));
		return new Rule(r.Name, options, r.Labels);
	}

	private List<Expr> transformStars(List<Expr> seq, List<Rule> createdRules, Gensym sym)
	{
		return Utils.map(seq, expr->transformStars(expr, createdRules, sym));
	}

	private Expr transformStars(Expr expr, List<Rule> createdRules, Gensym sym)
	{
		if(expr instanceof takmela.ast.Star)
		{
			/*
				myRule : (a)*
				
				 ===>
				myRule: gen1
				
				{ gen1(splice) }
				gen1: a gen1
				gen1 : 
				 
			 */
			takmela.ast.Star e = (takmela.ast.Star) expr;
			String genName = sym.sym("star%");
			List<Expr> gen_option_a = Utils.appendElement(e.Inner, new Call(genName));
			List<Expr> gen_option_b = new ArrayList<>();
			Rule gen = new Rule(genName, Utils.list(gen_option_a, gen_option_b), Utils.list());
			
			createdRules.add(gen);
			
			return new Call(genName);
		}
		else if(expr instanceof takmela.ast.Plus)
		{
			/*
			myRule : (a)+
			
			 ===>
			myRule: gen1
			
			{ gen1(splice) }
			gen1: a gen1
			gen1 : a
			 
		    */
			takmela.ast.Plus e = (takmela.ast.Plus) expr;
			String genName = sym.sym("plus%");
			List<Expr> gen_option_a = Utils.appendElement(e.Inner, new Call(genName));
			List<Expr> gen_option_b = e.Inner;
			Rule gen = new Rule(genName, Utils.list(gen_option_a, gen_option_b), Utils.list());
			
			createdRules.add(gen);
			
			return new Call(genName);
		}
		else if(expr instanceof takmela.ast.Question)
		{
			/*
			myRule : (a)?
			
			 ===>
			myRule: gen1
			
			{ gen1(splice) }
			gen1: a 
			gen1 : 
			 
		    */
			takmela.ast.Question e = (takmela.ast.Question) expr;
			String genName = sym.sym("question%");
			List<Expr> gen_option_a = e.Inner;
			List<Expr> gen_option_b = new ArrayList<>();
			Rule gen = new Rule(genName, Utils.list(gen_option_a, gen_option_b), Utils.list());
			
			createdRules.add(gen);
			
			return new Call(genName);
		}
		else if(expr instanceof takmela.ast.Nested)
		{
			/*
			myRule : (a / b)
			
			 ===>
			myRule: gen1
			
			{ gen1(splice) }
			gen1: a 
			 
		    */
			takmela.ast.Nested e = (takmela.ast.Nested) expr;
			String genName = sym.sym("nested%");
			
			Rule gen = new Rule(genName, e.InnerOptions, e.InnerLabels);
			
			createdRules.add(gen);
			
			return new Call(genName);
		}
		else
		{
			return expr;
		}
	}
}
