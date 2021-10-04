package takmela.engine;

import java.util.List;
import java.util.Map;
import java.util.Set;

import takmela.tree.Node;
import takmela.tree.Treeish;
import utils_takmela.Pair;

public class NopTracer implements TakmelaTracer
{
	@Override
	public final void trSuccessfulParse()
	{
	}

	@Override
	public final void trMatch(Call execRule, CodePos seq, String value, String tok, boolean succeed, int inputPosNow)
	{
	}

	@Override
	public final void trEndJoinFromNewCall(Call callee, int s, Cont cont)
	{
	}

	@Override
	public final void trJoinFromNewCall(Call callee, int s, Cont cont, List<Treeish> ts)
	{
	}

	@Override
	public final void trEndJoinFromSuccess(Call succeeded, int inputPosAfterSuccess, List<Treeish> successTrees, Cont cont)
	{
	}

	@Override
	public final void trJoinFromSuccess(Call succeeded, int inputPosAfterSuccess, Set<Treeish> successTrees, Cont cont)
	{
	}

	@Override
	public final void trExitProcess(Call execRule)
	{
	}

	@Override
	public final void trEnterProcess(Call execRule, CodePos seq, int inputPos)
	{
	}

	@Override
	public final void trSuccess(Call exec, int inputPosNow, Node tree)
	{
	}

	@Override
	public final void trCall(Call callee, Call caller, Cont cont, boolean firstCall)
	{
	}

	@Override
	public final void trCallTopLevel(Call callee, Cont rootC)
	{
	}

	@Override public final void reset()
	{
		
	}

	@Override public void trNextIteration()
	{
	}

	@Override public void trEndCallTopLevel(Call callee, Cont rootC)
	{
	}

	@Override public void trEndCall(Call callee, Call caller, Cont cont, boolean firstCall)
	{
	}

	@Override public void trNotSuccessfulParse()
	{
	}

	@Override public void trReachedFixedPoint()
	{
	}

	@Override public void trThisIterationsWorklists(Map<Call, Set<Integer>> scWorklist, Map<Call, Set<Cont>> kWorklist,
			Map<Pair<Call, Integer>, List<Treeish>> successfulCallTrees)
	{
	}

	@Override public void trInitialTopLevelCall()
	{
	}

	@Override public void trEndInitialTopLevelCall()
	{
	}

	@Override public void trNoSuccessesForNewCall(Call newCall, Cont cont)
	{
	}

	@Override public void trNoContinuationsForNewSuccess(Call succeededCall, Pair<Integer, List<Treeish>> posAndTree)
	{
	}
}
