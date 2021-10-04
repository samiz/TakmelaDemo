package takmela.ast;

import java.util.List;

import takmela.ast.Expr;
import utils_takmela.Utils;

public class Nested implements Expr
{
	public final List<List<Expr>> InnerOptions;
	public final List<String> InnerLabels;
	
	public Nested(List<List<Expr>> innerOptions, List<String> innerLabels)
	{
		super();
		InnerOptions = innerOptions;
		InnerLabels = innerLabels;
	}


	public String toString()
	{
		return String.format("(%s)", Utils.zip(InnerOptions, InnerLabels, (a,b) -> Utils.join(a, " ") + (b.equals("")?"": "#" + b)));
	}
}
