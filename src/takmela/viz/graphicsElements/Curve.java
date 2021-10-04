package takmela.viz.graphicsElements;

import java.awt.Point;

import utils_takmela.Triple;

public interface Curve
{
	void reset();
	boolean hasMoreSegments();
	
	Point firstMoveTo();
	Triple<Point, Point, Point> nextSegment();
	
	// The first point is the tip of the arrow
	Triple<Point, Point, Point> arrowHead();
	
	void invert(double h);
	void nudge(int dx, int dy);
}
