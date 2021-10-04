package takmela.ast;

public class Seq implements takmela.ast.Ast, takmela.ast.Expr
{
	public final java.util.List<Expr> Items;

	public Seq(java.util.List<Expr> _Items)
	{
		this.Items = _Items;
	}

	@Override public String toString()
	{
		return String.format("Seq(%s)", utils_takmela.Utils.surroundJoin(this.Items, "[", "]", ", "));
	}
}
