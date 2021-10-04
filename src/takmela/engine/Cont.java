package takmela.engine;

import takmela.tree.Tree;
import utils_takmela.Utils;

public class Cont
{
	// A continuation is 2 things:
	// - A 'call', composed of 
	// 		- A rule name, the caller to which we will return
	// 		- The input position at which this rule (the caller) was called
	// - The 'code position' within the rule
	
	// It can contain extra state used during processing (e.g parse trees)
	
	public final Call Caller;
	public final CodePos Code;

	// State that needs to be stored as part of the engine's 'call stack' will be here
	public final Tree TreeSoFar;
	
	// CalledRuleName is redundant (already stored as in the map key), stored here for debugging
	public final String CalledRuleName;
	
	public Cont(Call caller, CodePos code, String calledRuleName,Tree treeSoFar)
	{
		Caller = caller;
		Code = code;
		CalledRuleName = calledRuleName;
		TreeSoFar = treeSoFar;
		//Parent = parent;
	}

	public String toString()
	{
		return String.format("(%s ; %s; %s)", Caller, Code !=null? Code.toString() : "", Utils.toStringIfNotNull(TreeSoFar));
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Caller == null) ? 0 : Caller.hashCode());
		result = prime * result + ((Code == null) ? 0 : Code.hashCode());
		result = prime * result + ((TreeSoFar == null) ? 0 : TreeSoFar.hashCode());
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
		if (Caller == null)
		{
			if (other.Caller != null)
				return false;
		}
		else if (!Caller.equals(other.Caller))
			return false;
		if (Code == null)
		{
			if (other.Code != null)
				return false;
		}
		else if (!Code.equals(other.Code))
			return false;
		if (TreeSoFar == null)
		{
			if (other.TreeSoFar != null)
				return false;
		}
		else if (!TreeSoFar.equals(other.TreeSoFar))
			return false;
		return true;
	}
}
