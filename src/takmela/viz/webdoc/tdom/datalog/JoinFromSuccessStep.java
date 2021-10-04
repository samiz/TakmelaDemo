package takmela.viz.webdoc.tdom.datalog;

import java.util.List;
import java.util.Map;

import takmelogic.engine.Call;
import takmelogic.engine.Cont;

public class JoinFromSuccessStep extends Step
{
	public Call Succeeded;
	public Cont Cont;
	public List<Object> S;
	public Map<String, Object> NewBindings;
	
	public JoinFromSuccessStep(Call succeeded, List<Object> s, Cont cont, Map<String, Object> newBindings)
	{
		Succeeded = succeeded;
		Cont = cont;
		S = s;
		NewBindings = newBindings;
	}

	@Override public String getDescription()
	{
		return String.format("Join: %s  → %s will resume cont: %s", Succeeded, S, Cont);
	}
}
