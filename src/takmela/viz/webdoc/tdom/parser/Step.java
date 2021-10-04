package takmela.viz.webdoc.tdom.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import takmela.engine.Call;
import takmela.engine.Cont;
import utils_takmela.Utils;

public abstract class Step implements ProcessingOwner
{
	private List<Processing> processing = new ArrayList<>();
	private List<Success> successes = new ArrayList<>();
	private StepStatus status = StepStatus.Processing;
	private Map<Call, Set<Cont>> KWorkList = new HashMap<>();

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
	
	public Map<Call, Set<Cont>> getNewConts() { return KWorkList; }
	public void setNewConts(Map<Call, Set<Cont>> value) { KWorkList = value; }
	public void addNewCont(Call callee, Cont cont)
	{
		Utils.addMapSet(KWorkList, callee, cont);
	}
	
	public StepStatus getStatus() { return status; }
	public void setStatus(StepStatus value) { status = value; }
}
