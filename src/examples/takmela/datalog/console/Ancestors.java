package examples.takmela.datalog.console;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmelogic.parser.DatalogParsingException;
import takmelogic.tool.RunDatalog;
import takmelogic.tool.TakmelogicEngineConfiguration;
import utils_takmela.UniquenessException;

public class Ancestors
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, DatalogParsingException
	{
		TakmelogicEngineConfiguration config = new TakmelogicEngineConfiguration();
		
		String query = "ancestor";
		Object[] queryArgs = new Object[] {null, "c"}; // null -> output parameter
		
		RunDatalog.runStdOut("./takmelogic_examples/ancestors/ancestors.dlog", 
				query, queryArgs, config);
	}
}
