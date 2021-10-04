package takmela.viz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utils_takmela.Triple;

public class GradualGraphBuilder
{
	public Set<String> Nodes = new HashSet<>();
	public Set<Triple<String, String, String>> Edges = new HashSet<>();
	
	public Map<String, Integer> NodeStages = new HashMap<>();
	public Map<Triple<String, String, String>, Integer> EdgeStages = new HashMap<>();
	
	private int stage;
	
	public GradualGraphBuilder() { reset(); }
	
	public void node(String v)
	{
		if(!Nodes.contains(v))
		{
			NodeStages.put(v, stage);
		}
		
		Nodes.add(v);
	}
	
	public void edge(String v, String w)
	{
		edge(v, w, "");
	}
	
	public void edge(String v, String w, String label)
	{
		Triple<String, String, String> edge = new Triple<>(v, w, label);
		Edges.add(edge);
		if(!EdgeStages.containsKey(edge))
		{
			EdgeStages.put(edge, stage);
		}
	}
	
	public void next()
	{
		stage++;
	}
	
	public GradualGraph build()
	{
		int _stage = stage;
		return new GradualGraph(Nodes, Edges, NodeStages, EdgeStages, _stage);
	}
	
	public void reset()
	{
		stage = 0;
		Nodes = new HashSet<>();
		Edges = new HashSet<>();
		NodeStages = new HashMap<>();
		EdgeStages = new HashMap<>();
	}

	// Use for diagnostics only!!
	public int dbgGetCurrentStage()
	{
		return stage;
	} 
}
