package takmela.viz.webdoc.tdom.datalog;

import java.util.List;
import takmelogic.engine.Call;
import takmelogic.engine.Cont;

public class JoinFromSuccessFailedBindingStep extends Step
{
	public Call Succeeded;
	public Cont Cont;
	public List<Object> S;
	
	public JoinFromSuccessFailedBindingStep(Call succeeded, List<Object> s, Cont cont)
	{
		Succeeded = succeeded;
		Cont = cont;
		S = s;
	}

	@Override public String getDescription()
	{
		return String.format("Join: %s  → %s will resume cont: %s (failed to bind vars)", Succeeded, S, Cont);
	}
}
