package takmelogic.ast;

public class Str implements Term, Ast
{
	public final String V;

	public Str(String _V)
	{
		V = _V;
	}

	@Override
	public String toString()
	{
		return String.format("\"%s\"", V);
	}
}
