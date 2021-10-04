package takmela.ast;

public class Tokenz implements takmela.ast.Ast, takmela.ast.Expr
{
	public final String Value;

	public Tokenz(String _Value)
	{
		this.Value = _Value;
	}

	@Override public String toString()
	{
		return String.format("%s", this.Value);
	}
}
