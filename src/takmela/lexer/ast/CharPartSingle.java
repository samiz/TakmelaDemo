package takmela.lexer.ast;

public class CharPartSingle implements takmela.lexer.ast.Ast, takmela.lexer.ast.CharClassPart
{
	public final char Ch;

	public CharPartSingle(char _Ch)
	{
		this.Ch = _Ch;
	}

	@Override public String toString()
	{
		return String.format("CharSingle(%s)", this.Ch);
	}
}
