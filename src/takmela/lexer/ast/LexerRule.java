package takmela.lexer.ast;

import takmela.lexer.ast.RExpr;
import utils_takmela.Utils;

public class LexerRule implements takmela.lexer.ast.Ast
{
	public final String Name;
	public final java.util.List<String> Within;
	public final java.util.List<String> Pushes;
	public final java.util.List<String> Pops;
	public final String Class;
	public final boolean Skip;
	public final RExpr Expr;

	public LexerRule(String _Name, java.util.List<String> _Within, java.util.List<String> _Pushes,
			java.util.List<String> _Pops, String _Class, boolean _Skip, RExpr _Expr)
	{
		this.Name = _Name;
		this.Within = _Within;
		this.Pushes = _Pushes;
		this.Pops = _Pops;
		this.Class = _Class;
		this.Skip = _Skip;
		this.Expr = _Expr;
	}
	
	public LexerRule(String _Name, RExpr _Expr, boolean _Skip)
	{
		this.Name = _Name;
		this.Within = Utils.list();
		this.Pushes = Utils.list();
		this.Pops = Utils.list();
		this.Class = "";
		this.Skip = _Skip;
		this.Expr = _Expr;
	}
	
	public LexerRule(String _Name, RExpr _Expr)
	{
		this.Name = _Name;
		this.Within = Utils.list();
		this.Pushes = Utils.list();
		this.Pops = Utils.list();
		this.Class = "";
		this.Skip = false;
		this.Expr = _Expr;
	}

	@Override public String toString()
	{
		return String.format("LexerRule(%s, %s, %s, %s, %s, %s, %s)", this.Name,
				utils_takmela.Utils.surroundJoin(this.Within, "[", "]", ", "),
				utils_takmela.Utils.surroundJoin(this.Pushes, "[", "]", ", "),
				utils_takmela.Utils.surroundJoin(this.Pops, "[", "]", ", "), this.Class, this.Skip, this.Expr);
	}
}
