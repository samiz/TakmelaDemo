package takmela.tree;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class TreeUtils
{
	public static Leaf leafChild(Tree t, int i)
	{
		return (Leaf) t.Children.get(i);
	}
	
	public static String lexemeChild(Tree t, int i)
	{
		return ((Leaf) t.Children.get(i)).token.text();
	}
	
	public static Tree branchChild(Tree t, int i)
	{
		return (Tree) t.Children.get(i);
	}

	public static String lexemeLeaf(Node n)
	{
		return ((Leaf) n).token.text();
	}
	
	public static Token leftMost(Tree tree)
	{
		return nodeLeftMost(tree);
	}
	
	private static Token nodeLeftMost(Node node)
	{
		if(node instanceof Leaf)
		{
			return new Token(((Leaf) node).token);
		}
		else if(node instanceof Tree)
		{
			Tree t = (Tree) node;
			return nodeLeftMost(t.Children.get(0));
		}
		else
		{
			throw new RuntimeException("Option not handled: " + node.toString());
		}
	}

	public static Token rightMost(Tree tree)
	{
		return nodeRightMost(tree);
	}
	
	public static Token nodeRightMost(Node node)
	{
		if(node instanceof Leaf)
		{
			return new Token(((Leaf) node).token);
		}
		else if(node instanceof Tree)
		{
			Tree t = (Tree) node;
			return nodeRightMost(t.Children.get(t.Children.size()-1));
		}
		else
		{
			throw new RuntimeException("Option not handled: " + node.toString());
		}
	}

	// When you just want to ignore all structure and search for certain nodes in the tree
	public static<T> void collect(List<T> collectInto, Node node, Predicate<Tree> test, Function<Tree, T> extract)
	{
		if(node instanceof Tree)
		{
			Tree t = (Tree) node;
			if(test.test(t))
			{
				collectInto.add(extract.apply(t));
			}
			for(Node child : t.Children)
			{
				collect(collectInto, child, test, extract);
			}
		}
		else if(node instanceof Leaf)
		{
			//
		}
		else
		{
			throw new RuntimeException("Node type not handled: " + node);
		}
	}
	
	public static void enumStar(Tree t, java.util.function.BiConsumer<Integer, Node> fn)
	{
		/*
		myRule : (a)+
		
		 ===>
		myRule: gen1
		
		{ gen1(splice) }
		gen1: a gen1
		gen1 : a
		 
	    */
		int i=0;
		while(true)
		{
			if(t.Children.size() == 0)
			{
				break;
			}
			else if(t.Children.size() == 2)
			{
				Node car = t.Children.get(0);
				Tree cdr = (Tree) t.Children.get(1);
				fn.accept(i, car);
				t = cdr;
			}
			else
			{
				throw new RuntimeException("Malformed tree while calling TreeUtils.enumStar: " + t);
			}
			++i;
		}
	}
	
	public static void enumPlus(Tree t, java.util.function.BiConsumer<Integer, Node> fn)
	{
		/*
		myRule : (a)+
		
		 ===>
		myRule: gen1
		
		{ gen1(splice) }
		gen1: a gen1
		gen1 : a
		 
	    */
		int i=0;
		while(true)
		{
			if(t.Children.size() == 1)
			{
				Node n = t.Children.get(0);
				fn.accept(i, n);
				break;
			}
			else if(t.Children.size() == 2)
			{
				Node car = t.Children.get(0);
				Tree cdr = (Tree) t.Children.get(1);
				fn.accept(i, car);
				t = cdr;
			}
			else
			{
				throw new RuntimeException("Malformed tree while calling TreeUtils.enumPlus: " + t);
			}
			++i;
		}
	}
	
	public static void enumQuestion(Tree t, java.util.function.BiConsumer<Integer, Node> fn)
	{
		/*
		myRule : (a)?
		
		 ===>
		myRule: gen1
		
		{ gen1(splice) }
		gen1: a 
		gen1 : 
	    */
		if(t.Children.size() == 0)
		{
			//
		}
		else if(t.Children.size() == 1)
		{
			Node n = t.Children.get(0);
			fn.accept(0, n);
		}
		else
		{
			throw new RuntimeException("Malformed tree while calling TreeUtils.enumQuestion: " + t);
		}
	}
}
