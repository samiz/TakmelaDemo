package takmela.engine;

import java.util.List;

import takmela.ast.Expr;
import takmela.ast.Rule;

public class CodePos
{
	public final Rule rule;
	public final int ruleAlt;
	public final int i;

	public CodePos(Rule rule, int ruleAlt, int i)
	{
		super();
		this.rule = rule;
		this.ruleAlt = ruleAlt;
		this.i = i;
	}
	
	public CodePos next()
	{
		return new CodePos(rule, ruleAlt, i+1);
	}

	public String toString()
	{
		List<Expr> alt = rule.Options.get(ruleAlt);
		String s = rule.Name + " → ";
		
		for(int c=0; c<i; ++c)
		{
			s += " " + alt.get(c).toString();
		}
		s += "•";
		for(int c=i; c<alt.size(); ++c)
		{
			s += " " + alt.get(c).toString();
		}
		return s;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + i;
		result = prime * result + ((rule.Name == null) ? 0 : rule.hashCode());
		result = prime * result + ruleAlt;
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
		CodePos other = (CodePos) obj;
		if (i != other.i)
			return false;
		if (rule.Name == null)
		{
			if (other.rule.Name != null)
				return false;
		}
		else if (!rule.Name.equals(other.rule.Name))
			return false;
		if (ruleAlt != other.ruleAlt)
			return false;
		return true;
	}
}
