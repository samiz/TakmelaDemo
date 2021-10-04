package takmela.viz.graphicsElements;

public class NodeProps
{
	public double Height, Width;
	public double X, Y;
	public String Label;
	
	public String toString() { return String.format("n(w=%s, h=%s, x=%s, y=%s, label=\"%s\")", Width, Height, X, Y , Label); }
}
