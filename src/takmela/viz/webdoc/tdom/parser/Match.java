package takmela.viz.webdoc.tdom.parser;

public class Match implements ProcessingOp
{
	public String TokenToMatch;
	public String InputToMatch;
	public boolean Succeeded;
	public int InputPosAfter;
	
	public Match(String tokenToMatch, String inputToMatch, boolean succeeded, int inputPosAfter)
	{
		super();
		TokenToMatch = tokenToMatch;
		InputToMatch = inputToMatch;
		Succeeded = succeeded;
		InputPosAfter = inputPosAfter;
	}
}
