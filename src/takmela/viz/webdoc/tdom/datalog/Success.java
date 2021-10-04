package takmela.viz.webdoc.tdom.datalog;

import java.util.List;

public class Success implements ProcessingOp
{
	public takmelogic.engine.Call Exec;
	public List<Object> S;
	public boolean Root = false;
	
	public Success(takmelogic.engine.Call exec, List<Object> s)
	{
		Exec = exec;
		S = s;
	}
}
