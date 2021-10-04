package examples.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import takmela.ast.Module;
import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.GradualGraph;
import takmela.viz.StagedGraphRender;
import takmela.viz.graphicsElements.DrawingUtils;
import takmela.viz.webdoc.parsing.RenderVizDomToHtml;
import takmela.viz.webdoc.parsing.TraceDOM;
import takmela.viz.webdoc.parsing.WebDocTracer;
import takmela.viz.webdoc.parsing.WebTraceConfiguration;
import utils_takmela.Box;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class TestUtils
{
	public static void testWithoutTracing(String grammar, String inputStr, String startSymbol)
			throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		List<String> outParseErrors = new ArrayList<>();
		Module mod = takmela.tool.Parse.parseGrammar(grammar, outParseErrors);
		takmela.tool.Parse.parse(mod, inputStr, startSymbol, 
		(tree)-> {
			System.out.println(tree);
		},
		(expected, given, pos) -> {
				System.err.println(takmela.tool.Parse.defaultFailMessage(expected, given, pos));
		});
	}
	
	public static void testWithHtmlTracer(String grammar, String inputStr, String startSymbol, WebTraceConfiguration config)
			throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		
		Box<TraceDOM> traceDom = new Box<>(null);
		
		String html = renderHtmlTrace(grammar, inputStr, startSymbol, config, traceDom, true);
		
		String dir = Utils.parentDir(config.OutputPath);
		new File(dir).mkdirs();

		Utils.writeToFile(config.OutputPath, html);
		
		int margin = config.GraphMargin;
		
		GradualGraph graph = traceDom.Value.GraphStages;
		
		List<BufferedImage> images = StagedGraphRender.positionAndRenderDefault(graph.Nodes, graph.Edges, graph.NodeStages, graph.EdgeStages, graph.maxStage, margin,
				config.ConnectEdgeLabelToEdge, config.GraphMaxEdgeLabelWidth);
		
		
		String imgSavePath = dir;
		
		System.out.println("Saving trace to " + dir);
		
		for(int i=0; i<images.size(); ++i)
		{
			DrawingUtils.saveImage(images.get(i), Utils.combinePath(imgSavePath, "trace") + Utils.pad(i, 3) + ".png");
		}
	}
	
	public static String renderHtmlTrace(String grammar, String inputStr, String startSymbol, 
			WebTraceConfiguration config, Box<TraceDOM> outTraceDom, boolean fullHtmlFile) throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		WebDocTracer tracer = new WebDocTracer(config.ShowFullContinuationsOnGraphEdges);
		
		List<String> outParseErrors = new ArrayList<>();
		Module mod = takmela.tool.Parse.parseGrammar(grammar, outParseErrors );
		takmela.tool.Parse.parse(mod, tracer, inputStr, startSymbol, 
		(tree)-> {
			System.out.println(tree);
		},
		(expected, given, pos) -> {
				System.err.println(takmela.tool.Parse.defaultFailMessage(expected, given, pos));
		});
		
		TraceDOM dom = tracer.getTraceDom();
		if(outTraceDom != null)
		{
			outTraceDom.Value = dom;
		}
		
		String html = fullHtmlFile? RenderVizDomToHtml.render(dom, config) : RenderVizDomToHtml.renderDiv(dom, config);
		return html;
	}
}
