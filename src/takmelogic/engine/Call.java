package takmelogic.engine;

import java.util.List;

public class Call
{
	public String Rule;
	public List<Object> Args; // Null = Free, otherwise = Bound
	
	public Call(String rule, List<Object> args)
	{
		super();
		Rule = rule;
		Args = args;
	}

	@Override public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Rule);
		sb.append("(");
		String sep = "";
		for(Object arg : Args)
		{
			sb.append(sep);
			sep = ", ";
			if(arg !=null)
			{
				sb.append(arg);
			}
			else
			{
				sb.append("?");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Args == null) ? 0 : Args.hashCode());
		result = prime * result + ((Rule == null) ? 0 : Rule.hashCode());
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
		if (Args == null)
		{
			if (other.Args != null)
				return false;
		}
		else if (!Args.equals(other.Args))
			return false;
		if (Rule == null)
		{
			if (other.Rule != null)
				return false;
		}
		else if (!Rule.equals(other.Rule))
			return false;
		return true;
	}
}
