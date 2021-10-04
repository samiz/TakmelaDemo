package takmelogic.engine;

public class S
{
	public final String Value;

	public S(String value)
	{
		Value = value;
	}

	@Override public String toString()
	{
		return Value;
	}

	@Override public int hashCode()
	{
		return Value.hashCode();
	}

	@Override public boolean equals(Object other)
	{
		return (other instanceof S) && Value.equals(((S) other).Value);
	}
}
