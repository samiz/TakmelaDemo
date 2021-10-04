package takmela.lexer.ast;

import takmela.lexer.ast.RExpr;

public class Oring implements takmela.lexer.ast.Ast, takmela.lexer.ast.RExpr
{
	public final java.util.List<RExpr> Exprs;

	public Oring(java.util.List<RExpr> _Exprs)
	{
		this.Exprs = _Exprs;
	}

	@Override public String toString()
	{
		return String.format("Oring(%s)", utils_takmela.Utils.surroundJoin(this.Exprs, "[", "]", ", "));
	}
}
