package takmela.ast;

public class Terminal implements Ast, Expr
{
	public final String Value;

	public Terminal(String _Value)
	{
		this.Value = _Value;
	}

	@Override public String toString()
	{
		return String.format("`%s`", this.Value);
	}
}
