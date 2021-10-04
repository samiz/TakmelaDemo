package utils_takmela;

public class Triple<T1, T2, T3>
{
	public final T1 a;
	public final T2 b;
	public final T3 c;
	
	public Triple(T1 a, T2 b, T3 c)
	{
		super();
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	@Override public String toString() { return String.format("(%s, %s, %s)", a, b, c); }

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Triple other = (Triple) obj;
		if (a == null)
		{
			if (other.a != null)
				return false;
		}
		else if (!a.equals(other.a))
			return false;
		if (b == null)
		{
			if (other.b != null)
				return false;
		}
		else if (!b.equals(other.b))
			return false;
		if (c == null)
		{
			if (other.c != null)
				return false;
		}
		else if (!c.equals(other.c))
			return false;
		return true;
	}
}
