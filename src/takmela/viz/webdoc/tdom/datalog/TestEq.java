package takmela.viz.webdoc.tdom.datalog;

import java.util.Map;

import takmelogic.ast.Term;

public class TestEq implements ProcessingOp
{
	public Term A;
	public Term B;
	public boolean TestSuccess;
	public Map<String, Object> Env;
	
	public TestEq(Term a, Term b, boolean testSuccess, Map<String, Object> env)
	{
		super();
		A = a;
		B = b;
		TestSuccess = testSuccess;
		Env = env;
	}
}
