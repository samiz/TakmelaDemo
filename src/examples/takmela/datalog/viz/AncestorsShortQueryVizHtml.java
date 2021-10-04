package examples.takmela.datalog.viz;

import java.io.IOException;
import examples.utils.TestUtilsDatalog;
import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.webdoc.logic.WebTraceConfiguration;
import takmelogic.parser.DatalogParsingException;
import utils_takmela.UniquenessException;

public class AncestorsShortQueryVizHtml
{

	public static void main(String[] args) throws IOException, UniquenessException, LexerError, DatalogParsingException, GrammarParsingException, InterruptedException
	{
		build();
	}

	public static void build() throws IOException, UniquenessException, LexerError, GrammarParsingException,
			InterruptedException, DatalogParsingException
	{

		String datalogProgramPath = "./takmelogic_examples/ancestors_shortquery_viz/ancestors.dlog";
		String query = "a";
		Object[] queryArgs = new Object[] {"b", null}; // null -> output parameter
		
		WebTraceConfiguration config = new WebTraceConfiguration();
		config.OutputPath = "./trace_html/datalog_examples/ancestors_shortquery/index.html";
		config.ShowFullContinuationsOnGraphEdges = true;
		config.ShowFullContinuationsInJoins = true;
		config.GraphMargin = 10;
		config.ConnectEdgeLabelToEdge = true;
		config.TemplatePath = "./trace_templates/ancestors_shortquery.html";
		
		TestUtilsDatalog.testWithHtmlTracer(datalogProgramPath, query, queryArgs, config);
	}
}
