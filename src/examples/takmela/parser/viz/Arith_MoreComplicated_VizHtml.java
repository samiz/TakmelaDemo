package examples.takmela.parser.viz;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.webdoc.parsing.SuccessTreeDisplay;
import takmela.viz.webdoc.parsing.WebTraceConfiguration;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class Arith_MoreComplicated_VizHtml
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		build();
	}

	public static void build()
			throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		String grammar = Utils.readAllFile("./takmela_examples/arithmetic_more_complicated/grammar.takmela");
		String inputStr = Utils.readAllFile("./takmela_examples/arithmetic_more_complicated/input1.txt");
		String startSymbol = "e";
		
		
		WebTraceConfiguration config = new WebTraceConfiguration();
		config.OutputPath = "./trace_html/parser_examples/arithmetic_more_complicated/index.html";
		config.ShowFullContinuationsInJoins = false;
		config.ShowFullContinuationsOnGraphEdges = false;
		config.ShowFullContinuationsInNewContinuations = false;
		config.ShowForestWithSuccessesInJoins = SuccessTreeDisplay.Count;
		config.GraphMargin = 10;
		config.TemplatePath = "./trace_templates/arithmetic_more_complicated.html";
		
		examples.utils.TestUtils.testWithHtmlTracer(grammar, inputStr, startSymbol, config);
	}
}
