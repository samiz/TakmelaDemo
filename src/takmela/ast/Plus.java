package takmela.ast;

import java.util.List;

import takmela.ast.Expr;

public class Plus implements Expr
{
	public final List<Expr> Inner;

	public Plus(List<Expr> inner)
	{
		super();
		Inner = inner;
	}
	
	public String toString()
	{
		return String.format("(%s)+", Inner);
	}
}
