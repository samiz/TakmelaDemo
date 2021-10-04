package takmela.viz.webdoc.tdom.datalog;

import java.util.List;
import takmelogic.engine.Call;
import takmelogic.engine.Cont;

public class JoinFromNewCallFailedBindingStep extends Step
{
	public Call Callee;
	public List<Object> S;
	public Cont Cont;
	
	public JoinFromNewCallFailedBindingStep(Call callee, List<Object> s, Cont cont)
	{
		Callee = callee;
		S = s;
		Cont = cont;
	}

	@Override public String getDescription()
	{
		return String.format("Join: %s will use existing succeess → %s to resume cont: %s (failed to bind vars)", 
				Callee, S, Cont);
	}
}
