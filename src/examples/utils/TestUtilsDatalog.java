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
import takmela.viz.webdoc.logic.RenderVizDomToHtml;
import takmela.viz.webdoc.logic.TraceDOM;
import takmela.viz.webdoc.logic.WebDocTracerDatalog;
import takmela.viz.webdoc.logic.WebTraceConfiguration;
import takmelogic.engine.TakmelaDatalogEngine;
import takmelogic.parser.DatalogParsingException;
import takmelogic.tool.RunDatalog;
import takmelogic.tool.TakmelogicEngineConfiguration;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class TestUtilsDatalog
{
	public static void testWithoutTracing(String grammar, String inputStr, String startSymbol)
			throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		List<String> outParseErrors = new ArrayList<>();
		Module mod = takmela.tool.Parse.parseGrammar(grammar, outParseErrors );
		takmela.tool.Parse.parse(mod, inputStr, startSymbol, 
		(tree)-> {
			System.out.println(tree);
		},
		(expected, given, pos) -> {
				System.err.println(takmela.tool.Parse.defaultFailMessage(expected, given, pos));
		});
	}
	
	public static void testWithHtmlTracer(String datalogProgramPath, String query, Object[] queryArgs, WebTraceConfiguration traceConfig)
			throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException, DatalogParsingException
	{
		WebDocTracerDatalog tracer = new WebDocTracerDatalog(traceConfig.ShowFullContinuationsOnGraphEdges);
		
		TakmelogicEngineConfiguration config = new TakmelogicEngineConfiguration();
		config.Tracing = true;
		config.UseTracer = tracer;
		config.onFinish = (TakmelaDatalogEngine e) -> { 
			try
			{
				TraceDOM dom = tracer.getTraceDom();
				GradualGraph graph = dom.GraphStages;
				
				String html = RenderVizDomToHtml.render(dom, traceConfig);
				
				String dir = Utils.parentDir(traceConfig.OutputPath);
				
				new File(dir).mkdirs();
				
				Utils.writeToFile(traceConfig.OutputPath, html);
				
				int margin = traceConfig.GraphMargin;
				
				List<BufferedImage> images = StagedGraphRender.positionAndRenderDefault(graph.Nodes, graph.Edges, graph.NodeStages, graph.EdgeStages, graph.maxStage,
						margin, traceConfig.ConnectEdgeLabelToEdge, traceConfig.GraphMaxEdgeLabelWidth);

				String imgSavePath = dir;
				
				System.out.println("Writing trace to " + dir);
				for(int i=0; i<images.size(); ++i)
				{
					DrawingUtils.saveImage(images.get(i), Utils.combinePath(imgSavePath, "trace") + Utils.pad(i, 3) + ".png");
				} 
			}
			catch(IOException | LexerError | UniquenessException | GrammarParsingException | InterruptedException 
					
					ex )
			{
				ex.printStackTrace();
			}
		};
		
		RunDatalog.runStdOut(datalogProgramPath, query,queryArgs, config);
	}
}
