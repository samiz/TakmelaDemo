package takmela.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import takmela.ast.Expr;
import takmela.ast.Rule;
import takmela.ast.Terminal;
import takmela.ast.Tokenz;
import takmela.lexer.Token;
import takmela.lexer.TokenVocab;
import takmela.tree.Leaf;
import takmela.tree.Node;
import takmela.tree.Tree;
import takmela.tree.TreeUtils;
import takmela.tree.Treeish;
import utils_takmela.Pair;
import utils_takmela.Utils;
import utils_takmela.fn.IProc3;

public class ParseEngine implements IParseEngine
{
	public final List<Rule> rules;
	private TokenVocab tokenVocab;

	private final Map<String, Rule> namedRules = new HashMap<>();
	private final Map<Call, Set<Cont>> continuations = new HashMap<>();
	private Map<Call, Set<Cont>> newContinuations = new HashMap<>();
	private final Map<Call, Set<Integer>> successfulCalls = new HashMap<>();
	private Map<Call, Set<Integer>> newSuccessfulCalls = new HashMap<>();
	private Map<Pair<Call, Integer>, List<Treeish>> successfulCallTrees = new HashMap<>();

	private final Set<Pair<Integer, Cont>> awakenings = new HashSet<>();
	
	private List<Token> input;

	private Consumer<Node> onSuccessfulParse;
	private Function<List<Treeish>, List<Treeish>> combineTrees;
	
	// Parser errors
	public int lastFailPos;
	public Set<Expected> lastFailExpected;
	public Token lastFailGot;
	private boolean anySuccess;
	
	private TakmelaTracer tracer;
	
	public ParseEngine(List<Rule> rules)
	{
		this.rules = rules;
		this.tracer = new NopTracer();
		
		setNormalForest();
		//setErrorOnAmbiguity();
	}
	
	public ParseEngine setErrorOnAmbiguity() { this.combineTrees = (a)->errorOnAmbiguityCombine(a); return this; }
	public ParseEngine setNormalForest() { this.combineTrees = (a)->normalForestCombine(a); return this; }
	
	public void setTracer(TakmelaTracer tracer) { this.tracer = tracer; }
	
	public void initTokenVocab(TokenVocab tokenVocab)
	{
		this.tokenVocab = tokenVocab;
		for (Rule r : rules)
		{
			namedRules.put(r.Name, r);
		}		 
	}
	
	public void parse(String startSymbol, List<Token> input, Consumer<Node> onSuccessfulParse, 
			IProc3<Set<Expected>, String, Integer> onFail)
	{
		this.onSuccessfulParse = onSuccessfulParse;
		this.input = input;
		
		this.lastFailPos = -1;
		this.lastFailExpected = null;
		this.lastFailGot = null;
		this.anySuccess = false;
		
		tracer.reset();
		
		tracer.trInitialTopLevelCall();
		callTopLevel(startSymbol, 0);
		tracer.trEndInitialTopLevelCall();
		
		Map<Call, Set<Integer>> scWorklist;
		Map<Call, Set<Cont>> kWorklist;
		
		int iteration = 0; // debugging only!
		
		while (true)
		{
			scWorklist = Utils.mapSetDiff(newSuccessfulCalls, successfulCalls);
			kWorklist = Utils.mapSetDiff(newContinuations, continuations);
			
			tracer.trThisIterationsWorklists(scWorklist, kWorklist, successfulCallTrees);
			
			Utils.merge(successfulCalls, newSuccessfulCalls);
			Utils.merge(continuations, newContinuations);
			
			newSuccessfulCalls = new HashMap<>();
			newContinuations = new HashMap<>();

			awakenings.clear();
			
			boolean stop = true; // determine if reached fixedpoint
			
			if (scWorklist.size() != 0)
			{
				stop = false;
				
				for (Entry<Call, Set<Integer>> kv : scWorklist.entrySet())
				{
					Call k = kv.getKey();
					Set<Integer> ps = kv.getValue();
					for (int p : ps)
					{
						updateFromSuccess(k, p);
					}
				}
			}

			if (kWorklist.size() != 0)
			{
				stop = false;
				
				for (Entry<Call, Set<Cont>> kv : kWorklist.entrySet())
				{
					Call k = kv.getKey();
					Set<Cont> cs = kv.getValue();
					for (Cont c : cs)
					{
						updateFromNewCall(k, c);
					}
				}
			}

			if (stop)
			{
				break;
			}
			
			iteration++;
			tracer.trNextIteration();
		} // while / fixedpoint
		
		tracer.trReachedFixedPoint();
		
		if(!anySuccess)
		{
			String got = this.lastFailGot != null ? this.lastFailGot.text() : "<EOF>";
			int pos;
			if(this.lastFailGot != null)
			{
				pos = this.lastFailGot.pos();
			}
			else
			{
				if(!this.input.isEmpty())
				{
					Token t = Utils.last(this.input);
					pos = t.pos() + t.text().length();
				}
				else
				{
					pos = 0;
				}
			}
			onFail.apply(this.lastFailExpected, got, pos);
		}
	}
	
	private void updateFromSuccess(Call succeeded, int inputPosAfterSuccess)
	{
		Set<Cont> contList = continuations.getOrDefault(succeeded, new HashSet<>());

		List<Treeish> ts = Utils.mustGet(successfulCallTrees,
				new Pair<>(succeeded, inputPosAfterSuccess));
		
	 	if(contList.isEmpty())
		{
			tracer.trNoContinuationsForNewSuccess(succeeded, new Pair<>(inputPosAfterSuccess, ts));
		}
		
		for (Cont cont : contList)
		{
			Pair<Integer, Cont> sk = new Pair<>(inputPosAfterSuccess, cont);
			if (!awakenings.contains(sk))
			{
				awakenings.add(sk);
				
				tracer.trJoinFromSuccess(succeeded, inputPosAfterSuccess, Utils.set(ts), cont);
				List<Treeish> ts2 = this.combineTrees.apply(ts);
				for(Treeish ast : ts2)
				{
					this.process(cont.Caller, cont.Code,
								inputPosAfterSuccess, cont.TreeSoFar.appendChild(ast));
				}
				
				tracer.trEndJoinFromSuccess(succeeded, inputPosAfterSuccess, ts, cont);
			}
		}
	}
	
	private void updateFromNewCall(Call callee, Cont cont)
	{
		Set<Integer> sc = successfulCalls.getOrDefault(callee, new HashSet<>());
		
		if(sc.isEmpty())
		{
			tracer.trNoSuccessesForNewCall(callee, cont);
		}
		
		for (int s : sc)
		{
			Pair<Integer, Cont> sk = new Pair<>(s, cont);
			
			if (!awakenings.contains(sk))
			{
				awakenings.add(sk);

				List<Treeish> ts = Utils.mustGet(successfulCallTrees, new Pair<>(callee, s));

				List<Treeish> ts2 = this.combineTrees.apply(ts);
				
				tracer.trJoinFromNewCall(callee, s, cont, ts2);
				
				for(Treeish ast : ts2)
				{
					this.process(cont.Caller, cont.Code, s,
								cont.TreeSoFar.appendChild(ast));
				}
				
				tracer.trEndJoinFromNewCall(callee, s, cont);
			}
		}
	}

	private void processRule(Call execRule)
	{
		Rule r = namedRules.get(execRule.Callee);
		
		if(r == null)
		{
			throw new RuntimeException("Non-existing rule: " + execRule.Callee);
		}
		
		if(r.Labels.size() != 0 && r.Labels.size() != r.Options.size())
	    {
	        throw new RuntimeException(String.format("Rule %s: all choices must have labels or none must have labels", r.Name));
	    }
		
		for (int i = 0; i < r.Options.size(); ++i)
		{
			String label = execRule.Callee;
	        if(r.Labels.size() != 0)
	        {
	            label = r.Labels.get(i);
	        }
	        
	        // Since execRule is just starting, the inputPosNow is the same as its call inputPos
			process(execRule, new CodePos(r, i, 0), execRule.InputPos, new Tree(label, i));
		}
	}

	private void process(Call execRule, CodePos seq, int inputPosNow, Tree treeSoFar)
	{
		if (execRule.Callee.equals("Root"))
		{
			if (eof(inputPosNow))
			{
				anySuccess = true;
				onSuccessfulParse.accept(treeSoFar);
				tracer.trSuccessfulParse();
			}
			else
			{
				tracer.trNotSuccessfulParse();
				return;
			}
		}
		
		tracer.trEnterProcess(execRule, seq, inputPosNow);
		
		boolean stop = false;
		List<Expr> ruleAlt = seq.rule.Options.get(seq.ruleAlt);
		while (seq.i < ruleAlt.size())
		{
			Expr e = ruleAlt.get(seq.i);
			seq = seq.next();
			if (e instanceof Terminal)
			{
				Terminal t = (Terminal) e;
				if (!match(t.Value, inputPosNow))
				{
					stop = true;
					recordExpected(t.Value, inputPosNow, execRule.Callee);
					tracer.trMatch(execRule, seq, "`" + t.Value + "`", dbgInputToken(inputPosNow), false, inputPosNow);
					break;
				}
				else
				{
					treeSoFar = treeSoFar.appendChild(new Leaf(inputHere(inputPosNow)));
					inputPosNow++;
					tracer.trMatch(execRule, seq, t.Value, dbgInputToken(inputPosNow-1), true, inputPosNow);
				}
			}
			else if (e instanceof Tokenz)
			{
				Tokenz t = (Tokenz) e;
				int id = tokenVocab.tokenId(t.Value);
				if (!match(id, t.Value, inputPosNow))
				{
					stop = true;
					recordExpected(t.Value, inputPosNow, execRule.Callee);
					tracer.trMatch(execRule, seq, t.Value,dbgInputToken(inputPosNow), false, inputPosNow);
					break;
				}
				else
				{
					treeSoFar = treeSoFar.appendChild(new Leaf(inputHere(inputPosNow)));
					inputPosNow++;
					tracer.trMatch(execRule, seq, t.Value, dbgInputToken(inputPosNow-1), true, inputPosNow);
				}
			}
			else if (e instanceof takmela.ast.Call)
			{
				takmela.ast.Call c = (takmela.ast.Call) e;
				call(new Call(c.Callee, inputPosNow), execRule, seq, treeSoFar);
				stop = true;
				break;
			}
			else
			{
				throw new RuntimeException("Option not handled: " + e);
			}
		} // while
		tracer.trExitProcess(execRule);
		if (!stop)
		{
			// Reached end of the rule with success
			doReturn(execRule, inputPosNow, treeSoFar);
		}
	}

	private void call(Call callee, Call caller, CodePos seq, Tree treeSoFar)
	{
		Cont cont = mkCont(callee.Callee, caller, seq, treeSoFar);
		
		Call key = callee;
		boolean firstCall = !continuations.containsKey(key) && !newContinuations.containsKey(key);
		Utils.addMapSet(this.newContinuations, key, cont);

		tracer.trCall(callee, caller, cont, firstCall);
		
		if (firstCall)
		{
			processRule(callee);
		}
		
		tracer.trEndCall(callee, caller, cont, firstCall);
	}
	
	private void callTopLevel(String startSymbol, int inputPos)
	{
		Rule r = namedRules.get(startSymbol);
		if(r == null)
		{
			throw new RuntimeException("Did not find start symbol '" + startSymbol + "' in grammar");
		}
		
		Call thisCall = new Call(startSymbol, inputPos);
		Cont rootC = mkRootCont(thisCall);
		
		Utils.addMapSet(this.continuations, thisCall, rootC);
		
		tracer.trCallTopLevel(thisCall, rootC);
		
		for (int i = 0; i < r.Options.size(); ++i)
		{
			process(thisCall, new CodePos(r, i, 0), inputPos, new Tree(startSymbol, 0));
		}
		
		tracer.trEndCallTopLevel(thisCall, rootC);
	}

	private void doReturn(Call exec, int inputPosNow, Tree treeSoFar)
	{
		Pair<Call, Integer> key2 = new Pair<>(exec, inputPosNow);
		Utils.addMapSet(newSuccessfulCalls, exec, inputPosNow);
		Utils.addMapList(successfulCallTrees, key2, treeSoFar);
		
		tracer.trSuccess(exec, inputPosNow, treeSoFar);
	}

	private void recordExpected(String expected, int inputPos, String execRuleName) 
	{
		if(inputPos > lastFailPos)
		{
			lastFailPos = inputPos;
			lastFailExpected = new HashSet<>();
			lastFailExpected.add(new Expected(expected, execRuleName));
			lastFailGot = inputPos < input.size()? input.get(inputPos) : null;
		}
		else if(inputPos == lastFailPos)
		{
			lastFailExpected.add(new Expected(expected, execRuleName));
		}
	}

	private Cont mkCont(String callee, Call caller, CodePos seq, Tree treeSoFar)
	{
		// Remember: the callee is just for tracing, a continuation is Call+CodePos
		return new Cont(caller, seq, callee, treeSoFar);
	}

	private Cont mkRootCont(Call call)
	{
		// Create an AST representing the root call
		// so that it can be processed and added to successes
		// for more details see TakmelaDatalogEngine.mkRootCont(Call call)
		
		List<takmela.ast.Expr> seq = new ArrayList<>();
		seq.add(new takmela.ast.Call(call.Callee));
		List<List<takmela.ast.Expr>> alt = new ArrayList<>();
		alt.add(seq);
		Rule ast = new Rule("Root", 
							alt, 
							Utils.list()); 

		return new Cont(new Call("Root", 0), new CodePos(ast, 0, 1), "Root", new Tree("Root", 0));
	}

	private boolean match(String value, int inputPos)
	{
		if (eof(inputPos))
		{
			return false;
		}
		String inp = input.get(inputPos).text();
		boolean result = inp.equals(value);
		return result;
	}

	private boolean match(int tokenId, String lexeme, int inputPos)
	{
		if (eof(inputPos))
		{
			return false;
		}
		int inp = input.get(inputPos).id();
		boolean result = inp == tokenId;
		
		return result;
	}
	
	private String dbgInputToken(int inputPos) { if(eof(inputPos) ) { return "(eof)"; } else { return inputHere(inputPos).text(); } }
	private boolean eof(int inputPos) {	return inputPos >= input.size(); }
	private Token inputHere(int inputPos) {	return input.get(inputPos);	}
	
	public List<Treeish> errorOnAmbiguityCombine(List<Treeish> ts)
	{
		if(ts.size() != 1 )
		{
			System.err.println(String.format("Discovered multiple trees:\n:%s", ts));
			String pos = "";
			if(ts.get(0) instanceof Tree)
			{
				Tree t = (Tree) ts.get(0);
				takmela.tree.Token start = TreeUtils.leftMost(t);
				takmela.tree.Token end = TreeUtils.rightMost(t);
				pos = String.format("@input-range %s:%s -> %s:%s", start.Token.line()+1, start.Token.col(), end.Token.line()+1, end.Token.col());
			}
			throw new RuntimeException("Ambiguity discovered in grammar" + pos);
		}
		return ts;
	}
	
	public List<Treeish> normalForestCombine(List<Treeish> ts)
	{
		return ts;
	}
}
