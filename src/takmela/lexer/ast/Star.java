package takmela.lexer.ast;

import takmela.lexer.ast.RExpr;

public class Star implements takmela.lexer.ast.Ast, takmela.lexer.ast.RExpr
{
	public final RExpr Expr;

	public Star(RExpr _Expr)
	{
		this.Expr = _Expr;
	}

	@Override public String toString()
	{
		return String.format("Star(%s)", this.Expr);
	}
}
