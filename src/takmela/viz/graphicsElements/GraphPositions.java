package takmela.viz.graphicsElements;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import utils_takmela.Triple;

public class GraphPositions
{
	public GraphProps graphProps = new GraphProps();
	public Map<String, NodeProps> nodes = new HashMap<>();
	public Map<Triple<String, String, String>, EdgeProps> edges = new HashMap<>();
	
	public String toString()
	{
		return String.format("dot(g=%s, n=%s, e=%s)", graphProps, nodes, edges);
	}

	public NodeProps node(String n)
	{
		NodeProps node = nodes.get(n);
		if(node == null)
		{
			node = new NodeProps();
			nodes.put(n, node);
		}
		return node;
	}
	
	public EdgeProps edge(String n1, String n2, String label)
	{
		Triple<String, String, String> key = new Triple<>(n1, n2, label);
		EdgeProps edge = edges.get(key);
		if(edge == null)
		{
			edge = new EdgeProps();
			edge.Label = label;
			edges.put(key, edge);
		}
		return edge;
	}
	
	public EdgeProps edge(String n1, String n2)
	{
		return edge(n1, n2, "");
	}
	
	public void scale()
	{
		// GraphViz stores node dimensions in inches
		// we need to scale them to pixels
		
		double scale = 72;
		
		for(Entry<String, NodeProps> kv : nodes.entrySet())
		{
			NodeProps nprop = kv.getValue();
			nprop.Width *= scale;
			nprop.Height *= scale;
		}
	}
	
	public void invert()
	{
		double h = graphProps.Height;
		
		for(NodeProps n : nodes.values())
		{
			n.Y = h - n.Y;
		}
		
		for(EdgeProps e : edges.values())
		{
			e.LabelY = h - e.LabelY;
			e.Curve.invert(h);
		}
	}

	public void randomizeLabelPositions()
	{
		// Labels positioned using GraphViz often get vertically aligned
		// to be exactly on the same line, thus difficult to tell apart if they're close
		
		final double limit = 20;
		
		for(EdgeProps e : edges.values())
		{
			e.LabelY += (int) ((Math.random() - 0.5) * limit);
		}
	}

	public void nudge(int dx, int dy)
	{
		for(NodeProps n : nodes.values())
		{
			n.X += dx;
			n.Y += dy;
		}
		
		for(EdgeProps e : edges.values())
		{
			e.LabelX += dx;
			e.LabelY += dy;
			e.Curve.nudge(dx, dy);
		}
	}
}
