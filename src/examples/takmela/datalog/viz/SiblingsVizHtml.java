package examples.takmela.datalog.viz;

import java.io.IOException;
import examples.utils.TestUtilsDatalog;
import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.webdoc.logic.WebTraceConfiguration;
import takmelogic.parser.DatalogParsingException;
import utils_takmela.UniquenessException;

public class SiblingsVizHtml
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, DatalogParsingException, GrammarParsingException, InterruptedException
	{
		build();
	}

	public static void build() throws IOException, UniquenessException, LexerError, GrammarParsingException,
			InterruptedException, DatalogParsingException
	{
		String datalogProgramPath = "./takmelogic_examples/siblings_viz/siblings.dlog";
		String query = "sibling";
		Object[] queryArgs = new Object[] {null, null}; // null -> output parameter
		
		WebTraceConfiguration config = new WebTraceConfiguration();
		config.OutputPath = "./trace_html/datalog_examples/siblings/index.html";
		config.ShowFullContinuationsOnGraphEdges = true;
		config.ShowFullContinuationsInJoins = true;
		config.TemplatePath = "./trace_templates/siblings.html";
		
		TestUtilsDatalog.testWithHtmlTracer(datalogProgramPath, query, queryArgs, config);
	}
}
