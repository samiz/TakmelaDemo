package examples.takmela.build;

import java.io.IOException;

import examples.takmela.datalog.viz.AncestorsShortQueryVizHtml;
import examples.takmela.datalog.viz.AncestorsMediumQueryVizHtml;
import examples.takmela.datalog.viz.AncestorsVizHtml;
import examples.takmela.datalog.viz.RsgAbridgedVizHtml;
import examples.takmela.datalog.viz.RsgVizHtml;
import examples.takmela.datalog.viz.SelfMatchVizHtml;
import examples.takmela.datalog.viz.SiblingsVizHtml;
import examples.takmela.parser.viz.Arith_Ambig_Complicated_VizHtml;
import examples.takmela.parser.viz.Arith_MoreComplicated_VizHtml;
import examples.takmela.parser.viz.Arith_OnePlusTwoPlusThree_Ambig_VizHtml;
import examples.takmela.parser.viz.Arith_OnePlusTwoPlusThree_VizHtml;
import examples.takmela.parser.viz.Arith_OnePlusTwo_VizHtml;
import examples.takmela.parser.viz.NullableNonterminalBugSimpleVizHtml;
import examples.takmela.parser.viz.NullableNonterminalBugVizHtml;
import examples.takmela.parser.viz.LeftRecursion_Indirect_VizHtml;
import examples.takmela.parser.viz.LeftRecursion_Nullable_VizHtml;
import takmela.lexer.LexerError;
import takmela.metaparser.GrammarParsingException;
import takmelogic.parser.DatalogParsingException;
import utils_takmela.UniquenessException;

public class BuildAllHtml
{

	public static void main(String[] args) throws IOException, UniquenessException, LexerError, GrammarParsingException, InterruptedException, DatalogParsingException
	{
		// Takmela parser examples
		Arith_OnePlusTwo_VizHtml.build();
		Arith_MoreComplicated_VizHtml.build();
		Arith_Ambig_Complicated_VizHtml.build();
		Arith_OnePlusTwoPlusThree_VizHtml.build();
		Arith_OnePlusTwoPlusThree_Ambig_VizHtml.build();
		LeftRecursion_Indirect_VizHtml.build();
		LeftRecursion_Nullable_VizHtml.build();
		
		NullableNonterminalBugVizHtml.build();
		NullableNonterminalBugSimpleVizHtml.build();
		
		// Takmelogic examples
		AncestorsMediumQueryVizHtml.build();
		AncestorsShortQueryVizHtml.build();
		AncestorsVizHtml.build();
		SiblingsVizHtml.build();
		RsgAbridgedVizHtml.build();
		RsgVizHtml.build();
		SelfMatchVizHtml.build();
		
		//Articles
		BuildMainArticle.build();
		
		System.out.println("\n---------------\nFinished generating all traces");
	}

}
