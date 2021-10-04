package takmela.engine;

import java.util.List;
import java.util.Map;
import java.util.Set;

import takmela.tree.Node;
import takmela.tree.Treeish;
import utils_takmela.Pair;

public interface TakmelaTracer
{
	void reset();

	void trInitialTopLevelCall();
	void trEndInitialTopLevelCall();
	
	void trNextIteration();
	void trReachedFixedPoint();
	void trThisIterationsWorklists(Map<Call, Set<Integer>> scWorklist, Map<Call, Set<Cont>> kWorklist, Map<Pair<Call, Integer>, List<Treeish>> successfulCallTrees);
	
	void trCall(Call callee, Call caller, Cont cont, boolean firstCall);
	void trEndCall(Call callee, Call caller, Cont cont, boolean firstCall);
	
	void trCallTopLevel(Call callee, Cont rootC);
	void trEndCallTopLevel(takmela.engine.Call callee, takmela.engine.Cont rootC);
	
	void trSuccessfulParse();
	void trNotSuccessfulParse();
	void trMatch(Call execRule, CodePos seq, String value, String tok, boolean succeed, int inputPosNow);

	void trEnterProcess(Call execRule, CodePos seq, int inputPos);
	void trExitProcess(Call execRule);
	void trSuccess(Call exec, int inputPosNow, Node tree);

	void trJoinFromNewCall(Call callee, int s, Cont cont, List<Treeish> ts);
	void trEndJoinFromNewCall(Call callee, int s, Cont cont);
	
	void trJoinFromSuccess(Call succeeded, int inputPosAfterSuccess, Set<Treeish> successTrees, Cont cont);
	void trEndJoinFromSuccess(Call succeeded, int inputPosAfterSuccess, List<Treeish> successTrees, Cont cont);
	
	void trNoContinuationsForNewSuccess(Call succeededCall, Pair<Integer, List<Treeish>> posAndTree);
	void trNoSuccessesForNewCall(Call newCall, Cont cont);
}
