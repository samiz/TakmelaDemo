package examples.takmela.parser.viz;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.webdoc.parsing.SuccessTreeDisplay;
import takmela.viz.webdoc.parsing.WebTraceConfiguration;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class Arith_OnePlusTwoPlusThree_VizHtml
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		build();
	}
	
	public static void build()
			throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		String grammar = Utils.readAllFile("./takmela_examples/arith_one_plus_two_plus_three/grammar.takmela");
		String inputStr = Utils.readAllFile("./takmela_examples/arith_one_plus_two_plus_three/input1.txt");
		String startSymbol = "expr";
	
		
		WebTraceConfiguration config = new WebTraceConfiguration();
		config.OutputPath = "./trace_html/parser_examples/arith_one_plus_two_plus_three/index.html";
		config.ShowFullContinuationsInJoins = false;
		config.ShowFullContinuationsOnGraphEdges = false;
		config.ShowForestWithSuccessesInJoins = SuccessTreeDisplay.None;
		config.TemplatePath = "./trace_templates/arith_one_plus_two_plus_three.html";
		config.GraphMargin = 15;
		config.GraphMaxEdgeLabelWidth = 140;
		
		examples.utils.TestUtils.testWithHtmlTracer(grammar, inputStr, startSymbol, config);
	}
}
