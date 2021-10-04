package examples.takmela.build;

import java.io.IOException;

import examples.takmela.parser.viz.Arith_OnePlusTwo_VizHtml;
import examples.utils.TestUtils;
import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.webdoc.parsing.WebTraceConfiguration;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class BuildMainArticle
{

	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		build();
	}

	public static void build() throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		String onePlusTwoTrace = renderOnePlusTwo();
		
		String templ = Utils.readAllFile("./trace_templates/takmela_article.html");
		String html = templ.replace("{{trace_one_plus_two}}", onePlusTwoTrace);
		Utils.writeToFile("./trace_html/index.html", html);
		
		System.out.println("Generated main article in ./trace_html/index.html");		
	}

	private static String renderOnePlusTwo() throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		String grammar = Utils.readAllFile("./takmela_examples/arith_one_plus_two/grammar.takmela");
		String inputStr = Utils.readAllFile("./takmela_examples/arith_one_plus_two/input1.txt");
		String startSymbol = "e";
		
		WebTraceConfiguration config = Arith_OnePlusTwo_VizHtml.prepapeOnePlusTwoConfiguration();
		config.GraphImgPathPrefix = "./parser_examples/arith_one_plus_two/";
		String html = TestUtils.renderHtmlTrace(grammar, inputStr, startSymbol, config, null, false);
		return html;
	}
}
