package takmela.viz.webdoc.tdom.parser;

import java.util.Set;

import takmela.engine.Call;
import takmela.engine.Cont;
import takmela.tree.Node;

public class JoinFromSuccessStep extends Step
{
	public Call Succeeded;
	public int InputPosAfterSuccess;
	public Set<Node> SuccessTrees;
	
	public Cont Cont;
	
	public JoinFromSuccessStep(Call succeeded, int s, Set<Node> ts, Cont cont)
	{
		Succeeded = succeeded;
		InputPosAfterSuccess = s;
		SuccessTrees = ts;
		Cont = cont;
	}

	@Override public String getDescription()
	{
		return String.format("Join: %s  → %s will resume cont: %s", Succeeded, InputPosAfterSuccess, Cont);
	}
}
