package test.takmela.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import takmela.ast.Module;
import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.tree.Tree;
import takmela.tree.TreeUtils;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;
import static takmela.tree.TreeUtils.branchChild;

public class TestTreesWithEBNF
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		testStar("./test/ebnf.takmela", "./test/ebnf.star.input.1.txt", "testStar");
		line();
		testStar("./test/ebnf.takmela", "./test/ebnf.star.input.2.txt", "testStar");
		line();
		testPlus("./test/ebnf.takmela", "./test/ebnf.plus.input.1.txt", "testPlus");
		line();
		testPlus("./test/ebnf.takmela", "./test/ebnf.plus.input.2.txt", "testPlus");
		line();
		testQuestion("./test/ebnf.takmela", "./test/ebnf.question.input.1.txt", "testQuestion");
		line();
		testQuestion("./test/ebnf.takmela", "./test/ebnf.question.input.2.txt", "testQuestion");
		
	}

	private static void testStar(String grammarPath, String inputPath, String startSymbol) throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		System.out.println("## Test Star");
		String grammar = Utils.readAllFile(grammarPath);
		String inputStr = Utils.readAllFile(inputPath);
		
		List<String> outParseErrors = new ArrayList<>();
		Module mod = takmela.tool.Parse.parseGrammar(grammar, outParseErrors);
		takmela.tool.Parse.parse(mod, inputStr, startSymbol, 
		(_tree)-> {
			Tree root = (Tree) _tree;
			System.out.println(root);
			
			Tree startSym = branchChild(root, 0);
			Tree toTest = branchChild(startSym, 0);
			TreeUtils.enumStar(toTest, (i, t)->System.out.println(String.format("%s: %s", i, t)));
		},
		(expected, given, pos) -> {
				System.err.println(takmela.tool.Parse.defaultFailMessage(expected, given, pos));
		});
	}
	
	private static void testPlus(String grammarPath, String inputPath, String startSymbol) throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		System.out.println("## Test Plus");
		String grammar = Utils.readAllFile(grammarPath);
		String inputStr = Utils.readAllFile(inputPath);
		
		List<String> outParseErrors = new ArrayList<>();
		Module mod = takmela.tool.Parse.parseGrammar(grammar, outParseErrors);
		takmela.tool.Parse.parse(mod, inputStr, startSymbol, 
		(_tree)-> {
			Tree root = (Tree) _tree;
			System.out.println(root);
			
			Tree startSym = branchChild(root, 0);
			Tree toTest = branchChild(startSym, 0);
			TreeUtils.enumPlus(toTest, (i, t)->System.out.println(String.format("%s: %s", i, t)));
		},
		(expected, given, pos) -> {
				System.err.println(takmela.tool.Parse.defaultFailMessage(expected, given, pos));
		});
	}
	
	private static void testQuestion(String grammarPath, String inputPath, String startSymbol) throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		System.out.println("## Test Question");
		String grammar = Utils.readAllFile(grammarPath);
		String inputStr = Utils.readAllFile(inputPath);
		
		List<String> outParseErrors = new ArrayList<>();
		Module mod = takmela.tool.Parse.parseGrammar(grammar, outParseErrors);
		takmela.tool.Parse.parse(mod, inputStr, startSymbol, 
		(_tree)-> {
			Tree root = (Tree) _tree;
			System.out.println(root);
			
			Tree startSym = branchChild(root, 0);
			Tree toTest = branchChild(startSym, 0);
			TreeUtils.enumQuestion(toTest, (i, t)->System.out.println(String.format("%s: %s", i, t)));
		},
		(expected, given, pos) -> {
				System.err.println(takmela.tool.Parse.defaultFailMessage(expected, given, pos));
		});
	}
	
	private static void line()
	{
		System.out.println("-------------------------");
	}
}
