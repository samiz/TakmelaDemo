package takmelogic.engine;

import java.util.List;
import java.util.Map;
import java.util.Set;

import takmelogic.ast.ComparisonOp;
import takmelogic.ast.Term;

public interface TakmelogicTracer
{
	void reset();

	void trSetRootCall(Call rootCall);
	void trInitialTopLevelCall();
	void trEndInitialTopLevelCall();
	
	void trNextIteration();
	void trReachedFixedPoint();
	void trThisIterationsWorklists(Map<Call, Set<List<Object>>> scWorklist,
			Map<Call, Set<Cont>> kWorklist);
	
	void trCall(Call callee, Call caller, Cont cont, boolean firstCall);
	void trEndCall(Call callee, Call caller, Cont cont, boolean firstCall);

	void trCallAsFact(Call callee, Call caller, Cont cont);
	void trEndCallAsFact(Call callee, Call caller, Cont cont);
	
	void trCallTopLevel(Call callee);
	void trEndCallTopLevel(Call callee);
	
	void trEquals(Term a, Term b, boolean comparisonSuccess, Map<String, Object> env);
	void trNotEquals(Term a, Term b, boolean comparisonSuccess, Map<String, Object> env);
	void trComparison(Term a, Term b, ComparisonOp op, boolean comparisonSuccess, Map<String, Object> env);

	void trEnterProcess(Call execRule, CodePos seq, Map<String, Object> bindingsSoFar);
	void trExitProcess(Call execRule);
	void trSuccess(Call exec, List<Object> s);
	void trSuccessFact(Call successfull, List<Object> successTuple);
	
	void trJoinFromNewCall(Call callee, List<Object> s, Cont cont, Map<String,Object> newEnv);
	void trEndJoinFromNewCall(Call callee, List<Object> s, Cont cont, Map<String,Object> newEnv);
	void trJoinFromNewCallFailedBinding(Call newCall, List<Object> s, Cont cont);
	
	void trJoinFromSuccess(Call succeeded, List<Object> s, Cont cont, Map<String,Object> newEnv);
	void trEndJoinFromSuccess(Call succeeded, List<Object> s, Cont cont, Map<String,Object> newEnv);
	void trJoinFromSuccessFailedBinding(Call succeededCall, List<Object> successTuple, Cont cont);

	void trNoContinuationsForNewSuccess(Call succeededCall, List<Object> successTuple);
	void trNoSuccessesForNewCall(Call newCall, Cont cont);
}
