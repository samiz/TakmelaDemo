package takmelogic.engine;

import java.util.Map;

public class Cont
{
	// The caller
	public Call Call;
	
	// The caller codePos
	public CodePos CodePos;

	public Map<String, Object> BindingsSoFar;

	public Cont(Call call, CodePos codePos, Map<String, Object> bindingsSoFar)
	{
		super();
		this.Call = call;
		this.CodePos = codePos;
		this.BindingsSoFar = bindingsSoFar;
	}
	
	public String toString()
	{
		return String.format("(%s ; %s; %s)", Call, CodePos, BindingsSoFar);
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((BindingsSoFar == null) ? 0 : BindingsSoFar.hashCode());
		result = prime * result + ((Call == null) ? 0 : Call.hashCode());
		result = prime * result + ((CodePos == null) ? 0 : CodePos.hashCode());
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
		Cont other = (Cont) obj;
		if (BindingsSoFar == null)
		{
			if (other.BindingsSoFar != null)
				return false;
		}
		else if (!BindingsSoFar.equals(other.BindingsSoFar))
			return false;
		if (Call == null)
		{
			if (other.Call != null)
				return false;
		}
		else if (!Call.equals(other.Call))
			return false;
		if (CodePos == null)
		{
			if (other.CodePos != null)
				return false;
		}
		else if (!CodePos.equals(other.CodePos))
			return false;
		return true;
	}
}
