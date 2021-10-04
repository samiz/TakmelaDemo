package examples.takmela.parser.viz;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.webdoc.parsing.SuccessTreeDisplay;
import takmela.viz.webdoc.parsing.WebTraceConfiguration;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class Arith_Ambig_Complicated_VizHtml
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		build();
	}

	public static void build()
			throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		String grammar = Utils.readAllFile("./takmela_examples/arith_ambig_complicated/grammar.takmela");
		String inputStr = Utils.readAllFile("./takmela_examples/arith_ambig_complicated/input1.txt");
		String startSymbol = "e";
		
		
		WebTraceConfiguration config = new WebTraceConfiguration();
		config.OutputPath = "./trace_html/parser_examples/arith_ambig_complicated/index.html";
		config.ShowFullContinuationsInJoins = true;
		config.ShowFullContinuationsOnGraphEdges = true;
		config.ShowFullContinuationsInNewContinuations = false;
		config.ShowFullContinuationsInWorklist = true;
		config.ShowForestWithSuccessesInJoins = SuccessTreeDisplay.Full;
		config.GraphMargin = 15;
		config.ConnectEdgeLabelToEdge = true;
		config.TemplatePath = "./trace_templates/arith_ambig_complicated.html";
		
		examples.utils.TestUtils.testWithHtmlTracer(grammar, inputStr, startSymbol, config);
	}
}
