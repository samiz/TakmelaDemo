package takmela.viz;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import java.util.Set;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.graphicsElements.DrawingUtils;
import takmela.viz.graphicsElements.EdgeProps;
import takmela.viz.graphicsElements.GraphPositions;
import takmela.viz.graphicsElements.NodeProps;
import takmela.viz.graphicsElements.PositionStyle;
import takmela.viz.graphicsElements.ReadDot;
import takmela.viz.graphicsElements.WriteDot;
import utils_takmela.Pair;
import utils_takmela.ProcessResult;
import utils_takmela.Triple;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class StagedGraphRender
{
	private final static String pathToDot = "/usr/bin/dot";
	
	public static final PositionStyle DefaultPositionStyle = PositionStyle.Dot;
	
	public static final int DefaultMargin = 6;
	
	public static List<BufferedImage> positionAndRenderDefault(Set<String> nodes, Set<Triple<String,String,String>> edges,
			Map<String, Integer> nodeStages, Map<Triple<String, String, String>, Integer> edgeStages, int maxStage,
			int margin, boolean connectEdgeLabelToEdge,
			int graphMaxEdgeLabelWidth) throws IOException, LexerError, UniquenessException, GrammarParsingException, InterruptedException
	{
		switch(DefaultPositionStyle)
 		{
		case Dot:
			return positionAndRenderDot(nodes, edges, nodeStages, edgeStages, maxStage, margin, connectEdgeLabelToEdge, graphMaxEdgeLabelWidth);
		default:
			throw new RuntimeException("Option not handled: " + DefaultPositionStyle);
		}
	}
	
	public static List<BufferedImage> positionAndRenderDot(Set<String> nodes, Set<Triple<String, String, String>> edges,
			Map<String, Integer> nodeStages, Map<Triple<String, String, String>, Integer> edgeStages, int maxStage, 
			int margin, boolean connectEdgeLabelToEdge, int graphMaxEdgeLabelWidth) throws IOException, LexerError, UniquenessException, GrammarParsingException, InterruptedException
	{
		GraphPositions poss = positionDot(nodes, edges, margin);
		return renderStages(poss, nodeStages, edgeStages, maxStage, margin, connectEdgeLabelToEdge, graphMaxEdgeLabelWidth);
	}
	
	public static GraphPositions positionDot(Set<String> nodes, Set<Triple<String, String, String>> edges,
			int margin) throws IOException, LexerError, UniquenessException, GrammarParsingException, InterruptedException
	{
		Map<String, String> outNodeLabelToId = new HashMap<>();
		String dotSpec = WriteDot.generateDotSpec(nodes, edges, outNodeLabelToId);
		String dotPositions = launchGraphViz(dotSpec);
		
		Map<String, String> idToNodeLabel = Utils.invertMap(outNodeLabelToId);
		GraphPositions positions = ReadDot.parseDotLayout(dotPositions, (id)->Utils.mustGet(idToNodeLabel, id));
		
		positions.invert();
		positions.scale();
		positions.randomizeLabelPositions();
		positions.nudge(margin, margin); // sometimes text from labels seeps outside the image boundary
		
		return positions;
	}
	
	private static String launchGraphViz(String dotSpec) throws InterruptedException, IOException
	{
		ProcessResult pr = Utils.runProcessAndWait(dotSpec, new String[] {pathToDot});
		return pr.StdOut;
	}

	public static List<BufferedImage> renderStages(GraphPositions poss, Map<String, Integer> nodeStages, 
			Map<Triple<String, String, String>, Integer> edgeStages, int maxStage, 
			int margin, boolean connectEdgeLabelToEdge, int graphMaxEdgeLabelWidth)
	{
		// Inefficient, since it traverses the whole graph at every stage
		// but it's for our small demos anyway.
		// TODO: use 'stageNodes' and 'stageEdges' instead of 'nodeStages' and 'edgeStages'
		
		List<BufferedImage> result = new ArrayList<>();
		
		GraphicsConfiguration gconf = DrawingUtils.defaultGraphicsConfiguration();
		
		for(int stage=0; stage < maxStage; ++stage)
		{
			BufferedImage img = drawStage(stage, poss, nodeStages, edgeStages, margin, connectEdgeLabelToEdge, 
					graphMaxEdgeLabelWidth, gconf);
			result.add(img);
		}
		return result;
	}

	private static BufferedImage drawStage(int stage, GraphPositions poss, Map<String, Integer> nodeStages,
			Map<Triple<String, String, String>, Integer> edgeStages, int margin, boolean connectEdgeLabelToEdge, 
			int graphMaxEdgeLabelWidth, GraphicsConfiguration gconf)
	{
		int w = (margin * 2) + (int) poss.graphProps.Width;
		int h = (margin * 2) + (int) poss.graphProps.Height;
		
		BufferedImage result = DrawingUtils.createFilledImage(gconf, w, h, Color.white);
		Graphics2D g = result.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics metrics = g.getFontMetrics();
		
		for(Entry<String, NodeProps> node : poss.nodes.entrySet())
		{
			String n = node.getKey();
			NodeProps npos = node.getValue();
			int nodeStage = nodeStages.get(n);

			if(nodeStage < stage)
			{
				drawNode(g, n, npos, Color.black, metrics);
			}
			else if(nodeStage == stage)
			{
				drawNode(g, n, npos, Color.red, metrics);
			}
			else
			{
				// Don't draw it at this stage
			}
		}
		
		for(Entry<Triple<String, String, String>, EdgeProps> edge : poss.edges.entrySet())
		{
			Triple<String, String, String> e = edge.getKey();
			EdgeProps epos = edge.getValue();
			int edgeStage = edgeStages.get(e);
			if(edgeStage < stage)
			{
				drawEdge(g, e, epos, Color.black, connectEdgeLabelToEdge, graphMaxEdgeLabelWidth, metrics);
			}
			else if(edgeStage == stage)
			{
				drawEdge(g, e, epos, Color.red, connectEdgeLabelToEdge, graphMaxEdgeLabelWidth, metrics);
			}
			else
			{
				// Don't draw it at this stage
			}
		}
		g.dispose();
		return result;
	}

	private static void drawNode(Graphics2D g, String n, NodeProps npos, Color clr, FontMetrics metrics)
	{
		int w = (int) npos.Width;
		int h = (int) npos.Height;
		
		g.setColor(clr);
		
		g.drawOval((int) (npos.X - w/2), (int) (npos.Y-h/2), w, h);
		
		// Complex/large graphs will be shrank in the HTML; let's give them a chance to be still somewhat readable
		// by enlarging the node text
		Font f = g.getFont();
		Font f2 = new Font(f.getFamily(), f.getStyle(), f.getSize() + 5); 
		g.setFont(f2);
		
		metrics = g.getFontMetrics();
		Rectangle2D tb = metrics.getStringBounds(npos.Label, g);
		
		g.drawString(n, (int) (npos.X - tb.getWidth()/2), (int) (npos.Y + 5)); 
		
		g.setFont(f);
	}

	private static void drawEdge(Graphics2D g, Triple<String,String,String> e, EdgeProps epos, 
			Color clr, boolean connectEdgeLabelToEdge, int graphMaxEdgeLabelWidth, FontMetrics metrics)
	{
		DrawingUtils.drawCurve(g, epos.Curve, clr); 
		
		Triple<Point,Point,Point> arrow = epos.Curve.arrowHead();
		DrawingUtils.fillPoly(g, arrow);
		
		// Add spaces around edge labels to prevent overlap
		//String label = epos.Label;
		String label = String.format("  %s  ", epos.Label);
		
		Rectangle2D tb = metrics.getStringBounds(epos.Label, g);

		int lblX = (int) (epos.LabelX - tb.getWidth() / 2);
		int lblY = (int) (epos.LabelY - tb.getHeight() / 2);
		
		if(graphMaxEdgeLabelWidth > 0)
		{
			DrawingUtils.drawStringWrapped(g, label, lblX, lblY, graphMaxEdgeLabelWidth);
		}
		else
		{
			g.drawString(label, lblX, lblY);
		}
		
		if(connectEdgeLabelToEdge)
		{
			Stroke old = g.getStroke();
			Color oldC = g.getColor();
			
			// Dashed
			Stroke s = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] {3f}, 0f);
			
			// Dotted
			//Stroke s = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] {2f}, 0f);
			
			g.setStroke(s);
			
			Color c = Color.DARK_GRAY;
			g.setColor(c);
			
			Pair<Double, Double> midPoint = DrawingUtils.bezierMidPoint(epos.Curve);
			g.drawLine((int) (double) midPoint.a, (int) (double) midPoint.b, lblX + 20, lblY);
			g.setStroke(old);
			g.setColor(oldC);
		}
	}
}
