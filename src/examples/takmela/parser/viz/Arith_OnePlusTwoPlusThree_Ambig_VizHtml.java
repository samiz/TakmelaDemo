package examples.takmela.parser.viz;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.webdoc.parsing.WebTraceConfiguration;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class Arith_OnePlusTwoPlusThree_Ambig_VizHtml
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		build();
	}
	
	public static void build()
			throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		String grammar = Utils.readAllFile("./takmela_examples/arith_one_plus_two_plus_three_ambig/grammar.takmela");
		String inputStr = Utils.readAllFile("./takmela_examples/arith_one_plus_two_plus_three_ambig/input1.txt");
		String startSymbol = "expr";
		
		WebTraceConfiguration config = new WebTraceConfiguration();
		config.OutputPath = "./trace_html/parser_examples/arith_one_plus_two_plus_three_ambig/index.html";
		config.ShowFullContinuationsInJoins = true;
		config.ShowFullContinuationsOnGraphEdges = true;
		config.GraphMargin = 30;
		config.TemplatePath = "./trace_templates/arith_one_plus_two_plus_three_ambig.html";
		
		examples.utils.TestUtils.testWithHtmlTracer(grammar, inputStr, startSymbol, config);
	}
}
