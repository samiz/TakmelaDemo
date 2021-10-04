package takmela.viz.webdoc.tdom.datalog;

import java.util.ArrayList;
import java.util.List;

public class Call implements ProcessingOp, ProcessingOwner
{
	public takmelogic.engine.Call Callee;
	public boolean WillProcess;
	public List<Processing> Processings;
	
	public Call(takmelogic.engine.Call callee, boolean willProcess)
	{
		super();
		Callee = callee;
		WillProcess = willProcess;
		Processings = new ArrayList<>();
	}

	@Override public void addProcessing(Processing p)
	{
		Processings.add(p);
	}

	@Override public List<Processing> getProcessings()
	{
		return Processings;
	}
}
