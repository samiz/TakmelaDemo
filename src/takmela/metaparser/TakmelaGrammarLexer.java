package takmela.metaparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import takmela.lexer.Lexer;
import takmela.lexer.LexerError;
import takmela.lexer.Token;
import takmela.lexer.ast.*;
import utils_takmela.UniquenessException;
import utils_takmela.Utils;

public class TakmelaGrammarLexer
{
	Lexer lexer;
    Map<Integer, String> tokenNames;
	    
    public TakmelaGrammarLexer() throws UniquenessException, IOException
    {
    	init(true);
    }

    public TakmelaGrammarLexer(boolean writeDotFiles) throws UniquenessException, IOException
    {
		init(writeDotFiles);
    }
	
    private void init(boolean writeDotFiles) throws UniquenessException, IOException
	{
	    List<LexerRule> rules = new ArrayList<>();
	    Map<String, Integer> tokenVocab = new HashMap<>();
	  
	    tokenVocab.put("KW_WITHIN", TakmelaTokens.KW_WITHIN);
	    tokenVocab.put("KW_PUSHES", TakmelaTokens.KW_PUSHES);
	    tokenVocab.put("KW_POPS", TakmelaTokens.KW_POPS);
	    tokenVocab.put("KW_CLASS", TakmelaTokens.KW_CLASS);
	    tokenVocab.put("KW_SKIP", TakmelaTokens.KW_SKIP);
	    tokenVocab.put("KW_CONS", TakmelaTokens.KW_CONS);
	    tokenVocab.put("KW_SEPBY1", TakmelaTokens.KW_SEPBY1);
	    tokenVocab.put("KW_SEPBY0", TakmelaTokens.KW_SEPBY0);
	    tokenVocab.put("KW_HOIST", TakmelaTokens.KW_HOIST);
	    tokenVocab.put("KW_HOISTALL", TakmelaTokens.KW_HOISTALL);
	    tokenVocab.put("KW_NO_LITERAL_TERMINALS", TakmelaTokens.KW_NO_LITERAL_TERMINALS);
	    tokenVocab.put("KW_SPLICE", TakmelaTokens.KW_SPLICE);
	    tokenVocab.put("KW_ERROR", TakmelaTokens.KW_ERROR);
	    tokenVocab.put("IDSmall", TakmelaTokens.IDSmall);
	    tokenVocab.put("IDCap", TakmelaTokens.IDCap);
	    tokenVocab.put("LABEL", TakmelaTokens.LABEL);
	    tokenVocab.put("INT", TakmelaTokens.INT);
	    tokenVocab.put("STRING", TakmelaTokens.STRING);
	    tokenVocab.put("COMMA", TakmelaTokens.COMMA);
	    tokenVocab.put("HASH", TakmelaTokens.HASH);
	    tokenVocab.put("OPAREN", TakmelaTokens.OPAREN);
	    tokenVocab.put("CPAREN", TakmelaTokens.CPAREN);
	    tokenVocab.put("OBRACE", TakmelaTokens.OBRACE);
	    tokenVocab.put("CBRACE", TakmelaTokens.CBRACE);
	    tokenVocab.put("DOLLAR", TakmelaTokens.DOLLAR);
	    tokenVocab.put("PLUS", TakmelaTokens.PLUS);
	    tokenVocab.put("STAR", TakmelaTokens.STAR);
	    tokenVocab.put("QUESTION", TakmelaTokens.QUESTION);
	    tokenVocab.put("BAR", TakmelaTokens.BAR);
	    tokenVocab.put("COLON", TakmelaTokens.COLON);
	    tokenVocab.put("SEMI", TakmelaTokens.SEMI);
	    tokenVocab.put("WS", TakmelaTokens.WS);
	    tokenVocab.put("START_CHARCLASS", TakmelaTokens.START_CHARCLASS);
	    tokenVocab.put("START_CHARCLASS_NOT", TakmelaTokens.START_CHARCLASS_NOT);
	    tokenVocab.put("CHAR", TakmelaTokens.CHAR);
	    tokenVocab.put("DASH", TakmelaTokens.DASH);
	    tokenVocab.put("END_CHARCLASS", TakmelaTokens.END_CHARCLASS);
	    tokenVocab.put("COMMENT", TakmelaTokens.COMMENT);
	    tokenVocab.put("COMMENT2", TakmelaTokens.COMMENT2);


	    tokenNames = Utils.invertMap(tokenVocab);

	    // The keywords come before IDSmall since
	    // longest match win, in case of tie first-in-rules-list wins
	    r(rules, "KW_WITHIN", str("within"));
	    r(rules, "KW_PUSHES", str("pushes"));
	    r(rules, "KW_POPS", str("pops"));
	    r(rules, "KW_CLASS", str("class"));
	    r(rules, "KW_SKIP", str("skip"));
	    r(rules, "KW_CONS", str("cons"));
	    r(rules, "KW_SEPBY1", str("sepBy1"));
	    r(rules, "KW_SEPBY0", str("sepBy0"));
	    r(rules, "KW_HOIST", str("hoist"));
	    r(rules, "KW_HOISTALL", str("hoistall"));
	    r(rules, "KW_ERROR", str("error"));

	    r(rules, "KW_NO_LITERAL_TERMINALS", str("no_literal_terminals"));

	    r(rules, "IDSmall", seq(
	                range('a', 'z'),
	                star(chClass(rng('a', 'z'), rng('A', 'Z'), rng('0', '9'), sng('_')))
	                            ));


	    r(rules, "IDCap", seq(
	                range('A', 'Z'),
	                star(chClass(rng('a', 'z'), rng('A', 'Z'), rng('0', '9'), sng('_')))
	                            ));

	    r(rules, "LABEL", seq(
	                single('#'),
	                range('A', 'Z'),
	                star(chClass(rng('a', 'z'), rng('A', 'Z'), rng('0', '9'), sng('_')))
	                            ));

	    r(rules, "INT", pluz(range('0', '9')));

	    r(rules, "STRING", orr(seq(
	                               str("\""),
	                               star(orr(
                            		   str("\\\\"), // this must come before \\\\, or the string "\\" will be mislexed
                            		   str("\\\""),
                            		   str("\\'"),
                            		   str("\\n"),
                            		   str("\\r"),
                            		   str("\\t"),
                            		   nott(sng('"'), sng('\\'))
	                                   
									)),
	                               str("\"")
	                           ),
	                           seq(
	                                  str("'"),
	                                  star(orr(
	                                      nott(sng('\''), sng('\\')),
	                                      str("\\\\"), // same
	                                      str("\\\""),
	                                      str("\\'"),
	                                      str("\\r"),
	                                      str("\\n"),
	                                      str("\\t")
	                                      
	                                  )),
	                                  str("'")
	                              )
	                           ));
	    r(rules, "COMMA", str(","));
	    r(rules, "HASH", str("#"));
	    r(rules, "OPAREN", str("("));
	    r(rules, "CPAREN", str(")"));
	    r(rules, "OBRACE", str("{"));
	    r(rules, "CBRACE", str("}"));
	    r(rules, "DOLLAR", str("$"));
	    r(rules, "PLUS", str("+"));
	    r(rules, "STAR", str("*"));
	    r(rules, "QUESTION", str("?"));
	    r(rules, "BAR", str("|"));
	    r(rules, "COLON", str(":"));
	    r(rules, "SEMI", str(";"));
	    r_skip(rules, "WS", pluz(orr(
	                           single(' '),
	                           single('\r'),
	                           single('\n'),
	                           single('\t')
	                       )));
	    
	    /*
	    We can finally accept comments without needing ANTLR-like lazy matching!
	    COMMENT skip: '//' ([^\n])* '\n';
		COMMENT2 skip: '/ *' ([^*]|('*' [^/]))* '* / ';   
		*/
	    r_skip(rules, "COMMENT", seq(
	    			str("//"),
	    			star(nott(sng('\n'))),
	    			single('\n')
	    		));

	    r_skip(rules, "COMMENT2", seq(
    			str("/*"),
    			star(orr(
    					nott(sng('*')),
    					seq(single('*'), nott(sng('/')))
    					)),
    			str("*/")
    		));
	    
	    LexerRule startCharClass = new LexerRule("START_CHARCLASS",
	                             Utils.list(),
	                             Utils.list("IN_CHARCLASS"),
	                             Utils.list(),
	                             "",
	                             false,
	                             single('[')
	                             );

	    rules.add(startCharClass);

	    LexerRule startCharClassNot = new LexerRule("START_CHARCLASS_NOT",
	    						 Utils.list(),
	    						 Utils.list("IN_CHARCLASS"),
	    						 Utils.list(),
	                             "",
	                             false,
	                             str("[^")
	                             );

	    rules.add(startCharClassNot);

	    LexerRule _char = new LexerRule("CHAR",
	    						 Utils.list("IN_CHARCLASS"),
	    						 Utils.list(),
	    						 Utils.list(),
	                             "",
	                             false,
	                             orr(
	                                seq(single('\\'),
	                                     orr(
	                                        single('n'),
	                                        single('t'),
	                                        single('r'),
	                                        single('\\'),
	                                        single('\''),
	                                        single('-'),
	                                        single('"'),
	                                        single(']')
	                                     )
	                                ),
	                            nott(sng('\''), sng('\\'), sng('-'), sng('"'), sng(']'))
	                             ));

	    rules.add(_char);

	    LexerRule dash = new LexerRule("DASH",
	    						 Utils.list("IN_CHARCLASS"),
	    						 Utils.list(),
	    						 Utils.list(),
	                             "",
	                             false,
	                             single('-')
	                             );

	    rules.add(dash);

	    LexerRule endCharClass= new LexerRule("END_CHARCLASS",
	                             Utils.list("IN_CHARCLASS"),
	                             Utils.list(),
	                             Utils.list("IN_CHARCLASS"),
	                             "",
	                             false,
	                             single(']')
	                             );

	    rules.add(endCharClass);


	    lexer = new Lexer(rules, tokenVocab, writeDotFiles);

	}

	public List<Token> lex(String input) throws LexerError
	{
	    lexer.init(input);
	    List<Token> ret = new ArrayList<>();
	    while(lexer.hasMoreTokens())
	    {
	        Token t = lexer.nextToken();
	        if(!t.skip())
	        {
	            ret.add(t);
	        }
	    }
	    return ret;
	}
	
	void r(List<LexerRule> rules, String name, RExpr rxpr)
	{
	    rules.add(new LexerRule(name, rxpr));
	}

	void r_skip(List<LexerRule> rules, String name, RExpr rxpr)
	{
	    rules.add(new LexerRule(name, rxpr, true));
	}

	RExpr str(String value)
	{
	    return new Str(value);
	}

	RExpr range(char a, char b)
	{
	    return new CharClass(Utils.list(new CharPartRange(a, b)));
	}

	RExpr single(char a)
	{
	    return new CharClass(Utils.list(new CharPartSingle(a)));
	}

	CharClassPart rng(char a, char b)
	{
	    return new CharPartRange(a, b);
	}

	CharClassPart sng(char a)
	{
	    return new CharPartSingle(a);
	}

	RExpr chClass(CharClassPart...parts)
	{
	    return new CharClass(Utils.listFromArray(parts));
	}

	RExpr nott(CharClassPart ...parts)
	{
	    return new NotCharClass(Utils.listFromArray(parts));
	}

	RExpr star(RExpr expr)
	{
	    return new Star(expr);
	}

	RExpr pluz(RExpr expr)
	{
	    return new Plus(expr);
	}

	RExpr seq(RExpr... expr)
	{
	    return new RXSeq(Utils.listFromArray(expr));
	}

	RExpr orr(RExpr... expr)
	{
	    return new Oring(Utils.listFromArray(expr));
	}

	public Map<Integer, String> tokenNames()
	{
		return tokenNames;
	}
}
