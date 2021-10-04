package takmela.lexer.ast;

import takmela.lexer.ast.CharClassPart;

public class CharClass implements takmela.lexer.ast.Ast, takmela.lexer.ast.RExpr
{
	public final java.util.List<CharClassPart> Parts;

	public CharClass(java.util.List<CharClassPart> _Parts)
	{
		this.Parts = _Parts;
	}

	@Override public String toString()
	{
		return String.format("CharClass(%s)", utils_takmela.Utils.surroundJoin(this.Parts, "[", "]", ", "));
	}
}
