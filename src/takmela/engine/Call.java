package takmela.engine;

public class Call
{
	public final String Callee;
	public final int InputPos; // input pos during time of call
	
	public Call(String callee, int inputPos)
	{
		super();
		Callee = callee;
		InputPos = inputPos;
	}

	@Override public String toString()
	{
		return String.format("%s/%s", Callee, InputPos);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Callee == null) ? 0 : Callee.hashCode());
		result = prime * result + InputPos;
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
		Call other = (Call) obj;
		if (Callee == null)
		{
			if (other.Callee != null)
				return false;
		} 
		else if (!Callee.equals(other.Callee))
			return false;
		if (InputPos != other.InputPos)
			return false;
		return true;
	}
}
