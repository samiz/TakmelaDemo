package takmelogic.ast;

public class Int implements Term, Ast
{
	public final int V;

	public Int(int _V)
	{
		V = _V;
	}

	@Override
	public String toString()
	{
		return String.format("%s", V);
	}
}
