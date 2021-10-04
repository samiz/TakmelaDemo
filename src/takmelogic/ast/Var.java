package takmelogic.ast;

public class Var implements Term, Ast
{
	public final String N;

	public Var(String _N)
	{
		N = _N;
	}

	@Override
	public String toString()
	{
		return String.format("%s", N);
	}
}
