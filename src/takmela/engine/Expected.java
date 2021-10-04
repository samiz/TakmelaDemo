package takmela.engine;

public class Expected
{
	public final String Expected;
	public final String FromRule;
	
	public Expected(String expected, String fromRule)
	{
		super();
		Expected = expected;
		FromRule = fromRule;
	}
	
	public String toString()
	{
		return String.format("%s [in %s]", Expected, FromRule);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Expected == null) ? 0 : Expected.hashCode());
		result = prime * result + ((FromRule == null) ? 0 : FromRule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expected other = (Expected) obj;
		if (Expected == null)
		{
			if (other.Expected != null)
				return false;
		}
		else if (!Expected.equals(other.Expected))
			return false;
		if (FromRule == null)
		{
			if (other.FromRule != null)
				return false;
		}
		else if (!FromRule.equals(other.FromRule))
			return false;
		return true;
	}
	
	
}
