package takmela.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import utils_takmela.Box;
import utils_takmela.Pair;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;
import utils_takmela.fn.IProc3;
import takmela.ast.Expr;
import takmela.ast.Rule;
import takmela.ast.Terminal;
import takmela.engine.Expected;
import takmela.engine.IParseEngine;
import takmela.engine.ParseEngine;
import takmela.engine.TakmelaTracer;
import takmela.lexer.Lexer;
import takmela.lexer.LexerError;
import takmela.lexer.LexerUtils;
import takmela.lexer.Token;
import takmela.lexer.ast.LexerRule;
import takmela.metaparser.GrammarParsingException;
import takmela.metaparser.TakmelaGrammarLexer;
import takmela.metaparser.TakmelaGrammarParser;
import takmela.tree.Node;

public class Parse
{
	public static void main(String[] args) throws IOException, LexerError, UniquenessException, GrammarParsingException
	{
	}

	public static IParseEngine parse(takmela.ast.Module mod, String inputStr, String startSymbol, Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail) throws IOException, LexerError, UniquenessException
	{
		return parse(mod, inputStr, startSymbol, null, null, onSuccessfulParse, onFail);
	}
	
	public static IParseEngine parse(takmela.ast.Module mod, TakmelaTracer tracer, String inputStr, String startSymbol, Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail) throws IOException, LexerError, UniquenessException
	{
		return parse(mod, tracer, inputStr, startSymbol, null, null, onSuccessfulParse, onFail);
	}
	
	public static void parseWithoutAmbiguity(takmela.ast.Module mod, String inputStr, String startSymbol, Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail) throws IOException, LexerError, UniquenessException
	{
		ParseEngine pe = new ParseEngine(mod.Rules);
		pe.setErrorOnAmbiguity();
		boolean writeDotFiles = false;
		writeDotFiles = true;
		parseWith(pe, mod, inputStr, startSymbol, writeDotFiles, null, null, onSuccessfulParse, onFail);
	}
	
	public static void parseWithoutAmbiguity(takmela.ast.Module mod, String inputStr, String startSymbol,
			Box<int[]> outLinePositions, Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail) throws IOException, LexerError, UniquenessException
	{
		ParseEngine pe = new ParseEngine(mod.Rules);
		pe.setErrorOnAmbiguity();
		boolean writeDotFiles = false;
		writeDotFiles = true;
		parseWith(pe, mod, inputStr, startSymbol, writeDotFiles, null, outLinePositions, onSuccessfulParse, onFail);
	}
	
	public static void parseWithoutAmbiguity(takmela.ast.Module mod, String inputStr, String startSymbol,
			Box<int[]> outLinePositions, Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail, Consumer<LexerError> onLexerError) throws IOException, UniquenessException
	{
		ParseEngine pe = new ParseEngine(mod.Rules);
		pe.setErrorOnAmbiguity();
		boolean writeDotFiles = false;
		writeDotFiles = true;
		parseWith(pe, mod, inputStr, startSymbol, writeDotFiles, null, outLinePositions, onSuccessfulParse, onFail, onLexerError);
	}
	
	public static void parseWithoutAmbiguity(takmela.ast.Module mod, String inputStr, String startSymbol,
			Box<Map<Integer, String>> outTokenNames, Box<int[]> outLinePositions, Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail) throws IOException, LexerError, UniquenessException
	{
		ParseEngine pe = new ParseEngine(mod.Rules);
		pe.setErrorOnAmbiguity();
		boolean writeDotFiles = false;
		writeDotFiles = true;
		parseWith(pe, mod, inputStr, startSymbol, writeDotFiles, outTokenNames, outLinePositions, onSuccessfulParse, onFail);
	}
	
	public static void parseWithoutAmbiguity(takmela.ast.Module mod, String inputStr, String startSymbol,
			Box<Map<Integer, String>> outTokenNames, Box<int[]> outLinePositions, Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail, Consumer<LexerError> onLexerError) throws IOException, LexerError, UniquenessException
	{
		ParseEngine pe = new ParseEngine(mod.Rules);
		pe.setErrorOnAmbiguity();
		boolean writeDotFiles = false;
		writeDotFiles = true;
		parseWith(pe, mod, inputStr, startSymbol, writeDotFiles, outTokenNames, outLinePositions, onSuccessfulParse, onFail, onLexerError);
	}
	
	public static void parse(takmela.ast.Module mod, String inputStr, String startSymbol, 
			boolean writeDotFiles, Consumer<Node> onSuccessfulParse, IProc3<Set<Expected>, String, Integer> onFail) throws IOException, LexerError, UniquenessException
	{
		parse(mod, inputStr, startSymbol, writeDotFiles, null, null, onSuccessfulParse, onFail);
	}
	
	public static void parse(takmela.ast.Module mod, String inputStr, String startSymbol, 
			Box<Map<Integer, String>> outTokenNames, 
			Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail)
			throws IOException, LexerError, UniquenessException
	{
		parse(mod, inputStr, startSymbol, false, outTokenNames, null, onSuccessfulParse, onFail);
	}
	
	public static IParseEngine parse(takmela.ast.Module mod, String inputStr, String startSymbol, 
			Box<Map<Integer, String>> tokenNames, 
			Box<int[]> outLinePositions,
			Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail)
			throws IOException, LexerError, UniquenessException
	{
		return parse(mod, inputStr, startSymbol, false, tokenNames, outLinePositions, onSuccessfulParse, onFail);
	}
	
	public static IParseEngine parse(
			takmela.ast.Module mod, 
			TakmelaTracer tracer,
			String inputStr, String startSymbol, 
			Box<Map<Integer, String>> tokenNames, 
			Box<int[]> outLinePositions,
			Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail)
			throws IOException, LexerError, UniquenessException
	{
		return parse(mod, tracer, inputStr, startSymbol, false, tokenNames, outLinePositions, onSuccessfulParse, onFail);
	}
	
	public static void parse(takmela.ast.Module mod, String inputStr, String startSymbol, 
			Box<Map<Integer, String>> tokenNames, 
			Box<int[]> outLinePositions,
			Consumer<Node> onSuccessfulParse, 
			IProc3<Set<Expected>, String, Integer> onFail, 
			Consumer<LexerError> onLexerError)
			throws IOException, LexerError, UniquenessException
	{
		parse(mod, inputStr, startSymbol, false, tokenNames, outLinePositions, onSuccessfulParse, onFail, onLexerError);
	}
	
	public static IParseEngine parse(takmela.ast.Module mod, String inputStr, String startSymbol, 
			boolean writeDotFiles, Box<Map<Integer, String>> tokenNames, Box<int[]> outLinePositions, Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail) throws IOException, LexerError, UniquenessException
	{
		ParseEngine pe = new ParseEngine(mod.Rules);
		parseWith(pe, mod, inputStr, startSymbol, writeDotFiles, tokenNames, outLinePositions, onSuccessfulParse, onFail);
		return pe;
	}
	
	public static IParseEngine parse(takmela.ast.Module mod, TakmelaTracer tracer, String inputStr, String startSymbol, 
			boolean writeDotFiles, Box<Map<Integer, String>> tokenNames, Box<int[]> outLinePositions, Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail) throws IOException, LexerError, UniquenessException
	{
		ParseEngine pe = new ParseEngine(mod.Rules);
		pe.setTracer(tracer);
		parseWith(pe, mod, inputStr, startSymbol, writeDotFiles, tokenNames, outLinePositions, onSuccessfulParse, onFail);
		return pe;
	}
	
	public static void parse(takmela.ast.Module mod, String inputStr, String startSymbol, 
			boolean writeDotFiles, Box<Map<Integer, String>> tokenNames, Box<int[]> outLinePositions, Consumer<Node> onSuccessfulParse,
			IProc3<Set<Expected>, String, Integer> onFail, Consumer<LexerError> onLexerError) throws IOException, LexerError, UniquenessException
	{
		ParseEngine pe = new ParseEngine(mod.Rules);
		parseWith(pe, mod, inputStr, startSymbol, writeDotFiles, tokenNames, outLinePositions, onSuccessfulParse, onFail, onLexerError);
	}
	
	public static void parseWith(IParseEngine pe, takmela.ast.Module mod, String inputStr, String startSymbol, 
			boolean writeDotFiles, Consumer<Node> onSuccessfulParse, IProc3<Set<Expected>, String, Integer> onFail, Consumer<LexerError> onLexerError)
			throws IOException, LexerError, UniquenessException
	{
		parseWith(pe, mod, inputStr, startSymbol, writeDotFiles, null, null, onSuccessfulParse, onFail, onLexerError);
	}
	
	public static void parseWith(IParseEngine pe, takmela.ast.Module mod, String inputStr, String startSymbol, 
			boolean writeDotFiles, Consumer<Node> onSuccessfulParse, IProc3<Set<Expected>, String, Integer> onFail)
			throws IOException, LexerError, UniquenessException
	{
		parseWith(pe, mod, inputStr, startSymbol, writeDotFiles, null, null, onSuccessfulParse, onFail);
	}
	
	public static void parseWith(IParseEngine pe, takmela.ast.Module mod, String inputStr, String startSymbol, 
			boolean writeDotFiles, Box<Map<Integer, String>> tokenNames, Box<int[]> outLinePositions, Consumer<Node> onSuccessfulParse, 
			IProc3<Set<Expected>, String, Integer> onFail,
			Consumer<LexerError> onLexerError) throws IOException, UniquenessException
	{
		try
		{
			parseWith(pe, mod, inputStr, startSymbol, writeDotFiles, tokenNames, outLinePositions, onSuccessfulParse, onFail);
		}
		catch(LexerError err)
		{
			onLexerError.accept(err);
		}
	}
	
	public static void parseWith(IParseEngine pe, takmela.ast.Module mod, String inputStr, String startSymbol, 
			boolean writeDotFiles, Box<Map<Integer, String>> tokenNames, Box<int[]> outLinePositions, 
			Consumer<Node> onSuccessfulParse, IProc3<Set<Expected>, String, Integer> onFail)
			throws IOException, UniquenessException, LexerError
	{
		List<LexerRule> lexerRules = combineWithInlineTokens(mod);

		Lexer lex = new Lexer(lexerRules, writeDotFiles);

		if(tokenNames != null)
		{
			tokenNames.Value = lex.tokenNames();
		}
		
		lex.init(inputStr);
		
		if(outLinePositions != null)
		{
			outLinePositions.Value = lex.linePositions();
		}
		
		List<takmela.lexer.Token> input = new ArrayList<>();
		while (lex.hasMoreTokens())
		{
			try
			{
				Token t = lex.nextToken();
				if (!t.skip())
				{
					input.add(t);
				}
			}
			catch(LexerError err)
			{
				throw err; // Give ourselves a chance to put a breakpoint here
			}
		}
		pe.initTokenVocab(lex);
		pe.parse(startSymbol, input, onSuccessfulParse, onFail);
	}
	
	public static List<Token> lexize(String input, takmela.ast.Module mod, Box<List<LexerRule>> augmentedRules) throws IOException, LexerError, UniquenessException
	{
		List<LexerRule> lexerRules = combineWithInlineTokens(mod);
		
		List<Token> ret = lexize(input, lexerRules);
		
		if(augmentedRules != null)
		{
			augmentedRules.Value = lexerRules;
		}
		
		return ret;
	}

	// Combine a module's lexer rules ( e.g INT: [0-9]+ )
	// with inline tokens (e.g the plus in t-> t '+' f)
	// giving a full list of lexer rules
	public static List<LexerRule> combineWithInlineTokens(takmela.ast.Module mod)
	{
		Set<String> inlineToks = collectInlineTokens(mod.Rules);
		List<takmela.lexer.ast.LexerRule> inlineRules = new ArrayList<>();
		int i = 0;
		for (String tok : inlineToks)
		{
			inlineRules.add(r("_T__" + i++, tok));
		}
		List<LexerRule> lexerRules = Utils.concat(inlineRules, mod.LexerRules);
		return lexerRules;
	}
	
	public static List<Token> lexize(String inputStr, List<LexerRule> lexerRules) throws IOException, LexerError, UniquenessException
	{
		return lexize(inputStr, lexerRules, false);
	}
	
	public static List<Token> lexize(String inputStr, List<LexerRule> lexerRules, boolean writeDotFiles) throws IOException, LexerError, UniquenessException
	{
		Lexer lex = new Lexer(lexerRules, writeDotFiles);

		lex.init(inputStr);
		List<takmela.lexer.Token> inputTokens = new ArrayList<>();
		while (lex.hasMoreTokens())
		{
			Token t = lex.nextToken();
			if (!t.skip())
			{
				inputTokens.add(t);
			}
		}
		return inputTokens;
	}
	
	public static String defaultFailMessage(Set<Expected> expected, String given, int pos)
	{
		return String.format("Given '%s, expected '%s' at position %s'", given, expected, pos);
	}
	
	public static String defaultFailMessage(Set<Expected> expected, String given, int pos, int[] linePositions)
	{
		Pair<Integer,Integer> lineCol = LexerUtils.lineCol(pos, linePositions);
		return String.format("%s:%s: Given '%s', expected '%s'", lineCol.a +1 , lineCol.b, given, expected); // +1 to match editors
	}
	
	public static String defaultFailMessageNoRule(String sourceName, Set<String> expected, String given, int pos, int[] linePositions)
	{
		Pair<Integer,Integer> lineCol = LexerUtils.lineCol(pos, linePositions);
		return String.format("%s:%s:%s: Given '%s', expected '%s'", sourceName, lineCol.a +1 , lineCol.b, given, expected); // +1 to match editors
	}
	
	public static String defaultFailMessage(String sourceName, Set<Expected> expected, String given, int pos, int[] linePositions)
	{
		Pair<Integer,Integer> lineCol = LexerUtils.lineCol(pos, linePositions);
		return String.format("%s:%s:%s: Given `%s', expected '%s'", sourceName, lineCol.a +1 , lineCol.b, 
				given, Utils.joinMap(expected, ", ", (a,b)->String.format("`%s' [at rule:%s]", a.Expected, a.FromRule))); // +1 to match editors
	}
	
	public static String defaultLexerErrorMessage(String codeSource, LexerError err)
	{
		return String.format("%s:%s:%s    %s", codeSource, err.Line+1, err.Column, err.getMessage());
	}
	
	public static String defaultLexerErrorMessage(LexerError err)
	{
		return String.format("%s:%s    %s", err.Line, err.Column, err.getMessage());
	}

	private static takmela.lexer.ast.LexerRule r(String name, String tok)
	{
		return new takmela.lexer.ast.LexerRule(name, 
				new ArrayList<>(), 
				new ArrayList<>(), 
				new ArrayList<>(), 
				null, 
				false, 
				new takmela.lexer.ast.Str(tok));
	}

	private static Set<String> collectInlineTokens(List<Rule> rules)
	{
		Set<String> ret = new HashSet<>();
		for (Rule rule : rules)
		{
			for (List<Expr> seq : rule.Options)
			{
				for (Expr _e : seq)
				{
					if (_e instanceof Terminal)
					{
						Terminal t = (Terminal) _e;
						ret.add(t.Value);
					}
				}
			}
		}
		return ret;
	}

	public static takmela.ast.Module parseSingleFile(String fn, List<String> outParseErrors) throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		return parseGrammar(Utils.readAllFile(fn), outParseErrors);
	}
	
	public static takmela.ast.Module parseSingleFile(String fn, List<String> outParseErrors, Box<takmela.ast.Module> preTransformation) throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		return parseGrammar(Utils.readAllFile(fn), outParseErrors, preTransformation);
	}
	
	public static takmela.ast.Module parseGrammar(String code, List<String> outParseErrors) throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		return parseGrammar(code, outParseErrors, null);
	}
	
	public static takmela.ast.Module parseGrammar(String code, List<String> outParseErrors, Box<takmela.ast.Module> preTransformation) throws IOException, UniquenessException, LexerError, GrammarParsingException
	{
		boolean writeMetaLexerDotFiles = false;
		TakmelaGrammarLexer metaLexer = new TakmelaGrammarLexer(writeMetaLexerDotFiles);
		List<Token> tokens = metaLexer.lex(code);
		TakmelaGrammarParser metaParser = new TakmelaGrammarParser(metaLexer.tokenNames());
		takmela.ast.Module mod = metaParser.parseGrammar(tokens);
		takmela.ast.Module mod2 = metaParser.transformGrammar(mod);
		if(preTransformation !=null)
		{
			preTransformation.Value = mod;
		}
		return mod2;
	}
}
