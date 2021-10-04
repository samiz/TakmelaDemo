package takmela.viz.test;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.graphicsElements.GraphPositions;
import takmela.viz.graphicsElements.ReadDot;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class TestReadDot
{
	public static void main(String[] args) throws IOException, LexerError, UniquenessException, GrammarParsingException
	{
		String dotCode = Utils.readAllFile("./test/graph.dotlayout");
		GraphPositions dot = ReadDot.parseDotLayout(dotCode, a->a);
		System.out.println(dot);
	}
}
