package takmela.viz.webdoc.tdom.parser;

import java.util.Set;

import takmela.engine.Call;
import takmela.engine.Cont;
import takmela.tree.Node;

public class JoinFromNewCallStep extends Step
{
	public Call Callee;
	public int InputPosAfterSuccess;
	public Cont Cont;
	public  Set<Node> SuccessTrees;
	
	public JoinFromNewCallStep(Call callee, int s, Cont cont, Set<Node> successTrees)
	{
		Callee = callee;
		InputPosAfterSuccess = s;
		Cont = cont;
		SuccessTrees = successTrees;
	}

	@Override public String getDescription()
	{
		return String.format("Join: %s will use existing succeess → %s {%s} to resume cont: %s", 
				Callee, InputPosAfterSuccess, Cont, SuccessTrees);
	}
}
