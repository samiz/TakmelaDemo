package takmela.lexer.automata;

public class Epsilon implements Trans
{
	@Override public boolean equals(Object other)
	{
		return other instanceof Epsilon;
	}

	@Override public int hashCode()
	{
		return 31;
	}

	@Override public boolean match(char c)
	{
		throw new RuntimeException("Epsilon shouldn't be used to match chars");
	}
}
