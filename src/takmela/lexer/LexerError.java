package takmela.lexer;

public class LexerError extends Exception
{
	private static final long serialVersionUID = 6394749093257146121L;

	public final int Line, Column;
	public LexerError(int line, int column, String msg)
	{
		super(msg);
		Line = line;
		Column = column;
	}
}
