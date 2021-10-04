package takmela.lexer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import takmela.lexer.ast.LexerRule;
import takmela.lexer.automata.FA;
import takmela.lexer.automata.FAAlgo;
import takmela.lexer.automata.Trans;
import utils_takmela.Pair;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class Lexer implements TokenVocab
{
	private final boolean writeDotFiles;
	private final Map<String, FA> ruleDfas;
	private Map<String, Integer> vocab = null;
	private Map<Integer, String> tokenNames = null;
	private final Set<String> skipRules;

	private String inputText;
	private int pos;

	private int[] linePositions;
	
	private Stack<String> scopes;
	private Map<String, List<String>> pushers;
	private Map<String, List<String>> poppers;
	private Map<String, List<Pair<String, FA>>> dfasByScope;

	public Lexer(takmela.lexer.ast.Module m, Map<String, Integer> tokenVocab, boolean writeDotFiles) throws IOException, UniquenessException
	{
		this.writeDotFiles = writeDotFiles;
		this.scopes = new Stack<>();
		ruleDfas = initAutomata(m.Rules);
		skipRules = initSkip(m.Rules);
		vocab = tokenVocab;
		tokenNames = Utils.invertMap(vocab);
		dfasByScope = initScopes(m.Rules, ruleDfas);
	}

	public Lexer(List<LexerRule> rules, Map<String, Integer> tokenVocab, boolean writeDotFiles) throws IOException, UniquenessException
	{
		this.writeDotFiles = writeDotFiles;
		this.scopes = new Stack<>();
		ruleDfas = initAutomata(rules);
		skipRules = initSkip(rules);
		vocab = tokenVocab;
		tokenNames = Utils.invertMap(vocab);
		dfasByScope = initScopes(rules, ruleDfas);
	}

	public Lexer(takmela.lexer.ast.Module m, boolean writeDotFiles) throws IOException, UniquenessException
	{
		this.writeDotFiles = writeDotFiles;
		this.scopes = new Stack<>();
		ruleDfas = initAutomata(m.Rules);
		skipRules = initSkip(m.Rules);
		vocab = initDefaultVocab(m.Rules);
		tokenNames = Utils.invertMap(vocab);
		dfasByScope = initScopes(m.Rules, ruleDfas);
	}

	public Lexer(List<LexerRule> rules, boolean writeDotFiles) throws IOException, UniquenessException
	{
		this.writeDotFiles = writeDotFiles;
		this.scopes = new Stack<>();
		ruleDfas = initAutomata(rules);
		skipRules = initSkip(rules);
		vocab = initDefaultVocab(rules);
		tokenNames = Utils.invertMap(vocab);
		dfasByScope = initScopes(rules, ruleDfas);
	}

	public Map<Integer, String> tokenNames() { return tokenNames; }
	
	// Must be called after initialization with input str
	public int[] linePositions() { return linePositions; }
	
	public static Map<String, Integer> initDefaultVocab(List<LexerRule> rules)
	{
		Map<String, Integer> vocab = new HashMap<>();
		int c = 0;
		for (LexerRule r : rules)
		{
			vocab.put(r.Name, c++);
		}
		return vocab;
	}

	private Set<String> initSkip(List<LexerRule> rules)
	{
		Set<String> ret = new HashSet<>();
		for (LexerRule r : rules)
		{
			if (r.Skip)
			{
				ret.add(r.Name);
			}
		}
		return ret;
	}
	private Map<String, FA> initAutomata(List<LexerRule> rules) throws IOException
	{
		return Lexer.initAutomata(rules, writeDotFiles);
	}
	
	public static Map<String, FA> initAutomata(List<LexerRule> rules, boolean writeDotFiles) throws IOException
	{
		Map<String, FA> ruleNfas = new HashMap<>();
		FAAlgo algo = new FAAlgo();

		if (writeDotFiles)
		{
			File f = new File("./dots");
			if (f.exists())
			{
				if (!f.isDirectory())
				{
					throw new RuntimeException(
							"Cannot write .dot files to " + f.getAbsolutePath() + ", not a directory");
				}
			}
			else
			{
				f.mkdirs();
			}
		}
		for (LexerRule r : rules)
		{
			ruleNfas.put(r.Name, algo.nfaFromRegEx(r.Expr));
		}

		if (writeDotFiles)
		{
			//TODO:
		}
		//*/

		Map<String, FA> ruleDfas = new HashMap<>();

		for (Entry<String, FA> kv : ruleNfas.entrySet())
		{
			String name = kv.getKey();
			FA dfa = algo.dfaFromNfa(kv.getValue());
			ruleDfas.put(name, dfa);
			if (writeDotFiles)
			{
				//	TODO:
			}
		}

		Map<String, FA> ruleNoCommon = new HashMap<>();

		for (Entry<String, FA> kv : ruleDfas.entrySet())
		{
			FA noCommonTrans = algo.nfaWithNoCommonTransitions(kv.getValue());
			ruleNoCommon.put(kv.getKey(), noCommonTrans);

			if (writeDotFiles)
			{
				// TODO:
			}
		}

		for (Entry<String, FA> kv : ruleNoCommon.entrySet())
		{
			String name = kv.getKey();
			FA dfa = algo.dfaFromNfa(kv.getValue());
			ruleDfas.put(name, dfa);
			if (writeDotFiles)
			{
				// TODO:
			}
		}
		return ruleDfas;
	}

	private Map<String, List<Pair<String, FA>>> initScopes(List<LexerRule> rules, Map<String, FA> _ruleDfas)
	{
		Map<String, List<Pair<String, FA>>> ret = new HashMap<>();
		
		pushers = new HashMap<>();
		poppers = new HashMap<>();
		
		for(LexerRule r : rules)
		{
		    String name = r.Name;
		    FA fa = Utils.mustGet(_ruleDfas, name);
		    for(String scope : r.Within)
		    {
		    	Utils.addMapList(ret, scope, new Pair<>(name, fa));
		    }
		    if(r.Within.size() == 0)
		    {
		        Utils.addMapList(ret, "", new Pair<>(name, fa));
		    }
		
		    pushers.put(name, Utils.list(r.Pushes));
		    poppers.put(name, Utils.list(r.Pops));
		}
		return ret;
	}
	
	public void init(String input)
	{
		this.scopes.clear();
		this.inputText = input;
		this.pos = 0;
		this.linePositions = LexerUtils.linePositions(input);
	}

	public boolean hasMoreTokens()
	{
		return pos < inputText.length();
	}

	public Token nextToken() throws LexerError
	{
		String maxAcceptingRule = null;
		int maxAcceptingPos = 0;

		String scope = "";
	    if(!scopes.empty())
	    {
	        scope = scopes.peek();
	    }
	    
		forRules: for (Pair<String, FA> kv : Utils.mustGet(this.dfasByScope, scope))
		{
			String ruleName = kv.a;
			FA fa = kv.b;
			int state = fa.startState;
			int p = pos;
			while (true)
			{
				if (p == inputText.length())
				{
					if (fa.acceptingStates.contains(state))
					{
						return accept(ruleName, p);
					}
					else
					{
						Pair<Integer,Integer> lineCol = LexerUtils.lineCol(pos, linePositions);
						throw new LexerError(lineCol.a, lineCol.b, "No recognizable token at end of file");
					}
				}

				char c = inputText.charAt(p);
				boolean found = false;
				
				for (Pair<Trans, Integer> t : fa.outTrans(state))
				{
					if (t.a.match(c))
					{
						state = t.b;
						found = true;
						break;
					}
				}
				if (found)
				{
					p++;
				}
				else
				{
					if (fa.acceptingStates.contains(state))
					{
						if (p > maxAcceptingPos)
						{
							maxAcceptingPos = p;
							maxAcceptingRule = ruleName;
							continue forRules;
						}
						else
						{
							continue forRules;
						}
					}
					else
					{
						continue forRules;
					}
				}
			}
		}
		if (maxAcceptingPos > pos)
		{
			return accept(maxAcceptingRule, maxAcceptingPos);
		}
		else
		{
			String context = Utils.mid(inputText, pos, 5);
			Pair<Integer,Integer> lineCol = LexerUtils.lineCol(pos, linePositions);
			throw new LexerError(lineCol.a, lineCol.b,
					String.format("Cannot process input near [[ %s ]] (pos=%s)",formatLiteral(context), pos));
		}
	}

	private String formatLiteral(String s)
	{
		return s.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
	}

	private Token accept(String ruleName, int p)
	{
		String lexeme = this.inputText.substring(pos, p);
		Pair<Integer, Integer> lineCol = LexerUtils.lineCol(pos, linePositions);
		Token t = new Token(tokenId(ruleName), lexeme, pos, lineCol.a, lineCol.b, skipRules.contains(ruleName));
		pos = p;
		
		for(String _p : Utils.mustGet(pushers, ruleName))
		{
		    scopes.push(_p);
		}
		
		for(String _p : Utils.mustGet(poppers, ruleName))
		{
		    String s = scopes.peek();
		    if(!s.equals(_p) ) { throw new RuntimeException("Unable to pop scope '" + p +"', found scope '" + s +"'"); }
		    scopes.pop();
		}

		return t;
	}

	public int tokenId(String ruleName)
	{
		// done: Handle non-existing token types, see the call to this function in ParseEngine.process
		if(!vocab.containsKey(ruleName))
		{
			throw new RuntimeException("Unknown token type: " + ruleName);
		}
		return vocab.get(ruleName);
	}

	@SuppressWarnings("unused")
	private void log(String fmt, Object... args)
	{
		System.out.println(String.format(fmt, args));
	}
}
