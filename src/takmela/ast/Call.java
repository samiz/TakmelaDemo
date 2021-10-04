package takmela.ast;

public class Call implements takmela.ast.Ast, takmela.ast.Expr
{
	public final String Callee;

	public Call(String _Callee)
	{
		this.Callee = _Callee;
	}

	@Override public String toString()
	{
		return String.format("%s", this.Callee);
	}
}
