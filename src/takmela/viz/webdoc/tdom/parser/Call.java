package takmela.viz.webdoc.tdom.parser;

import java.util.ArrayList;
import java.util.List;

import takmela.engine.Cont;

public class Call implements ProcessingOp, ProcessingOwner
{
	public takmela.engine.Call Callee;
	public takmela.engine.Cont Cont;
	public boolean WillProcess;
	public List<Processing> Processings;
	
	public Call(takmela.engine.Call callee, Cont cont, boolean willProcess)
	{
		super();
		Callee = callee;
		Cont = cont;
		WillProcess = willProcess;
		Processings = new ArrayList<>();
	}

	@Override public void addProcessing(takmela.viz.webdoc.tdom.parser.Processing p)
	{
		Processings.add(p);
	}

	@Override public List<takmela.viz.webdoc.tdom.parser.Processing> getProcessings()
	{
		return Processings;
	}
}
