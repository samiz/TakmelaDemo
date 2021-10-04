package takmelogic.engine;

import java.util.List;
import takmelogic.ast.Rule;
import takmelogic.ast.SubGoal;

//Note: uses takmelogic.ast.Rule object identity in equals() and hashCode()

public class CodePos
{
	public takmelogic.ast.Rule rule;
	public int sgNum;
	
	public CodePos(Rule rule, int sgNum)
	{
		super();
		this.rule = rule;
		this.sgNum = sgNum;
	}

	public CodePos next()
	{
		return new CodePos(rule, sgNum+1);
	}

	public String toString()
	{
		List<SubGoal> alt = rule.SubGoals;
		String s = rule.Head + " :- ";
		
		for(int c=0; c<sgNum; ++c)
		{
			s += " " + alt.get(c).toString();
		}
		s += "•";
		for(int c=sgNum; c<alt.size(); ++c)
		{
			s += " " + alt.get(c).toString();
		}
		return s;
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
		result = prime * result + sgNum;
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
		CodePos other = (CodePos) obj;
		if (rule == null)
		{
			if (other.rule != null)
				return false;
		}
		else if (!rule.equals(other.rule))
			return false;
		if (sgNum != other.sgNum)
			return false;
		return true;
	}
}
