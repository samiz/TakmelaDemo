package takmela.viz.webdoc.tdom.datalog;

import java.util.Map;

import takmelogic.ast.ComparisonOp;
import takmelogic.ast.Term;

public class TestComp implements ProcessingOp
{
	public Term A;
	public Term B;
	public ComparisonOp Op;
	public boolean TestSuccess;
	public Map<String, Object> Env;
	
	public TestComp(Term a, Term b, ComparisonOp op, boolean testSuccess, Map<String, Object> env)
	{
		super();
		A = a;
		B = b;
		Op = op;
		TestSuccess = testSuccess;
		Env = env;
	}
}
