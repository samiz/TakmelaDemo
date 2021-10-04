package examples.takmela.parser.viz;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.webdoc.parsing.SuccessTreeDisplay;
import takmela.viz.webdoc.parsing.WebTraceConfiguration;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class LeftRecursion_Indirect_VizHtml
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		build();
	}
	
	public static void build()
			throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		String grammar = Utils.readAllFile("./takmela_examples/left_recursion_indirect/grammar.takmela");
		String inputStr = Utils.readAllFile("./takmela_examples/left_recursion_indirect/input1.txt");
		String startSymbol = "expr";
		
		WebTraceConfiguration config = new WebTraceConfiguration();
		config.OutputPath = "./trace_html/parser_examples/left_recursion_indirect/index.html";
		config.ShowFullContinuationsInJoins = false;
		config.ShowFullContinuationsOnGraphEdges = false;
		config.ShowFullContinuationsInProcessCall = false;
		config.ShowTreesWithSuccesses = true;
		config.ShowForestWithSuccessesInJoins = SuccessTreeDisplay.None;
		config.ShowForestWithSuccessesInWorklist = SuccessTreeDisplay.None;
		config.ConnectEdgeLabelToEdge = true;
		config.GraphMaxEdgeLabelWidth = 135;
		
		config.TemplatePath = "./trace_templates/left_recursion_indirect.html";
		
		examples.utils.TestUtils.testWithHtmlTracer(grammar, inputStr, startSymbol, config);
	}
}
