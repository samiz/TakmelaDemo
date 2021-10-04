package examples.takmela.datalog.console;

import java.io.IOException;

import takmela.lexer.LexerError;
import takmelogic.parser.DatalogParsingException;
import takmelogic.tool.RunDatalog;
import takmelogic.tool.TakmelogicEngineConfiguration;
import utils_takmela.UniquenessException;

public class Siblings
{
	public static void main(String[] args) throws IOException, UniquenessException, LexerError, DatalogParsingException
	{
		TakmelogicEngineConfiguration config = new TakmelogicEngineConfiguration();
		
		String query = "sibling";
		Object[] queryArgs = new Object[] {null, null}; // null -> output parameter
		
		RunDatalog.runStdOut("./takmelogic_examples/siblings/siblings.dlog", 
				query, queryArgs, config);
	}
}
