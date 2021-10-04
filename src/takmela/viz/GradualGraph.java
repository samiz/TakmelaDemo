package takmela.viz;

import java.util.Map;
import java.util.Set;

import utils_takmela.Triple;

public class GradualGraph
{
	public Set<String> Nodes;
	public Set<Triple<String, String, String>> Edges;
	
	public Map<String, Integer> NodeStages;
	public Map<Triple<String, String, String>, Integer> EdgeStages;
	
	public int maxStage;

	public GradualGraph(Set<String> nodes, Set<Triple<String,String,String>> edgese, Map<String, Integer> nodeStages,
			Map<Triple<String, String, String>, Integer> edgeStages, int maxStage)
	{
		super();
		Nodes = nodes;
		Edges = edgese;
		NodeStages = nodeStages;
		EdgeStages = edgeStages;
		this.maxStage = maxStage;
	}
}
