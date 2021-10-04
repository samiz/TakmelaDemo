package examples.takmela.parser.viz;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.webdoc.parsing.SuccessTreeDisplay;
import takmela.viz.webdoc.parsing.WebTraceConfiguration;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class Arith_OnePlusTwo_VizHtml
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		build();
	}
	
	public static void build()
			throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		String grammar = Utils.readAllFile("./takmela_examples/arith_one_plus_two/grammar.takmela");
		String inputStr = Utils.readAllFile("./takmela_examples/arith_one_plus_two/input1.txt");
		String startSymbol = "e";
		
		WebTraceConfiguration config = prepapeOnePlusTwoConfiguration();
		
		examples.utils.TestUtils.testWithHtmlTracer(grammar, inputStr, startSymbol, config);
	}

	public static WebTraceConfiguration prepapeOnePlusTwoConfiguration()
	{
		WebTraceConfiguration config = new WebTraceConfiguration();
		config.OutputPath = "./trace_html/parser_examples/arith_one_plus_two/index.html";
		config.ShowFullContinuationsInJoins = false;
		config.ShowFullContinuationsOnGraphEdges = false;
		config.ShowFullContinuationsInProcessCall = false;
		config.ShowForestWithSuccessesInJoins = SuccessTreeDisplay.None;
		config.TemplatePath = "./trace_templates/arith_one_plus_two.html";
		return config;
	}
}
