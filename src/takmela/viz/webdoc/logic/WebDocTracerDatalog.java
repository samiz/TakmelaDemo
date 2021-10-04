package takmela.viz.webdoc.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import takmelogic.ast.ComparisonOp;
import takmelogic.ast.Term;
import takmelogic.engine.Call;
import takmelogic.engine.CodePos;
import takmelogic.engine.Cont;
import takmelogic.engine.TakmelogicTracer;
import utils_takmela.Pair;
import utils_takmela.Utils;
import takmela.viz.GradualGraphBuilder;
import takmela.viz.TraceUtils;
import takmela.viz.webdoc.tdom.datalog.CallAsFact;
import takmela.viz.webdoc.tdom.datalog.Iteration;
import takmela.viz.webdoc.tdom.datalog.JoinFromNewCallFailedBindingStep;
import takmela.viz.webdoc.tdom.datalog.JoinFromNewCallStep;
import takmela.viz.webdoc.tdom.datalog.JoinFromSuccessFailedBindingStep;
import takmela.viz.webdoc.tdom.datalog.JoinFromSuccessStep;
import takmela.viz.webdoc.tdom.datalog.Processing;
import takmela.viz.webdoc.tdom.datalog.ProcessingOwner;
import takmela.viz.webdoc.tdom.datalog.Step;
import takmela.viz.webdoc.tdom.datalog.Success;
import takmela.viz.webdoc.tdom.datalog.SuccessFact;
import takmela.viz.webdoc.tdom.datalog.TopLevelCallStep;

public class WebDocTracerDatalog implements TakmelogicTracer
{
	/*

	We want to generate a web document that follows Takmela's algorithm. 
	We are building a data model, a 'TraceDom', that can be used to generate a trace HTML file

	For a detailed explanation, check out takmela/viz/webdoc/parsing/WebDocTracer.java 
	
    */
	
	public List<Iteration> Iterations;
	public Iteration InitialTopLevelCalls;
	
	private Iteration currentIteration;
	private Step currentStep;
	private Stack<ProcessingOwner> currentProcessingStack;
	private Processing currentProcessing;
	private GradualGraphBuilder graph;
	
	private boolean showFullContinuationsOnGraphEdges = true;
	
	public WebDocTracerDatalog(boolean showFullContinuationsOnGraphEdges)
	{
		this.showFullContinuationsOnGraphEdges = showFullContinuationsOnGraphEdges;
		reset();
	}

	public TraceDOM getTraceDom() { return new TraceDOM(InitialTopLevelCalls, Iterations, graph.build()); }
	
	@Override public void reset()
	{
		Iterations = new ArrayList<>();
		
		currentProcessingStack = new Stack<>();
		currentProcessing = null;
		currentStep = null;
		
		graph = new GradualGraphBuilder();
	}
	
	@Override public void trSetRootCall(Call rootCall)
	{
	}
	
	@Override public void trInitialTopLevelCall()
	{
		// Don't call iterations.add(..) since the first iteration is the top-level calls
		// which is displayed separately from the `iterations` list
		currentIteration = new Iteration();
	}

	@Override public void trEndInitialTopLevelCall()
	{
		InitialTopLevelCalls = currentIteration;
		currentIteration = new Iteration();
		Iterations.add(currentIteration);
	}
	
	@Override public void trNextIteration()
	{
		currentIteration = new Iteration();
		Iterations.add(currentIteration);
	}
	
	@Override public void trThisIterationsWorklists(Map<Call, Set<List<Object>>> scWorklist,
			Map<Call, Set<Cont>> kWorklist)
	{
		currentIteration.SuccessWorklist = Utils.copyMapSet(scWorklist);
		currentIteration.KWorkList = Utils.copyMapSet(kWorklist);
	}

	@Override public void trEnterProcess(Call execRule, CodePos seq, Map<String, Object> bindingsSoFar)
	{
		currentProcessing = new Processing(execRule, seq, bindingsSoFar);
		currentProcessingOwner().addProcessing(currentProcessing);
	}

	@Override public void trExitProcess(Call execRule)
	{
		
	}

	@Override public void trCallTopLevel(Call callee)
	{
		currentStep = new TopLevelCallStep(callee);
		currentIteration.Steps.add(currentStep);
		this.graph.node(callee.toString());
		
		currentProcessingStack.push(currentStep);
	}
	
	@Override public void trEndCallTopLevel(Call callee)
	{
		popProcessing();
		currentStep = null;
		graph.next();
	}
	
	@Override public void trCallAsFact(Call callee, Call caller, Cont cont)
	{
		currentProcessing.Operations.add(new CallAsFact(callee, caller, cont));
	}

	@Override public void trEndCallAsFact(Call callee, Call caller, Cont cont)
	{
		
	}
	
	private void popProcessing()
	{
		currentProcessingStack.pop();
		ProcessingOwner proc = currentProcessingOwner();
		if(proc != null)
		{
			currentProcessing = Utils.last(proc.getProcessings());
		}
		else
		{
			currentProcessing = null;
		}
	}
	
	@Override public void trCall(Call callee, Call caller, Cont cont, boolean firstCall)
	{
		this.graph.node(callee.toString());
		this.graph.edge(callee.toString(), caller.toString(), TraceUtils.formatK(cont, this.showFullContinuationsOnGraphEdges));
		
		takmela.viz.webdoc.tdom.datalog.Call call = new takmela.viz.webdoc.tdom.datalog.Call(callee, firstCall);
		
		currentProcessing.Operations.add(call);
		
		if(firstCall)
		{
			currentProcessingStack.push(call);
		}
	}
	
	@Override public void trEndCall(Call callee, Call caller, Cont cont, boolean firstCall)
	{
		if(firstCall)
		{
			popProcessing();
		}
	}
	
	@Override public void trEquals(Term a, Term b, boolean comparisonSuccess, Map<String, Object> env)
	{
		takmela.viz.webdoc.tdom.datalog.TestEq test = new takmela.viz.webdoc.tdom.datalog.TestEq(a, b, comparisonSuccess, env);
		currentProcessing.Operations.add(test);		
	}

	@Override public void trNotEquals(Term a, Term b, boolean comparisonSuccess, Map<String, Object> env)
	{
		takmela.viz.webdoc.tdom.datalog.TestNe test = new takmela.viz.webdoc.tdom.datalog.TestNe(a, b, comparisonSuccess, env);
		currentProcessing.Operations.add(test);
	}

	@Override public void trComparison(Term a, Term b, ComparisonOp op, boolean comparisonSuccess, Map<String, Object> env)
	{
		takmela.viz.webdoc.tdom.datalog.TestComp test = new takmela.viz.webdoc.tdom.datalog.TestComp(a, b, op, comparisonSuccess, env);
		currentProcessing.Operations.add(test);
	}
	
	@Override public void trJoinFromNewCall(Call callee, List<Object> s, Cont cont, Map<String,Object> newEnv)
	{
		currentStep = new JoinFromNewCallStep(callee, s, cont, Utils.diffByKeys(newEnv, cont.BindingsSoFar));
		currentIteration.Steps.add(currentStep);
		
		currentProcessingStack.push(currentStep);
		
		currentIteration.ActiveNewCalls.add(new Pair<>(callee, cont));
		currentIteration.UsedSuccesses.add(new Pair<>(callee, s));
	}

	@Override public void trEndJoinFromNewCall(Call callee, List<Object> s, Cont cont, Map<String,Object> newEnv)
	{
		popProcessing();
		currentStep = null;
		graph.next();
	}

	@Override public void trJoinFromSuccess(Call succeeded, List<Object> s, Cont cont, Map<String,Object> newEnv)
	{
		currentStep = new JoinFromSuccessStep(succeeded, s, cont, Utils.diffByKeys(newEnv, cont.BindingsSoFar));
		currentIteration.Steps.add(currentStep);	
		
		currentProcessingStack.push(currentStep);
		
		currentIteration.ActiveSuccesses.add(new Pair<>(succeeded, s));
		currentIteration.UsedNewCalls.add(new Pair<>(succeeded, cont));
	}

	@Override public void trEndJoinFromSuccess(Call succeeded, List<Object> s, Cont cont, Map<String,Object> newEnv)
	{
		popProcessing();
		currentStep = null;	
		graph.next();
	}
	
	@Override public void trNoContinuationsForNewSuccess(Call succeededCall, List<Object> successTuple)
	{
		currentIteration.IdleSuccesses.add(new Pair<>(succeededCall, successTuple));
	}

	@Override public void trNoSuccessesForNewCall(Call newCall, Cont cont)
	{
		currentIteration.IdleNewCalls.add(new Pair<>(newCall, cont));
	}

	@Override public void trJoinFromSuccessFailedBinding(Call succeededCall, List<Object> s, Cont cont)
	{
		currentStep = new JoinFromSuccessFailedBindingStep(succeededCall, s, cont);
		currentIteration.Steps.add(currentStep);
		
		currentIteration.FailedMatchSuccesses.add(new Pair<>(succeededCall, s));
	}
	
	@Override public void trJoinFromNewCallFailedBinding(Call newCall, List<Object> s, Cont cont)
	{
		currentStep = new JoinFromNewCallFailedBindingStep(newCall, s, cont);
		currentIteration.Steps.add(currentStep);
		
		currentIteration.FailedMatchNewCalls.add(new Pair<>(newCall, cont));
	}
	
	@Override public void trSuccess(Call exec, List<Object> s)
	{
		Success _s = new Success(exec, s);
		
		if(exec.Rule.equals("Root"))
		{
			_s.Root = true;
		}
		
		currentProcessing.Operations.add(_s);
		currentStep.addSuccess(_s);
	}
	
	@Override public void trSuccessFact(Call exec, List<Object> s)
	{
		SuccessFact _s = new SuccessFact(exec, s);
		currentProcessing.Operations.add(_s);
		currentStep.addSuccess(new Success(_s.Exec, _s.S));		
	}
	
	@Override public void trReachedFixedPoint()
	{
		currentIteration.ReachedFixedPoint = true;
	}
	
	private ProcessingOwner currentProcessingOwner()
	{
		if(currentProcessingStack.empty())
		{
			return null;
		}
		return currentProcessingStack.peek();
	}
}
