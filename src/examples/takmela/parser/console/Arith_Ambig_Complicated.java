package examples.takmela.parser.console;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class Arith_Ambig_Complicated
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		String grammar = Utils.readAllFile("./takmela_examples/arith_ambig_complicated/grammar.takmela");
		String inputStr = Utils.readAllFile("./takmela_examples/arith_ambig_complicated/input1.txt");
		String startSymbol = "expr";
		
		examples.utils.TestUtils.testWithoutTracing(grammar, inputStr, startSymbol);
	}
}
