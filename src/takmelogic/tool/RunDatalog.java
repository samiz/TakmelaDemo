package takmelogic.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import takmela.lexer.LexerError;
import takmela.lexer.Token;
import takmelogic.ast.Fact;
import takmelogic.ast.Rule;
import takmelogic.ast.TopLevel;
import takmelogic.engine.InterpreterTools;
import takmelogic.engine.S;
import takmelogic.engine.TakmelaDatalogEngine;
import takmelogic.parser.DatalogParsingException;
import takmelogic.parser.TakmelogicLexer;
import takmelogic.parser.TakmelogicParser;
import utils_takmela.Box;
import utils_takmela.Pair;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class RunDatalog
{
	public static void runStdOut(String fileName, String call, Object[] args) throws IOException, UniquenessException, LexerError, DatalogParsingException
	{
		runStdOut(fileName, call, args, null);
	}
	
	public static void runStdOut(String fileName, String call, Object[] args,
			TakmelogicEngineConfiguration config) throws IOException, UniquenessException, LexerError, DatalogParsingException
	{
		runStdOut(fileName, new HashMap<>(), call, args, config);
	}
	
	public static void runStdOut(String fileName,  Map<String, Set<List<Object>>> extraFacts, String call, Object[] args) throws IOException, UniquenessException, LexerError, DatalogParsingException
	{
		runStdOut(fileName, extraFacts, call, args, null);
	}
	
	public static void runStdOut(String fileName,  Map<String, Set<List<Object>>> extraFacts,
			String call, Object[] args, TakmelogicEngineConfiguration config) throws IOException, UniquenessException, LexerError, DatalogParsingException
	{
		String output = runStr(fileName, extraFacts, call, args, config);
		System.out.println(String.format("All results of query %s(%s) are [%s]", 
				call, 
				Utils.joinMap(args, ", ", a->repr(a)), 
				output));
	}
	
	public static String runStr(String fileName,  Map<String, Set<List<Object>>> extraFacts, 
			String call, Object[] args) throws UniquenessException, LexerError, DatalogParsingException, IOException
	{
		return runStr(fileName, extraFacts, call, args, null);
	}
	
	public static String runStr(String fileName,  Map<String, Set<List<Object>>> extraFacts, 
			String call, Object[] args, TakmelogicEngineConfiguration config) throws IOException, UniquenessException, LexerError, DatalogParsingException
	{
		List<List<Object>> allTuples = runTuples(fileName, extraFacts, call, args, config);
		return Utils.joinMap(allTuples, ", ", x->"(" + Utils.joinMap(x, ", ", a->repr(a)) +")");
	}
	
	public static List<List<Object>> runTuples(String fileName,  Map<String, Set<List<Object>>> extraFacts, String call, Object[] args,
			TakmelogicEngineConfiguration config) throws IOException, UniquenessException, LexerError, DatalogParsingException
	{
		takmelogic.ast.Module m = load(fileName);
		
		Box<Map<String, List<List<Object>>>> outFacts = new Box<>(null);
		Map<String, List<Rule>> namedRules = prepareNamedRules(m, outFacts);
		
		for(Entry<String, Set<List<Object>>> kv : extraFacts.entrySet())
		{
			Utils.addAllMapList(outFacts.Value, kv.getKey(), kv.getValue());
		}
		
		List<List<Object>> allTuples = new ArrayList<>();
		
		
		TakmelaDatalogEngine runner = new TakmelaDatalogEngine(namedRules, outFacts.Value);
		
		if(config != null && config.Tracing) { runner.setTracer(config.UseTracer); }
		
		List<Object> _args = new ArrayList<>();
		for(Object arg : args)
		{
			_args.add(arg);
		}
		runner.query(call, _args, (tuple)->{
			System.out.println(String.format("- %s(%s)", call, Utils.joinMap(tuple, ", ", a->repr(a))));
			allTuples.add(tuple);
		});
		
		if(config != null && config.onFinish != null) { config.onFinish.accept(runner); }
		return allTuples;
	}
	
	public static String factRepr(Pair<String, List<Object>> fact)
	{
		return factRepr(fact.a, fact.b);
	}
	
	public static String factRepr(String functor, List<Object> terms)
	{
		return String.format("%s(%s).", functor, Utils.joinMap(terms, ", ", a->repr(a)));
	}
	
	private static String repr(Object a)
	{
		if(a == null)
		{
			return "null";
		}
		else if(a instanceof Integer)
		{
			return Integer.toString((int) a);
		}
		else if(a instanceof S)
		{
			return ((S) a).Value;
		}
		else if(a instanceof String)
		{
			return "\"" + StrEscape(a.toString()) + "\"";
		}
		else
		{
			throw new RuntimeException("Option not handled: " + a);
		}
	}

	private static String StrEscape(String str)
	{
		/*
		// A poor man's buggy insecure JSON escape
		String verTab = Character.toString((char) 0xb);
		String ret = str
				.replace("\\", "\\\\")
				.replace("\f", "\\f")
				.replace("\b", "\\b")
				.replace(verTab, "")  // who needs a vertical tab anyway
				.replace("\t", "\\t")
				.replace("\r", "\\r")
				.replace("\n", "\\n")
				.replace("\u001f", "\\u001f") // TODO more about control chars and escaping
				.replace("\"", "\\\""); 
		return ret;
		//*/
		
        if (str == null || str.length() == 0)
        {
            return "";
        }

        char         c = 0;
        int          i;
        int          len = str.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String       t;

        for (i = 0; i < len; i += 1) 
        {
            c = str.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
               sb.append("\\r");
               break;
            default:
                if (c < ' ') 
                {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                }
                else
                {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
	}

	private static Map<String, List<Rule>> prepareNamedRules(takmelogic.ast.Module m, Box<Map<String,List<List<Object>>>> outFacts)
	{
		Map<String, List<Rule>> rules = new HashMap<>();
		Map<String, List<List<Object>>> facts = new HashMap<>();
		outFacts.Value = facts;
		for(TopLevel t : m.Definitions)
		{
			if(t instanceof Rule)
			{
				Rule r = (Rule) t;
				Utils.addMapList(rules, r.Head.Name, r);
			}
			else if(t instanceof Fact)
			{
				Fact f = (Fact) t;
				String n = f.Tuple.Name;
				List<Object> data = new ArrayList<>(f.Tuple.Parts.size());
				for(int i=0; i<f.Tuple.Parts.size(); ++i)
				{
					data.add(InterpreterTools.unboxGroundTerm(f.Tuple.Parts.get(i)));
				}
				Utils.addMapList(facts, n, data);
			}
		}
		return rules;
	}

	private static takmelogic.ast.Module load(String fileName) throws IOException, UniquenessException, LexerError, DatalogParsingException
	{
		String code = Utils.readAllFile(fileName);
		
		boolean writeLexerDotFiles = false;
		TakmelogicLexer lexer = new TakmelogicLexer(writeLexerDotFiles);
		List<Token> tokens = lexer.lex(code);
		
		TakmelogicParser parser = new TakmelogicParser(lexer.tokenNames());
		takmelogic.ast.Module m = parser.parseDatalog(tokens);
		
		return m;
	}
}
