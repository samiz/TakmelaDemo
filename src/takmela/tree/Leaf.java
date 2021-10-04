package takmela.tree;

public class Leaf implements Node
{
	public final takmela.lexer.Token token;

	public Leaf(takmela.lexer.Token token)
	{
		this.token = token;
	}

	public String toString()
	{
		return token.toString();
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		Leaf other = (Leaf) obj;
		if (token == null)
		{
			if (other.token != null)
				return false;
		}
		else if (!token.equals(other.token))
			return false;
		return true;
	}
}
