package takmela.ast;

import java.util.List;

import takmela.ast.Expr;

public class Question implements Expr
{
	public final List<Expr> Inner;

	public Question(List<Expr> inner)
	{
		super();
		Inner = inner;
	}
	
	public String toString()
	{
		return String.format("(%s)?", Inner);
	}
}
