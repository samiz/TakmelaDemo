package takmela.lexer.ast;

public class ByName implements takmela.lexer.ast.Ast, takmela.lexer.ast.RExpr
{
	public final String Name;

	public ByName(String _Name)
	{
		this.Name = _Name;
	}

	@Override public String toString()
	{
		return String.format("ByName(%s)", this.Name);
	}
}
