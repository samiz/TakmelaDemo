package takmela.viz.webdoc.tdom.datalog;

import java.util.List;

public class SuccessFact implements ProcessingOp
{
	public takmelogic.engine.Call Exec;
	public List<Object> S;
	
	public SuccessFact(takmelogic.engine.Call exec, List<Object> s)
	{
		Exec = exec;
		S = s;
	}
}
