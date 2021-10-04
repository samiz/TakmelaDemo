package takmela.lexer.ast;

import takmela.lexer.ast.CharClassPart;

public class NotCharClass implements takmela.lexer.ast.Ast, takmela.lexer.ast.RExpr
{
	public final java.util.List<CharClassPart> Parts;

	public NotCharClass(java.util.List<CharClassPart> _Parts)
	{
		this.Parts = _Parts;
	}

	@Override public String toString()
	{
		return String.format("NotCharClass(%s)", utils_takmela.Utils.surroundJoin(this.Parts, "[", "]", ", "));
	}
}
