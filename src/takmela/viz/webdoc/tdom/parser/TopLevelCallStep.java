package takmela.viz.webdoc.tdom.parser;

import takmela.engine.Call;
import takmela.engine.Cont;

public class TopLevelCallStep extends Step
{
	public Call Callee;
	public Cont RootC;
	
	public TopLevelCallStep(Call callee, Cont rootC)
	{
		this.Callee = callee;
		this.RootC = rootC;
	}

	@Override public String getDescription()
	{
		return "Called " + Callee + " at the top level (will process)";
	}
}
