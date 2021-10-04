package takmela.viz.graphicsElements;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import takmela.engine.Expected;
import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.tree.Node;
import takmela.tree.Tree;
import takmela.tree.TreeUtils;
import utils_takmela.Pair;
import utils_takmela.Triple;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class ReadDot
{
	public static GraphPositions parseDotLayout(String dotCode, Function<String, String> idToNodeLabel) throws IOException, LexerError, UniquenessException, GrammarParsingException
	{
		// Parse the output of 'dot' with, what else?, Takmela!
		
		GraphPositions data = new GraphPositions();
		
		String dotGrammar = Utils.readAllFile("./dot.takmela");
		List<String> outParseErrors = new ArrayList<>();
		takmela.ast.Module mod = takmela.tool.Parse.parseGrammar(dotGrammar, outParseErrors);
		
		
		takmela.tool.Parse.parse(mod, dotCode, "dot", (t)->{
			extractTree(t, data, idToNodeLabel);
		}, (Set<Expected> expected, String given, Integer pos)->{
			String msg = takmela.tool.Parse.defaultFailMessage(expected, given, pos);
			System.err.println(msg);
		});
		
		return data;
	}

	private static void extractTree(Node _t, GraphPositions data, Function<String, String> idToNodeLabel)
	{
		if(!(_t instanceof Tree))
		{
			return;
		}
		
		Tree t = (Tree) _t;
		if(t.Label.equals("dot"))
		{
			for(Node child : t.Children)
			{
				extractTree(child, data, idToNodeLabel);
			}
		}
		else if(t.Label.equals("graph"))
		{
			List<Pair<String, String>> attributes = new ArrayList<>();
			
			TreeUtils.collect(attributes, t.Children.get(2), tt->tt.Label.equals("grAttr"), tt-> new Pair<>(
					TreeUtils.lexemeChild(tt, 0),
					TreeUtils.lexemeChild(tt, 2)));
			graph(data, attributes);
			
		}
		else if(t.Label.equals("node"))
		{
			// ignore
		}
		else if(t.Label.equals("n"))
		{
			String id = TreeUtils.lexemeChild(t, 0);
			
			List<Pair<String, String>> attributes = new ArrayList<>();
			
			TreeUtils.collect(attributes, t.Children.get(2), tt->tt.Label.equals("nAttr"), tt-> new Pair<>(
					TreeUtils.lexemeChild(tt, 0),
					TreeUtils.lexemeChild(tt, 2)));
			
			String node = idToNodeLabel.apply(id);
			node(data, node, attributes);
		}
		else if(t.Label.equals("edge"))
		{
			String id1 = TreeUtils.lexemeChild(t, 0);
			String id2 = TreeUtils.lexemeChild(t, 2);
			
			List<Pair<String, String>> attributes = new ArrayList<>();
			
			TreeUtils.collect(attributes, t.Children.get(4), tt->tt.Label.equals("edgeAttr"), tt-> new Pair<>(
					TreeUtils.lexemeChild(tt, 0),
					TreeUtils.lexemeChild(tt, 2)));
			
			String n1 = idToNodeLabel.apply(id1);
			String n2 = idToNodeLabel.apply(id2);
			
			edge(data, n1, n2, attributes);
		}
		else
		{
			for(Node child : t.Children)
			{
				extractTree(child, data, idToNodeLabel);
			}
		}
	}
	
	private static void graph(GraphPositions data, List<Pair<String, String>> attributes)
	{
		for(Pair<String, String> kv : attributes)
		{
			String v = stripQuotes(kv.b);
			setGraphAttr(data, kv.a, v);
		}
	}

	private static void node(GraphPositions data, String id, List<Pair<String, String>> attributes)
	{
		NodeProps n = new NodeProps();
		data.nodes.put(id, n);
		for(Pair<String, String> kv : attributes)
		{
			String v = stripQuotes(kv.b);
			setNodeAttr(n, kv.a, v);
		}		
	}

	private static void edge(GraphPositions data, String id1, String id2, List<Pair<String, String>> attributes)
	{
		EdgeProps e = new EdgeProps();
		
		for(Pair<String, String> kv : attributes)
		{
			String v = stripQuotes(kv.b);
			setEdgeAttr(e, kv.a, v);
		}
		
		String label = e.Label;
		if(label == null) { label = ""; }
		data.edges.put(new Triple<>(id1, id2, label), e);
	}
	
	private static void setGraphAttr(GraphPositions data, String key, String val)
	{
		List<Double> nums;
		switch(key)
		{
		case "bb":
			nums = toNums(val);
			data.graphProps.Width = nums.get(2);
			data.graphProps.Height = nums.get(3);
			break;
		case "ranksep":
			break;
		default:
			throw new RuntimeException("Unknown graph attribute " + key);
		}
	}
	
	private static void setNodeAttr(NodeProps n, String key, String val)
	{
		List<Double> nums;
		switch(key)
		{
		case "label":
			n.Label = val;
			break;
		case "width":
			n.Width = Double.parseDouble(val);
			break;
		case "height":
			n.Height = Double.parseDouble(val);
			break;
		case "pos":
			nums = toNums(val);
			n.X = nums.get(0);
			n.Y = nums.get(1);
			break;
		default:
			throw new RuntimeException("Unknown node attribute " + key);
		}
	}
	
	private static void setEdgeAttr(EdgeProps e, String key, String val)
	{
		List<Double> nums;
		switch(key)
		{
		case "label":
			e.Label = val;
			break;
		case "lp":
			nums = toNums(val);
			e.LabelX = nums.get(0);
			e.LabelY = nums.get(1);
			break;
		case "pos":
			parseSplineType(e, val);
			break;
		default:
			throw new RuntimeException("Unknown edge attribute " + key);
		}		
	}
	
	private static String stripQuotes(String s)
	{
		if(!s.startsWith("\""))
		{
			return s;
		}
		return s.substring(1, s.length() -1);
	}
	
	private static void parseSplineType(EdgeProps e, String val)
	{
		// Strings in dot can contain a line continuation like
		//   s= "1 2 3 \
		//       4 5"
		
		val = val.replace("\\\n", "");
		
		// Apparently can also contain just the backslash
		val = val.replace("\\", "");
				
		/*
		  A splineType in graphViz is (simplified for our very specific goals)
		      e,x,y p1 p2 p3 p4...
		   
	      i.e 
	      1- The items are separated by spaces
	      2- Each point is num,num
	      3- A special 'point' is the endpoint, e,num,num
	      
	      The number of (non-edge) points should be 3n+1
		 */
		
		String[] parts = val.split(" ");

		List<Point> points = new ArrayList<>();
		Pair<Double, Double> endP = null;
		
		for(String part : parts)
		{
			part = part.trim();
			if(part.startsWith("e"))
			{
				String[] endP_parts = part.split(",");
				double x = Double.parseDouble(endP_parts[1].trim());
				double y = Double.parseDouble(endP_parts[2].trim());
				endP = new Pair<>(x, y);
			}
			else
			{
				String[] point_parts = part.split(",");
				double x = Double.parseDouble(point_parts[0].trim());
				double y = Double.parseDouble(point_parts[1].trim());
				points.add(new Point((int) x, (int) y));
			}
		}
		
		e.Curve = new CurveD(points, endP);
	}

	private static List<Double> toNums(String val)
	{
		// Strings in dot can contain a line continuation like
		//   s= "1 2 3 \
		//       4 5"
		val = val.replace("\\\n", "");

		// Apparently can also contain just the backslash
		val = val.replace("\\", "");

		
		List<Double> result = new ArrayList<>();
		String[] parts = val.split(",");
		for(String part : parts)
		{
			part = part.trim();
			double v = Double.parseDouble(part);
			result.add(v);
		}
		return result;
	}
}
