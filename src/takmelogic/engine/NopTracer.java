package takmelogic.engine;

import java.util.List;
import java.util.Map;
import java.util.Set;

import takmelogic.ast.ComparisonOp;
import takmelogic.ast.Term;

public class NopTracer implements TakmelogicTracer
{
	@Override public void reset()
	{
	}

	@Override public void trCall(Call callee, Call caller, Cont cont, boolean firstCall)
	{
	}

	@Override public void trExitProcess(Call execRule)
	{
	}

	@Override public void trJoinFromNewCall(Call callee, List<Object> s, Cont cont, Map<String,Object> newEnv)
	{
	}

	@Override public void trEndJoinFromNewCall(Call callee, List<Object> s, Cont cont, Map<String,Object> newEnv)
	{
	}

	@Override public void trJoinFromSuccess(Call succeeded, List<Object> s, Cont cont, Map<String,Object> newEnv)
	{
	}

	@Override public void trEndJoinFromSuccess(Call succeeded, List<Object> s, Cont cont, Map<String,Object> newEnv)
	{
	}

	@Override public void trCallTopLevel(Call callee)
	{
	}

	@Override public void trEnterProcess(Call execRule, CodePos seq, Map<String, Object> bindingsSoFar)
	{
	}

	@Override public void trSuccess(Call exec, List<Object> s)
	{
	}

	@Override public void trNextIteration()
	{
	}

	@Override public void trReachedFixedPoint()
	{
	}

	@Override public void trEndCall(Call callee, Call caller, Cont cont, boolean firstCall)
	{
	}

	@Override public void trEndCallTopLevel(Call callee)
	{
	}

	@Override public void trInitialTopLevelCall()
	{
	}

	@Override public void trEndInitialTopLevelCall()
	{
	}

	@Override public void trThisIterationsWorklists(Map<Call, Set<List<Object>>> scWorklist,
			Map<Call, Set<Cont>> kWorklist)
	{
	}

	@Override public void trCallAsFact(Call callee, Call caller, Cont cont)
	{
	}

	@Override public void trEndCallAsFact(Call callee, Call caller, Cont cont)
	{
	}

	@Override public void trSuccessFact(Call successfull, List<Object> successTuple)
	{
	}

	@Override public void trJoinFromNewCallFailedBinding(Call newCall, List<Object> s, Cont cont)
	{
	}

	@Override public void trJoinFromSuccessFailedBinding(Call succeededCall, List<Object> successTuple, Cont cont)
	{
	}
	@Override public void trSetRootCall(Call rootCall)
	{
	}

	@Override public void trNoContinuationsForNewSuccess(Call succeededCall, List<Object> successTuple)
	{
	}

	@Override public void trNoSuccessesForNewCall(Call newCall, Cont cont)
	{
	}

	@Override public void trEquals(Term a, Term b, boolean comparisonSuccess, Map<String, Object> env)
	{
	}

	@Override public void trNotEquals(Term a, Term b, boolean comparisonSuccess, Map<String, Object> env)
	{
	}

	@Override public void trComparison(Term a, Term b, ComparisonOp op, boolean comparisonSuccess, Map<String, Object> env)
	{
	}
}
