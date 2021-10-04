package examples.takmela.parser.viz;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.webdoc.parsing.WebTraceConfiguration;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class NullableNonterminalBugSimpleVizHtml
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		build();
	}

	public static void build() throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException
	{
		String grammar = Utils.readAllFile("./takmela_examples/nullable_nonterminal_bug_simple/grammar.takmela");
		String inputStr = Utils.readAllFile("./takmela_examples/nullable_nonterminal_bug_simple/input1.txt");
		String startSymbol = "s";
		
		WebTraceConfiguration config = new WebTraceConfiguration();
		config.OutputPath = "./trace_html/parser_examples/nullable_nonterminal_bug_simple/index.html";
		config.ShowFullContinuationsInJoins = true;
		config.ShowFullContinuationsOnGraphEdges = true;
		config.TemplatePath = "./trace_templates/nullable_nonterminal_bug_simple.html";
		config.GraphMargin = 15;
		
		examples.utils.TestUtils.testWithHtmlTracer(grammar, inputStr, startSymbol, config);
	}
}
