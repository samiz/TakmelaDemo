package takmela.viz.webdoc.logic;

import takmela.viz.StagedGraphRender;

public class WebTraceConfiguration
{
	// Should we show the full continuation (including e.g variable bindings)
	// or just the CodePos part?
	public boolean ShowFullContinuationsOnGraphEdges = true;
	
	// Same, but in the trace column rather than the graph
	public boolean ShowFullContinuationsInJoins = true;
	
	// etc
	public boolean ShowFullContinuationsInWorklist = true;
		

	public String OutputPath = "/home/m/Downloads/y_test/takmelademo/dom.html";
	
	// If the TemplatePath is null, the renderer will generate a full HTML file
	// otherwise it will generate the trace in a <div> and insert it in the template
	public String TemplatePath = null;

	// Remove the CSS max-height limit on the pane where successes are displayed
	public boolean BiigSuccessesPanel = false;

	public int GraphMargin = StagedGraphRender.DefaultMargin; 
	
	public boolean ConnectEdgeLabelToEdge = false;
	
	public int GraphMaxEdgeLabelWidth = 0;
}
