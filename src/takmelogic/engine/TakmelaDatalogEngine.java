package takmelogic.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;

import takmelogic.ast.Atom;
import takmelogic.ast.ComparisonOp;
import takmelogic.ast.Int;
import takmelogic.ast.Rule;
import takmelogic.ast.Str;
import takmelogic.ast.SubGoal;
import takmelogic.ast.Symbol;
import takmelogic.ast.Term;
import takmelogic.ast.Var;
import utils_takmela.Box;
import utils_takmela.Pair;
import utils_takmela.Utils;

public class TakmelaDatalogEngine
{
	private final Map<String, List<Rule>> rules;
	private final Map<String, List<List<Object>>> facts;

	private Call rootCall;
	private Map<Call, Set<Cont>> k, kNew;
	private Map<Call, Set<List<Object>>> s, sNew;
	
	// Set of <success, continuation>, resumed at success point> pairs
	private final Set<Pair<List<Object>, Cont>> awakenings = new HashSet<>(); 
	
	private Consumer<List<Object>> resultFn;

	private TakmelogicTracer tracer;
	
	public TakmelaDatalogEngine(Map<String, List<Rule>> rules, Map<String, List<List<Object>>> facts)
	{
		super();
		this.rules = rules;
		this.facts = facts;
		this.tracer = new NopTracer();
	}
	
	public void setTracer(TakmelogicTracer tracer) { this.tracer = tracer; }

	public void query(String name, List<Object> args, Consumer<List<Object>> resultFn)
	{
		this.resultFn = resultFn;
		this.k = new HashMap<>();
		this.s = new HashMap<>();
		this.kNew = new HashMap<>();
		this.sNew = new HashMap<>();

		tracer.reset();
		
		tracer.trInitialTopLevelCall();
		callTopLevel(name, args);
		tracer.trEndInitialTopLevelCall();

		Map<Call, Set<List<Object>>> scWorklist;
		Map<Call, Set<Cont>> kWorkList;
		
		int iteration = 0; // for debugging only!
		while (true)
		{
			scWorklist = Utils.mapSetDiff(sNew, s);
			kWorkList = Utils.mapSetDiff(kNew, k);
			
			tracer.trThisIterationsWorklists(scWorklist, kWorkList);
			
			merge(s, sNew);
			merge(k, kNew);
			
			sNew = new HashMap<>();
			kNew = new HashMap<>();

			awakenings.clear();
			
			boolean stop = true;
			if (scWorklist.size() != 0)
			{
				stop = false;
				
				for (Entry<Call, Set<List<Object>>> kv : scWorklist.entrySet())
				{
					Call k = kv.getKey();
					Set<List<Object>> ps = kv.getValue();
					for (List<Object> p : ps)
					{
						updateFromSuccess(k, p);
					}
				}
			}

			if (kWorkList.size() != 0)
			{
				stop = false;
				
				for (Entry<Call, Set<Cont>> kv : kWorkList.entrySet())
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
	}
	
	private void updateFromSuccess(Call succeededCall, List<Object> successTuple)
	{
		if(succeededCall.equals(rootCall))
		{
			this.resultFn.accept(successTuple);
		}

		Set<Cont> contList = k.getOrDefault(succeededCall, Utils.set());
		
		if(contList.isEmpty())
		{
			tracer.trNoContinuationsForNewSuccess(succeededCall, successTuple);
		}
		
		for (Cont cont : contList)
		{
			Pair<List<Object>, Cont> sk = new Pair<>(successTuple, cont);
			if (!awakenings.contains(sk))
			{
				awakenings.add(sk);
				
				List<Term> justSucceededArgs = getCallArgs(cont.CodePos.rule.SubGoals.get(cont.CodePos.sgNum-1));
				
				Box<Map<String, Object>> newEnv = new Box<>(null);
				if(applyBindings(cont.BindingsSoFar, successTuple, justSucceededArgs, newEnv))
				{
					tracer.trJoinFromSuccess(succeededCall, successTuple, cont, newEnv.Value);
					this.process(cont.Call, cont.CodePos, newEnv.Value);
					tracer.trEndJoinFromSuccess(succeededCall, successTuple, cont, newEnv.Value);
				}
				else
				{
					tracer.trJoinFromSuccessFailedBinding(succeededCall, successTuple, cont);
				}
			}
		}
	}
	
	private void updateFromNewCall(Call newCall, Cont cont)
	{
		Set<List<Object>> sc = s.getOrDefault(newCall, new HashSet<>());
		
		if(sc.isEmpty())
		{
			tracer.trNoSuccessesForNewCall(newCall, cont);
		}
		
		for (List<Object> s : sc)
		{
			Pair<List<Object>, Cont> sk = new Pair<>(s, cont);
			if (!awakenings.contains(sk))
			{
				awakenings.add(sk);
				
				List<Term> justSucceededArgs = getCallArgs(cont.CodePos.rule.SubGoals.get(cont.CodePos.sgNum-1));
				
				Box<Map<String, Object>> newEnv = new Box<>(null);
				if(applyBindings(cont.BindingsSoFar, s, justSucceededArgs, newEnv))
				{
					tracer.trJoinFromNewCall(newCall, s, cont, newEnv.Value);
					this.process(cont.Call, cont.CodePos, newEnv.Value);
					tracer.trEndJoinFromNewCall(newCall, s, cont, newEnv.Value);
				}
				else
				{
					tracer.trJoinFromNewCallFailedBinding(newCall, s, cont);	
				}
			}
		}
	}

	private void processRule(Call callee)
	{
		String execRuleName = callee.Rule;
		
		List<Object> args = callee.Args;
		
		List<Rule> r = rules.get(execRuleName);
		
		if(r == null)
		{
			throw new RuntimeException("Rule '" + execRuleName + " doesn't exist");
		}
		
		for (int i = 0; i < r.size(); ++i)
		{
			Box<Map<String, Object>> env = new Box<>(null);
			if(bindHeadArgs(r.get(i), args, env))
			{
				process(new Call(execRuleName, args), new CodePos(r.get(i), 0), env.Value);
			}
		}
	}
	
	private void process(Call execRule, 
			CodePos seq,
			Map<String, Object> bindingsSoFar 
			)
	{
		tracer.trEnterProcess(execRule, seq, bindingsSoFar);
		
		boolean stop = false;
		Map<String,Object> env = bindingsSoFar;
		
		while(seq.sgNum < seq.rule.SubGoals.size())
		{
			SubGoal _e = seq.rule.SubGoals.get(seq.sgNum);
			seq = seq.next();
			
			if(_e instanceof takmelogic.ast.Call)
			{
				takmelogic.ast.Call e = (takmelogic.ast.Call) _e;
				if(rules.containsKey(e.Calling.Name))
				{
					// Call as a rule
					String calleeName = e.Calling.Name;
					List<Object> args = InterpreterTools.unboxTermsForCall(e.Calling.Parts, env);
					Call callee = new Call(calleeName, args);
					
					call(execRule, callee, seq, env);
					stop = true;
					break;
				}
				else
				{
					// Call as a fact (in our simplistic implementation, absence from the facts DB implies
					// a fact that has no tuples in the DB).
					String calleeName = e.Calling.Name;
					List<Object> args = InterpreterTools.unboxTermsForCall(e.Calling.Parts, env);
					Call callee = new Call(calleeName, args);
					callAsFact(execRule, callee, seq, env);
					stop = true;
					break;
				}
			}
			else if(_e instanceof takmelogic.ast.IsEqual)
			{
				// Not a Prolog-style unification, just testing the equality
				// of two ground values!
				takmelogic.ast.IsEqual e = (takmelogic.ast.IsEqual) _e;
				
				Object _a = InterpreterTools.resolvedTerm(e.A, env); 
				Object _b = InterpreterTools.resolvedTerm(e.B, env);
				
				boolean comparisonSuccess = _a.equals(_b);			
				tracer.trEquals(e.A, e.B, comparisonSuccess, env);
				
				if(!comparisonSuccess)
				{
					stop = true;
					break;
				}
			}
			else if(_e instanceof takmelogic.ast.Comparison)
			{
				takmelogic.ast.Comparison e = (takmelogic.ast.Comparison) _e;
				
				Object _a = InterpreterTools.resolvedTerm(e.A, env); 
				Object _b = InterpreterTools.resolvedTerm(e.B, env);

				boolean comparisonSuccess = compare(e.Op, _a, _b);
				tracer.trComparison(e.A, e.B, e.Op, comparisonSuccess, env);
				
				if(!comparisonSuccess)
				{
					stop = true;
					break;
				}
			}
			else if(_e instanceof takmelogic.ast.NotEqual)
			{
				takmelogic.ast.NotEqual e = (takmelogic.ast.NotEqual) _e;
				Object _a = InterpreterTools.resolvedTerm(e.A, env); 
				Object _b = InterpreterTools.resolvedTerm(e.B, env);
				
				boolean comparisonSuccess = !_a.equals(_b);			
				tracer.trNotEquals(e.A, e.B, comparisonSuccess, env);
				
				if(!comparisonSuccess)
				{
					stop = true;
					break;
				}
			}
			else
			{
				throw new RuntimeException("Option not handled " + _e);
			}
		} // while
		tracer.trExitProcess(execRule);
		if (!stop)
		{
			// Reached end of the rule with success
			List<Object> head = InterpreterTools.resolvedTerms(seq.rule.Head.Parts, env);
			doReturn(execRule, head);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean compare(ComparisonOp op, Object _a, Object _b)
	{
		Comparable aa = (Comparable) _a;
		if(op == ComparisonOp.Ge)
		{
			return aa.compareTo(_b) >= 0;
		}
		else if(op == ComparisonOp.Gt)
		{
			return aa.compareTo(_b) > 0;
		}
		if(op == ComparisonOp.Le)
		{
			return aa.compareTo(_b) <= 0;
		}
		if(op == ComparisonOp.Lt)
		{
			return aa.compareTo(_b) < 0;
		}
		else
		{
			throw new RuntimeException("Option not handled: " + op);
		}
	}

	private void call(Call caller, Call callee, CodePos codePos, Map<String, Object> envSoFar)
	{
		Cont cont = new Cont(caller, codePos, envSoFar);
		boolean firstCall = checkFirstCall(callee);
		Utils.addMapSet(this.kNew, callee, cont);
		
		tracer.trCall(callee, caller, cont, firstCall);
		
		if(firstCall)
		{
			processRule(callee);
		}
		
		tracer.trEndCall(callee, caller, cont, firstCall);
	}

	private boolean checkFirstCall(Call callee)
	{
		boolean firstCall = !k.containsKey(callee) && !kNew.containsKey(callee);
		return firstCall;
	}
	
	private void callTopLevel(String startSymbol, List<Object> args)
	{
		List<Rule> r = rules.get(startSymbol);
		if(r == null) { callTopLevelAsFact(startSymbol, args); /* If it's not in the top-level rules, maybe it's a fact?*/ }
		else          { callTopLevelAsRule(startSymbol, args, r); }
	}

	public void callTopLevelAsRule(String startSymbol, List<Object> args, List<Rule> rulesAlts)
	{
		Call call = new Call(startSymbol, args);
		Cont rootC = mkRootCont(call);
		rootCall = call;
		tracer.trSetRootCall(call);
		
		Utils.addMapSet(this.k, call, rootC);

		tracer.trCallTopLevel(call);
		for (int i = 0; i < rulesAlts.size(); ++i)
		{
			Box<Map<String, Object>> env = new Box<>(null);
			boolean match = bindHeadArgs(rulesAlts.get(i), args, env); 
			if(match)
			{
				process(call, new CodePos(rulesAlts.get(i), 0), env.Value);
			}
		}
		tracer.trEndCallTopLevel(call);
	}

	private Cont mkRootCont(Call call)
	{
		// Create an AST representing the root call
		// so that it can be processed and added to successes
		Rule ast = new Rule(new Atom("Root", InterpreterTools.boxValues(call.Args)), Utils.list(new takmelogic.ast.Call(
				new Atom(call.Rule, InterpreterTools.boxValues(call.Args)))));

		// So that our root continuation can have a CodePos
		// pretty hackish right?
		// Also make the CodePos have sgNum = 1, since we've already 'called' our goal from rootC
		return new Cont(new Call("Root", new ArrayList<>()), 
				new CodePos(ast, 1), new HashMap<>());
	}

	public void callTopLevelAsFact(String startSymbol, List<Object> args)
	{
		List<List<Object>> fs = facts.get(startSymbol);
		if(fs != null)
		{
			for(List<Object> row : fs)
			{
				if(this.matchRowToQuery(row, args))
				{
					this.resultFn.accept(row);
				}
			}				
		}
		else
		{
			//throw new RuntimeException("Did not find start symbol '" + startSymbol + "' in grammar");
			// For now treat it as an empty fact
			return;
		}
	}

	private void callAsFact(Call caller, Call callee, CodePos codePos, Map<String, Object> envSoFar)
	{
		
		Cont cont = new Cont(caller, codePos, envSoFar);
		Utils.addMapSet(this.kNew, callee, cont);
		
		tracer.trCallAsFact(callee, caller, cont);
		
		List<List<Object>> _rows = facts.getOrDefault(callee.Rule, new ArrayList<>());
		processFactQuery(callee, _rows);
		
		tracer.trEndCallAsFact(callee, caller, cont);
	}
	
	private void doReturn(Call successfull, List<Object> successTuple)
	{
		Utils.addMapSet(sNew, successfull, successTuple);
		tracer.trSuccess(successfull, successTuple);
	}
	
	private void doReturnFact(Call successfull, List<Object> successTuple)
	{
		// Should be identical to doReturn(), but with a different trace
		Utils.addMapSet(sNew, successfull, successTuple);
		tracer.trSuccessFact(successfull, successTuple);
	}
	
	private void processFactQuery(Call callee, Iterable<List<Object>> rows)
	{
		List<Object> args = callee.Args;
		
		for(List<Object> row : rows)
		{
			if(matchRowToQuery(row, args))
			{
				doReturnFact(callee, row);
			}
		}
	}
	
	private boolean matchRowToQuery(List<Object> row, List<Object> args)
	{
		for(int i=0; i<args.size(); ++i)
		{
			if(args.get(i) == null)
			{
				continue;
			}
			else if(!args.get(i).equals(row.get(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	private boolean bindHeadArgs(Rule rule, List<Object> args, Box<Map<String,Object>> outEnv)
	{
		Map<String, Object> env = new HashMap<>();
		boolean result = bindTerms(rule.Head.Parts, args, env);
		if(result)
		{
			outEnv.Value = env;
		}
		return result;
	}
	
	private boolean bindTerms(List<Term> terms, List<Object> args, Map<String,Object> env)
	{
		for(int i=0; i<args.size(); ++i)
		{
			Object arg = args.get(i);
			if(arg == null)
			{
				continue;
			}
			
			Term _p = terms.get(i);
			
		
			if(_p instanceof Int)
			{
				Int p = (Int) _p;
				if(!(arg instanceof Integer) || ((Integer)arg) != p.V)
				{
					return false;
				}
			}
			else if(_p instanceof Symbol)
			{
				Symbol p = (Symbol) _p;
				if(!(arg instanceof S) || !((S) arg).Value.equals(p.V))
				{
					return false;
				}
			}
			else if(_p instanceof Str)
			{
				Str p = (Str) _p;
				if(!(arg instanceof String) || !((String)arg).equals(p.V))
				{
					return false;
				}
			}
			else if(_p instanceof Var)
			{
				Var p = (Var) _p;
				Object val;
				if(env.containsKey(p.N))
				{
					val = env.get(p.N);
					return arg.equals(val);
				}
				else
				{
					val = arg;
					env.put(p.N, val);
				}
			}
			else
			{
				throw new RuntimeException("Option not handled " + _p);
			}
		}
		return true;
	}
	
	private boolean applyBindings(Map<String, Object> bindingsSoFar, List<Object> successTuple, List<Term> calleeArgs, Box<Map<String, Object>> newEnv)
	{
		Map<String, Object> newBind = new HashMap<>(bindingsSoFar);
		int i=0;
		for(Term _t : calleeArgs)
		{
			Object a = successTuple.get(i);
			boolean res = applyBindings(newBind, bindingsSoFar, _t, a);
			if(!res) { return false; }
			++i;
		}
		newEnv.Value = newBind;
		return true;
	}
	
	private boolean applyBindings(Map<String, Object> newBind, Map<String, Object> bindingsSoFar, Term _t, Object a)
	{
		if(_t instanceof Var)
		{
			Var v = (Var) _t;
			if(v.N.equals("_"))
			{
				// TODO: this might not be completely correct
	            // we should instead apply a transformation to replace all _ with fresh vars
				return true;
			}
			else if(newBind.containsKey(v.N))
			{
				Object b = newBind.get(v.N);
				if(!a.equals(b))
				{
					return false;
				}
			}
			else
			{
				newBind.put(v.N, a);
			}
		}
		
		return true;
	}

	private List<Term> getCallArgs(SubGoal _sg)
	{
		if(_sg instanceof takmelogic.ast.Call)
		{
			takmelogic.ast.Call sg = (takmelogic.ast.Call) _sg;
			return sg.Calling.Parts;
		}
		else
		{
			throw new RuntimeException("Option not handled " + _sg);
		}
	}
	
	private <Tk, Tv> void merge(Map<Tk, Set<Tv>> map1, Map<Tk, Set<Tv>> map2)
	{
		for (Entry<Tk, Set<Tv>> kv : map2.entrySet())
		{
			for (Tv val : kv.getValue())
			{
				Utils.addMapSet(map1, kv.getKey(), val);
			}
		}
	}
}
