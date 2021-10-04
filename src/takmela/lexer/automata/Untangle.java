package takmela.lexer.automata;

import java.util.List;

public class Untangle
{
	public List<CharRange> onlyA;
	public CharRange common;
	public List<CharRange> onlyB;

	public Untangle(List<CharRange> onlyA, CharRange common, List<CharRange> onlyB)
	{
		this.onlyA = onlyA;
		this.common = common;
		this.onlyB = onlyB;
	}

	public String toString()
	{
		return String.format("{common=%s, onlyA=%s, onlyB=%s}", common, onlyA, onlyB);
	}
}
