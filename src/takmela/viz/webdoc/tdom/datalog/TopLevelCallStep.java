package takmela.viz.webdoc.tdom.datalog;

import takmelogic.engine.Call;
import takmela.engine.Cont;

public class TopLevelCallStep extends Step
{
	public Call Callee;
	public Cont RootC;
	
	public TopLevelCallStep(Call callee)
	{
		this.Callee = callee;
	}

	@Override public String getDescription()
	{
		return "Called " + Callee + " at the top level (will process)";
	}
}
