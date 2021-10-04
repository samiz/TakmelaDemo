package takmela.lexer.automata;

public class CharRange implements Trans
{
	public final char From;
	public final char To;

	public CharRange(char _From, char _To)
	{
		this.From = _From;
		this.To = _To;
	}

	@Override public String toString()
	{
		return String.format("[%s, %s]", this.From, this.To);
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + From;
		result = prime * result + To;
		return result;
	}

	@Override public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CharRange other = (CharRange) obj;
		if (From != other.From)
			return false;
		if (To != other.To)
			return false;
		return true;
	}

	@Override public boolean match(char c)
	{
		return From <= c && c <= To;
	}
}
