package takmela.viz.graphicsElements;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import utils_takmela.Triple;

public class RenderDotData
{
	public static BufferedImage render(GraphPositions d, GraphicsConfiguration gconf)
	{
		BufferedImage img = DrawingUtils.createFilledImage(gconf, (int) d.graphProps.Width, (int) d.graphProps.Height + 1, Color.white);
		
		Graphics2D g = img.createGraphics();
		g.setColor(Color.blue);
		
		FontMetrics metrics = g.getFontMetrics();
		
		double scale = 72;
		
		DrawingUtils.invert(d);
		for(NodeProps n : d.nodes.values())
		{
			int w = (int) (scale * n.Width);
			int h = (int) (scale * n.Height);
			
			g.drawOval((int) n.X - w/2, (int) n.Y-h/2, w, h);
			
			Rectangle2D tb = metrics.getStringBounds(n.Label, g);
			
			g.drawString(n.Label, (int) (n.X - tb.getWidth()/2), (int) (n.Y ));
		}
		
		for(EdgeProps e : d.edges.values())
		{
			
			DrawingUtils.drawCurve(g, e.Curve, Color.black); 

			Triple<Point,Point,Point> arrow = e.Curve.arrowHead();
			DrawingUtils.fillPoly(g, arrow);
			
			Rectangle2D tb = metrics.getStringBounds(e.Label, g);
			g.drawString(e.Label, (int) (e.LabelX - tb.getWidth() / 2), (int) (e.LabelY - tb.getHeight() / 2));
		}
		
		g.dispose();
		return img;
	}
}
