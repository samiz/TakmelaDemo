package examples.takmela.parser.viz;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.webdoc.parsing.WebTraceConfiguration;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class LeftRecursion_Nullable_VizHtml
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		build();
	}
	
	public static void build()
			throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		String grammar = Utils.readAllFile("./takmela_examples/left_recursion_nullable/grammar.takmela");
		String inputStr = Utils.readAllFile("./takmela_examples/left_recursion_nullable/input1.txt");
		String startSymbol = "a";
		
		WebTraceConfiguration config = new WebTraceConfiguration();
		config.OutputPath = "./trace_html/parser_examples/left_recursion_nullable/index.html";
		config.ShowFullContinuationsInJoins = false;
		config.ShowFullContinuationsOnGraphEdges = false;
		config.TemplatePath = "./trace_templates/left_recursion_nullable.html";
		config.GraphMargin = 10;
		config.ConnectEdgeLabelToEdge = true;
		
		examples.utils.TestUtils.testWithHtmlTracer(grammar, inputStr, startSymbol, config);
	}
}
