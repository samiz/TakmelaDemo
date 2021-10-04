package takmela.viz.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.graphicsElements.DrawingUtils;
import takmela.viz.graphicsElements.GraphPositions;
import takmela.viz.graphicsElements.ReadDot;
import takmela.viz.graphicsElements.RenderDotData;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

// To make sure we can read a dot file correctly, we could render it!
// This class and its associated 'RenderDotData' aren't part of
// the production code
public class TestRenderDot
{
	public static void main(String[] args) throws IOException, LexerError, UniquenessException, GrammarParsingException
	{
		String dotCode = Utils.readAllFile("./test/graph.dotlayout");
		GraphPositions dot = ReadDot.parseDotLayout(dotCode, a->a);
		System.out.println(dot);
		
		String dir = "./test_output";new File(dir).mkdirs();
		new File(dir).mkdirs();
				
		BufferedImage img = RenderDotData.render(dot, DrawingUtils.defaultGraphicsConfiguration());
		DrawingUtils.saveImage(img, "./test_output/renderDot.png");
	}
}
