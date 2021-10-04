package takmela.viz.webdoc.tdom.datalog;

import java.util.List;
import java.util.Map;

import takmelogic.engine.Call;
import takmelogic.engine.Cont;

public class JoinFromNewCallStep extends Step
{
	public Call Callee;
	public List<Object> S;
	public Cont Cont;
	public Map<String, Object> NewBindings;
	
	public JoinFromNewCallStep(Call callee, List<Object> s, Cont cont, Map<String, Object> newBindings)
	{
		Callee = callee;
		S = s;
		Cont = cont;
		NewBindings = newBindings;
	}

	@Override public String getDescription()
	{
		return String.format("Join: %s will use existing succeess → %s to resume cont: %s", 
				Callee, S, Cont);
	}
}
