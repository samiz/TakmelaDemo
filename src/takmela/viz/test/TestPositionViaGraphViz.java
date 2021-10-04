package takmela.viz.test;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmela.viz.GradualGraphBuilder;
import takmela.viz.StagedGraphRender;
import takmela.viz.graphicsElements.GraphPositions;
import utils_takmela.UniquenessException;

public class TestPositionViaGraphViz
{
	public static void main(String[] args) throws IOException, LexerError, UniquenessException, GrammarParsingException, InterruptedException
	{
		GradualGraphBuilder g = new GradualGraphBuilder();
		g.node("a");
		g.node("b");
		g.node("c");
		
		g.edge("a", "b", "a to b");
		g.edge("b", "c", "b to c");
		g.edge("c", "a", "c to a");
		
		GraphPositions positions = StagedGraphRender.positionDot(g.Nodes, g.Edges, StagedGraphRender.DefaultMargin);
		System.out.println(positions);
	}
}
