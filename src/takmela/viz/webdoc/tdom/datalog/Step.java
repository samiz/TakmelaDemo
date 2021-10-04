package takmela.viz.webdoc.tdom.datalog;

import java.util.ArrayList;
import java.util.List;

public abstract class Step implements ProcessingOwner
{
	private List<Processing> processing = new ArrayList<>();
	private List<Success> successes = new ArrayList<>();

	public abstract String getDescription();
	
	@Override public List<Processing> getProcessings()
	{
		return processing;
	}

	@Override public void addProcessing(Processing processing)
	{
		this.processing.add(processing);
	}

	public List<Success> getSuccesses()
	{
		return successes;
	}

	public void addSuccess(Success success)
	{
		this.successes.add(success);
	}
}
