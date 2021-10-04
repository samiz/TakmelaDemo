package takmela.tree;

import java.util.ArrayList;
import java.util.List;

import utils_takmela.LLst;
import utils_takmela.Utils;

public class Tree implements Treeish
{
	public final String Label;
	public final int Choice;
	public final List<Node> Children;

	public Tree(String label, int choice, List<Node> children)
	{
		Label = label;
		Choice = choice;
		Children = Utils.list(children);
	}
	
	public Tree(String label, int choice, LLst<Node> children)
	{
		Label = label;
		Choice = choice;
		Children = children.toList();
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Children == null) ? 0 : Children.hashCode());
		result = prime * result + Choice;
		result = prime * result + ((Label == null) ? 0 : Label.hashCode());
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
		Tree other = (Tree) obj;
		if (Children == null)
		{
			if (other.Children != null)
				return false;
		}
		else if (!Children.equals(other.Children))
			return false;
		if (Choice != other.Choice)
			return false;
		if (Label == null)
		{
			if (other.Label != null)
				return false;
		}
		else if (!Label.equals(other.Label))
			return false;
		return true;
	}

	public Tree(String label, int choice)
	{
		Label = label;
		Choice = choice;
		Children = new ArrayList<>();
	}

	public Tree appendChild(Node child)
	{
		List<Node> lst2 = Utils.list(Children);
		lst2.add(child);
		return new Tree(Label, Choice, lst2);
	}
	
	public String toString()
	{
		if(Children.size() == 0)
		{
			return Label;
		}
		return String.format("%s(%s)", Label, Utils.join(Children, ", "));
	}
}
