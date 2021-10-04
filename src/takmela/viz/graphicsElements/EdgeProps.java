package takmela.viz.graphicsElements;

public class EdgeProps
{
	public double LabelX, LabelY;
	public String Label;
	public Curve Curve;
	
	public String toString() 
	{
		return String.format("edge(labelX=%s, labelY=%s, %scurve=%s)",
			LabelX, LabelY, Label, Curve); 
	}
}
