package takmela.lexer.ast;

import takmela.lexer.ast.RExpr;

public class Question implements takmela.lexer.ast.Ast, takmela.lexer.ast.RExpr
{
	public final RExpr Expr;

	public Question(RExpr _Expr)
	{
		this.Expr = _Expr;
	}

	@Override public String toString()
	{
		return String.format("Question(%s)", this.Expr);
	}
}
