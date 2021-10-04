package takmela.viz.webdoc.logic;

import java.util.List;

import takmela.viz.GradualGraph;
import takmela.viz.webdoc.tdom.datalog.Iteration;

public class TraceDOM
{
	public Iteration InitialTopLevelCalls;
	public List<Iteration> Iterations;
	public GradualGraph GraphStages;
	
	public TraceDOM(Iteration initialTopLevelCalls, List<Iteration> iterations, GradualGraph gradualGraph)
	{
		InitialTopLevelCalls = initialTopLevelCalls;
		Iterations = iterations;
		GraphStages = gradualGraph;
	}
}
