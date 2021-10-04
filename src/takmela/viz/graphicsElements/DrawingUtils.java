package takmela.viz.graphicsElements;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import utils_takmela.Pair;
import utils_takmela.Triple;

public class DrawingUtils
{
	public static void invert(GraphPositions d)
	{
		d.invert();
	}
	
	public static void drawCurve(Graphics2D g, Curve c, Color clr)
	{
		GeneralPath path = new GeneralPath();
		
		c.reset();
		Point p = c.firstMoveTo();
		path.moveTo(p.getX(), p.getY());
		
		while(c.hasMoreSegments())
		{
			Triple<Point,Point,Point> seg = c.nextSegment();
			
			int x1 = seg.a.x;
			int y1 = seg.a.y;
			
			int x2 = seg.b.x;
			int y2 = seg.b.y;
			
			int x3 = seg.c.x;
			int y3 = seg.c.y;
			path.curveTo(x1, y1, x2, y2, x3, y3);
		}
		
		if(clr != null)
		{
			g.setColor(clr);
		}
		g.draw(path);
	}
	
	public static Pair<Double, Double> bezierMidPoint(double x0, double y0, double x1, double y1, 
									  double x2, double y2, double x3, double y3)
	{
		/*
		 // With t = 0.5 
		 
		final double eighth = 1.0/8.0;
		final double three_eighths = 3.0/8.0;
		
		double mx =  eighth * x0 + three_eighths * x1 + three_eighths * x2 + eighth * x3;
		double my =  eighth * y0 + three_eighths * y1 + three_eighths * y2 + eighth * y3;
		*/
		
		final double t = 0.9;
		final double a = (1.0 - t) * (1.0 - t) * (1.0 - t);
		final double b = 3.0 * (1.0 - t) * (1.0 - t) * t;
		final double c = 3.0 * (1.0 - t) * t * t;
		final double d = t * t * t;
		
		double mx =  a * x0 + b * x1 + c * x2 + d * x3;
		double my =  a * y0 + b * y1 + c * y2 + d * y3;
		
		return new Pair<>(mx, my);
	}
	
	public static Pair<Double, Double> bezierMidPoint(Curve c)
	{
		c.reset();
		
		Point p = new Point(c.firstMoveTo());
		
		List<double[]> arr = new ArrayList<>();
		
		while(c.hasMoreSegments())
		{
			Triple<Point,Point,Point> seg = c.nextSegment();
			
			arr.add(new double[] { p.x, p.y, seg.a.x, seg.a.y, seg.b.x, seg.b.y, seg.c.x, seg.c.y });
			
			p.x = seg.c.x;
			p.y = seg.c.y;
		}
		
		int ind = ((int) (arr.size() / 2)) - 1;
		if(ind < 0)
		{
			ind = 0;
		}
		double[] mid = arr.get(ind);
		return bezierMidPoint(mid[0], mid[1], mid[2], mid[3], mid[4], mid[5], mid[6], mid[7]);
	}
	
	public static void drawCurve(Graphics2D g, List<Point> points, Color clr)
	{
		GeneralPath path = new GeneralPath();
		path.moveTo(points.get(0).x, points.get(0).y);
		
		for(int i=1; i<points.size(); i+=3) 
		{
			int x1 = points.get(i).x;
			int y1 = points.get(i).y;
			
			int x2 = points.get(i+1).x;
			int y2 = points.get(i+1).y;
			
			int x3 = points.get(i+2).x;
			int y3 = points.get(i+2).y;
			path.curveTo(x1, y1, x2, y2, x3, y3);
		}
		if(clr != null)
		{
			g.setColor(clr);
		}
		g.draw(path);
	}
	
	public static void fillPoly(Graphics2D g, Triple<Point, Point, Point> arrow)
	{
		g.fillPolygon(new int[] {arrow.a.x, arrow.b.x, arrow.c.x} ,  new int[] {arrow.a.y, arrow.b.y, arrow.c.y}, 3);		
	}
	
	public static void drawLinesArrowhead(Graphics2D g, Point p1, Point p2)
	{
		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		
		Point2D dirv = new Point2D.Double(-dx, -dy);
		
		// Normalize
		double norm = Math.sqrt(dirv.getX() * dirv.getX() + dirv.getY() *dirv.getY());
		dirv.setLocation(dirv.getX() / norm, dirv.getY()/ norm);
		
		Point2D normalVector = new Point2D.Double(-dirv.getY(), dirv.getX());
		
		final double bit = 0;
		Point2D aBitFar = new Point2D.Double(p1.getX() + bit * dirv.getX(), p1.getY() + bit * dirv.getY());
		
		final double span = 6;
		Point2D p3 = new Point2D.Double(aBitFar.getX() + span *normalVector.getX(), aBitFar.getY() + span *normalVector.getY());
		Point2D p4 = new Point2D.Double(aBitFar.getX() - span *normalVector.getX(), aBitFar.getY() - span *normalVector.getY());
		
		g.fillPolygon(new int[] {(int) p2.getX(), (int) p3.getX(), (int) p4.getX()  } ,
				new int[] {(int) p2.getY(), (int) p3.getY(), (int) p4.getY() }, 3  );
	}
	
	public static void drawStringWrapped(Graphics2D g, String text, int x, int y, int w)
	{
		if(text.equals("")) { return; }
		FontRenderContext frc = g.getFontRenderContext();
		AttributedString source = new AttributedString(text);
		source.addAttribute(TextAttribute.FONT, g.getFont());
		AttributedCharacterIterator iter = source.getIterator();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(iter, frc);

		int xMargin = 5;
		float yDraw = y;
		float yGap = 3;
		
		lineMeasurer.setPosition(iter.getBeginIndex());
		while (lineMeasurer.getPosition() < iter.getEndIndex()) 
		{
			int next = lineMeasurer.nextOffset(w - xMargin);
			int limit = next;
			if (limit <= text.length())
			{
			  for (int i = lineMeasurer.getPosition(); i < next; ++i)
			  {
			    char c = text.charAt(i);
			    if (c == '\n')
			    {
			      limit = i + 1;
			      break;
			    }
			  }
			}
			
			TextLayout layout = lineMeasurer.nextLayout(w - xMargin, limit, false);
			
			yDraw += layout.getAscent();
			layout.draw(g, x + xMargin, yDraw);
			yDraw += layout.getDescent() + layout.getLeading() + yGap;
		}	
	}
	
	public static void saveImage(BufferedImage img, String path) throws IOException
    {
		File f = new File(path);
        ImageIO.write(img, "png", f);
    }
	
	public static BufferedImage createFilledImage(GraphicsConfiguration grfxConfig, int width, int height, Color color)
	{
		BufferedImage img = grfxConfig.createCompatibleImage(width, height,
				Transparency.TRANSLUCENT);
		Graphics2D g = img.createGraphics();
		g.setBackground(color);
		g.clearRect(0, 0, width, height);
		g.dispose();
		return img;
	}
	
	public static GraphicsConfiguration defaultGraphicsConfiguration()
    {
        GraphicsConfiguration gConfig = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        return gConfig;
    }
}
