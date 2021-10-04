package takmela.lexer.ast;

import takmela.lexer.ast.RExpr;

public class Plus implements takmela.lexer.ast.Ast, takmela.lexer.ast.RExpr
{
	public final RExpr Expr;

	public Plus(RExpr _Expr)
	{
		this.Expr = _Expr;
	}

	@Override public String toString()
	{
		return String.format("Plus(%s)", this.Expr);
	}
}
