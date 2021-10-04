package takmela.viz.webdoc.parsing;

import takmela.viz.StagedGraphRender;

public class WebTraceConfiguration
{
	// Show the full continuation (including parse forest so far)
	// or just the CodePos?
	public boolean ShowFullContinuationsInJoins = true;

	// Same as above, but for the graph on the left
	public boolean ShowFullContinuationsOnGraphEdges = true;
	
	// Same, but in "Worklist - continuations" pane
	public boolean ShowFullContinuationsInWorklist = false;
	
	public boolean ShowFullContinuationsInNewContinuations = true;

	public boolean ShowFullContinuationsInProcessCall = true;
	
	// Show the success tree or only the input position after success?
	// Applies to the successes pane under the graph
	public boolean ShowTreesWithSuccesses = true;
	
	// // Either show a success with full parse trees, e.g a/1 -> (5, a(b('c')), a(x('y')))
	// or just show the number of trees, e.g a/1 -> (5, 2木).
	// Applies to the 'Worklist - successes' section
	public SuccessTreeDisplay ShowForestWithSuccessesInWorklist = SuccessTreeDisplay.Full;
	
	// Same, but in "JoinS" and "JoinK" views
	public SuccessTreeDisplay ShowForestWithSuccessesInJoins = SuccessTreeDisplay.Full;
	
	// Needs a lot of adjusting to work, not used right now
	public boolean TreeDepthShift = false;
	
	public String OutputPath = "/home/m/Downloads/y_test/takmelademo/dom.html";
	
	public String GraphImgPathPrefix = "";
	
	// If the TemplatePath is null, the renderer will generate a full HTML file
	// otherwise it will generate the trace in a <div> and insert it in the template
	public String TemplatePath = null;

	public int GraphMargin = StagedGraphRender.DefaultMargin;
	
	public boolean ConnectEdgeLabelToEdge = false;
	
	public int GraphMaxEdgeLabelWidth = 0;
}
