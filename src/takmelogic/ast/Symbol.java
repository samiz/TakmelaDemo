package takmelogic.ast;

public class Symbol implements Ast, Term
{
	public final String V;

	public Symbol(String _V)
	{
		V = _V;
	}

	@Override
	public String toString()
	{
		return String.format("%s", V);
	}
}
