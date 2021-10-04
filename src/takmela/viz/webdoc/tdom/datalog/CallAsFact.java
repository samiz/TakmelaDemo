package takmela.viz.webdoc.tdom.datalog;

import takmelogic.engine.Call;
import takmelogic.engine.Cont;

public class CallAsFact implements ProcessingOp
{
	public Call callee;
	public Call caller;
	public Cont cont;
	
	public CallAsFact(Call callee, Call caller, Cont cont)
	{
		this.callee = callee;
		this.caller = caller;
		this.cont = cont;
	}
}
