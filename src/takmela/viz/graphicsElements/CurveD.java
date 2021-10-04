package takmela.viz.graphicsElements;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;

import utils_takmela.Pair;
import utils_takmela.Triple;
import utils_takmela.Utils;

public class CurveD implements Curve
{
	private int i;
	private List<Point> points;
	private Pair<Double, Double> endP;
	
	public CurveD(List<Point> points, Pair<Double, Double> endP)
	{
		this.points = points;
		this.endP = endP;
		reset();
	}
	
	@Override public void reset()
	{
		i = 1;
	}

	@Override  public Point firstMoveTo()
	{
		return points.get(0);
	}
	
	@Override public boolean hasMoreSegments()
	{
		return i<points.size();
	}

	@Override public Triple<Point, Point, Point> nextSegment()
	{
		Triple<Point, Point, Point> result = new Triple<>(points.get(i), points.get(i+1), points.get(i+2));
		i+=3;
		return result;
	}

	@Override public Triple<Point, Point, Point> arrowHead()
	{
		Point p1;
		Point p2;
		
		if(endP != null)
		{
			p1 = Utils.last(points);
			p2 = new Point((int) (double) endP.a, (int) (double) endP.b);
			
		}
		else
		{
			p1 = points.get(points.size()-2);
			p2 = Utils.last(points);
		}
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
		
		return new Triple<>(toPoint(p2), toPoint(p3), toPoint(p4));
	}
	
	private Point toPoint(Point2D p)
	{
		return new Point((int) p.getX(), (int) p.getY());
	}
	
	public String toString()
	{
		return String.format("%s; endP=%s", points, formatEndP());
	}
	
	private String formatEndP()
	{
		return String.format("(%s, %s),", endP.a, endP.b);
	}

	@Override public void invert(double h)
	{
		if(endP != null)
		{
			endP = new Pair<>(endP.a, h - endP.b);
		}
		for(Point p : points)
		{
			p.setLocation(p.x, h- p.y);
		}		
	}

	@Override public void nudge(int dx, int dy)
	{
		if(endP != null)
		{
			endP = new Pair<>(endP.a + dx, endP.b + dy);
		}
		for(Point p : points)
		{
			p.setLocation(p.x +dx, p.y + dy);
		}
	}
}
