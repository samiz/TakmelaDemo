package takmela.viz.webdoc.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import takmela.engine.Call;
import takmela.engine.Cont;
import takmela.engine.TakmelaTracer;
import takmela.tree.Node;
import takmela.tree.Treeish;
import takmela.viz.GradualGraphBuilder;
import takmela.viz.TraceUtils;
import takmela.viz.webdoc.tdom.parser.Iteration;
import takmela.viz.webdoc.tdom.parser.JoinFromSuccessStep;
import takmela.viz.webdoc.tdom.parser.JoinFromNewCallStep;
import takmela.viz.webdoc.tdom.parser.Processing;
import takmela.viz.webdoc.tdom.parser.ProcessingOwner;
import takmela.viz.webdoc.tdom.parser.Step;
import takmela.viz.webdoc.tdom.parser.StepStatus;
import takmela.viz.webdoc.tdom.parser.Success;
import takmela.viz.webdoc.tdom.parser.TopLevelCallStep;
import utils_takmela.Pair;
import utils_takmela.Utils;

public class WebDocTracer implements TakmelaTracer
{
	/*
	We want to generate a web document that follows Takmela's algorithm. It's laid out like this:
	
	1- We will visualize each iteration, inside an iteration the unit of tracing will be a {Top level call} or a {join}.
	2- Inside either of those, we will have process(..) tree; i.e process(callA) lead to process(callB) and callC; etc
	3- Each tree of process(..) will have one graph where all successes and new calls/continuations are shown in red
	4- Inside an individual process(..) we will have our usual steps; match(X) with {success, fail}, or call, or the like
	5- And within those steps, we'll be careful to show the current input position
	
	+--------------------------------------------------+
	| Iteration 1                                      |
	|                                                  |
	+--------------------------------------------------+
	|   Graphy    |  Call topLevel  e/0                |
	|   Graphy    |     call t/0 [will process]        |
	|   Graphy    |         call f/0 [will process]    |
	|             |            match `1` success       |
	|             |            f/0 succeded pos=1      |
	|-------------|            return to t/0           |
	|  successes  |            ....                    |
	+--------------------------------------------------+
	| Iteration 2                                      |
	|                                                  |
	+--------------------------------------------------+	
			
			
    In order to achieve this amazing visualization, we will describe it 
    in a model before rendering it HTML; so we'll have an abstract
    'visualization DOM'.
    
          Iteration -> *step (CallTopLevel or Join) -> processing tree -> processing operation (call, match, success, return, ...)

	This tracer's job will be to construct this DOM from the various events it receives; after that a renderer will
	take the abstract vizDOM and render it to HTML, SVG, etc
    */
	
	// Implementation note/warning: This class frequently uses the 'successCallTrees' from the parse engine, which
	// is a mutable data structure; remember to copy any Collection<Treeish> you get from there and only store the copy
	// or it'll wreak havoc with program logic.
	// But then why don't we pass a copy to here in the first place?
	// 	1- Due to performance; a parser should not make expensive copies that could be passed to e.g a NopTracer
	//  2- In general the parser should be completely oblivious to anything but parsing anyway 
	
	public List<Iteration> Iterations;
	public Iteration InitialTopLevelCalls;
	
	private Iteration currentIteration;
	private Step currentStep;
	private Stack<ProcessingOwner> currentProcessingStack;
	private Processing currentProcessing;
	private GradualGraphBuilder graph;
	private boolean showFullContinuationOnGraphEdges;
	
	public WebDocTracer(boolean showFullContinuationOnGraphEdges) 
	{
		this.showFullContinuationOnGraphEdges = showFullContinuationOnGraphEdges;
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

	@Override public void trInitialTopLevelCall()
	{
		// Don't call iterations.add(..) since the first iteration is the top-level calls
		// which is displayed separately
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
	
	@Override public void trThisIterationsWorklists(Map<takmela.engine.Call, Set<Integer>> scWorklist,
			Map<takmela.engine.Call, Set<Cont>> kWorklist, Map<Pair<takmela.engine.Call, Integer>, List<Treeish>> successfulCallTrees)
	{
		currentIteration.SuccessWorklist = toSuccessWorkList(scWorklist, successfulCallTrees);
		currentIteration.KWorkList = Utils.copyMapSet(kWorklist);
	}

	private Map<Call, List<Pair<Integer, Set<Treeish>>>> toSuccessWorkList(
			Map<takmela.engine.Call, Set<Integer>> scWorklist,
			Map<Pair<takmela.engine.Call, Integer>, List<Treeish>> successfulCallTrees)
	{
		Map<takmela.engine.Call, List<Pair<Integer, Set<Treeish>>>> result = new HashMap<>();
		for(Entry<takmela.engine.Call, Set<Integer>> kv : scWorklist.entrySet())
		{
			takmela.engine.Call call = kv.getKey();
			Set<Integer> successes = kv.getValue();
			List<Pair<Integer, Set<Treeish>>> successesWithTrees = new ArrayList<>();
			for(int sPos : successes)
			{
				List<Treeish> trees = Utils.mustGet(successfulCallTrees, new Pair<>(call, sPos));
				Set<Treeish> trees2 = Utils.set(trees);
				successesWithTrees.add(new Pair<>(sPos, trees2));
			}
			result.put(call, successesWithTrees);
		}
		return result;
	}

	@Override public void trEnterProcess(takmela.engine.Call execRule, takmela.engine.CodePos seq, int inputPos)
	{
		currentProcessing = new Processing(execRule, seq, inputPos);
		currentProcessingOwner().addProcessing(currentProcessing);
	}

	@Override public void trExitProcess(takmela.engine.Call execRule)
	{
		
	}

	@Override public void trCallTopLevel(takmela.engine.Call callee, takmela.engine.Cont rootC)
	{
		currentStep = new TopLevelCallStep(callee, rootC);
		currentIteration.Steps.add(currentStep);
		this.graph.node(callee.toString());
		
		currentProcessingStack.push(currentStep);
	}
	
	@Override public void trEndCallTopLevel(takmela.engine.Call callee, takmela.engine.Cont rootC)
	{
		popProcessing();
		currentStep = null;
		graph.next();
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
	
	@Override public void trCall(takmela.engine.Call callee, takmela.engine.Call caller, takmela.engine.Cont cont,
			boolean firstCall)
	{
		this.graph.node(callee.toString());
		this.graph.edge(callee.toString(), caller.toString(), TraceUtils.formatKForGraphEdge(cont, showFullContinuationOnGraphEdges));
		
		currentStep.addNewCont(callee, cont);
		
		takmela.viz.webdoc.tdom.parser.Call call = new takmela.viz.webdoc.tdom.parser.Call(callee, cont, firstCall);
		
		currentProcessing.Operations.add(call);
		
		if(firstCall)
		{
			currentProcessingStack.push(call);
		}
	}
	
	@Override public void trEndCall(takmela.engine.Call callee, takmela.engine.Call caller, takmela.engine.Cont cont,
			boolean firstCall)
	{
		if(firstCall)
		{
			popProcessing();
		}
	}
	
	@Override public void trJoinFromSuccess(takmela.engine.Call succeeded, int inputPosAfterSuccess,
			Set<Treeish> successTrees,
			takmela.engine.Cont cont)
	{
		currentStep = new JoinFromSuccessStep(succeeded, inputPosAfterSuccess, Utils.set(successTrees), cont);
		currentIteration.Steps.add(currentStep);	
		
		currentProcessingStack.push(currentStep);
		
		currentIteration.ActiveSuccesses.add(new Pair<>(succeeded, new Pair<>(inputPosAfterSuccess, Utils.set(successTrees))));
		currentIteration.UsedNewCalls.add(new Pair<>(succeeded, cont));
	}

	@Override public void trEndJoinFromSuccess(takmela.engine.Call succeeded, int inputPosAfterSuccess,
			List<Treeish> successTrees,
			takmela.engine.Cont cont)
	{
		popProcessing();
		currentStep = null;	
		graph.next();
	}
	
	@Override public void trJoinFromNewCall(takmela.engine.Call callee, int s, takmela.engine.Cont cont, List<Treeish> ts)
	{
		currentStep = new JoinFromNewCallStep(callee, s, cont, Utils.set(ts));
		currentIteration.Steps.add(currentStep);
		
		currentProcessingStack.push(currentStep);
		
		currentIteration.ActiveNewCalls.add(new Pair<>(callee, cont));
		currentIteration.UsedSuccesses.add(new Pair<>(callee, new Pair<>(s, Utils.set(ts))));
	}

	@Override public void trEndJoinFromNewCall(takmela.engine.Call callee, int s, takmela.engine.Cont cont)
	{
		popProcessing();
		currentStep = null;
		graph.next();
	}
	
	@Override public void trNoContinuationsForNewSuccess(Call succeededCall, Pair<Integer, List<Treeish>> posAndTree)
	{
		currentIteration.IdleSuccesses.add(new Pair<>(succeededCall, 
				new Pair<>(posAndTree.a, Utils.set(posAndTree.b))));
	}

	@Override public void trNoSuccessesForNewCall(Call newCall, Cont cont)
	{
		currentIteration.IdleNewCalls.add(new Pair<>(newCall, cont));
	}
	
	@Override public void trMatch(takmela.engine.Call execRule, takmela.engine.CodePos seq, String value, String tok,
			boolean succeed, int inputPosNow)
	{
		currentProcessing.Operations.add(new takmela.viz.webdoc.tdom.parser.Match(tok, value, succeed, inputPosNow));
	}
	
	@Override public void trSuccess(takmela.engine.Call exec, int inputPosNow, Node tree)
	{
		boolean root = exec.Callee.equals("Root");
		Success s = new takmela.viz.webdoc.tdom.parser.Success(exec, inputPosNow, tree, root);
		currentProcessing.Operations.add(s);
		currentStep.addSuccess(s);
	}
	
	@Override public void trSuccessfulParse()
	{
		currentStep.setStatus(StepStatus.ResumingRootSuccess);
	}
	
	@Override public void trNotSuccessfulParse()
	{
		currentStep.setStatus(StepStatus.ResumingRootNoSuccess);		
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
