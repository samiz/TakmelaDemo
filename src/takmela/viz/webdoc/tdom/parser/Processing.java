package takmela.viz.webdoc.tdom.parser;

import java.util.ArrayList;
import java.util.List;

import takmela.engine.Call;
import takmela.engine.CodePos;

public class Processing
{
	public List<ProcessingOp> Operations;
	
	public Call Callee;
	public CodePos CodePos;
	public int InputPos;
	
	public Processing(Call execRule, CodePos seq, int inputPos)
	{
		this.Callee = execRule;
		this.CodePos = seq;
		this.InputPos = inputPos;
		Operations = new ArrayList<>();
	}
}
