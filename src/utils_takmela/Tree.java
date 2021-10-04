package utils_takmela;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tree<T> implements ITree<T>
{
	private List<Tree<T>> children = new ArrayList<>();
	private T value;
	
	public Tree(T value) { this.value = value; }
	public T getValue() { return value;}
	
	
	public int nChildren() { return children.size(); }
	public Tree<T> child(int i) { return children.get(i); }
	
	public Tree<T> addChild(T c) { Tree<T> t = new Tree<T>(c); children.add(t); return t; }
	public Tree<T> addChildTree(Tree<T> t) { children.add(t); return t; }
	public Tree<T> insertChild(T c, int at) { Tree<T> t = new Tree<T>(c); children.add(at, t); return t; }
	public Tree<T> insertChildTree(Tree<T> t, int at) { children.add(at, t); return t; }
	public Tree<T> removeChildAt(int i) { return children.remove(i); }
	
	public static<T> void computeLevels(Tree<T> tree, Map<T, Integer> levels)
	{
		computeLevels(tree, levels, 0);
	}
	
	private static<T> void computeLevels(Tree<T> tree, Map<T, Integer> levels, int i)
	{
		levels.put(tree.value, i);
		for(Tree<T> child : tree.children)
		{
			computeLevels(child, levels, i+1);
		}
	}
	
	public static<T> List<T> asDfs_pre(Tree<T> tree)
	{
		List<T> dfsTrail = new ArrayList<>();
		asDfs_pre(tree, dfsTrail);
		return dfsTrail;
	}
	
	private static<T> void asDfs_pre(Tree<T> tree, List<T> dfsTrail)
	{
		dfsTrail.add(tree.value);
		for(Tree<T> child : tree.children)
		{
			asDfs_pre(child, dfsTrail);
		}
	}
}
