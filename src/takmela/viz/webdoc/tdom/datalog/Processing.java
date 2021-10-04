package takmela.viz.webdoc.tdom.datalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import takmelogic.engine.Call;
import takmelogic.engine.CodePos;

public class Processing
{
	public List<ProcessingOp> Operations;
	
	public Call Callee;
	public CodePos CodePos;
	public Map<String, Object> BindingsSoFar;
	
	public Processing(Call execRule, CodePos seq, Map<String, Object> bindingsSoFar)
	{
		this.Callee = execRule;
		this.CodePos = seq;
		this.BindingsSoFar = bindingsSoFar;
		Operations = new ArrayList<>();
	}
}
