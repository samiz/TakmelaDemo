package takmelogic.parser;

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

public class TakmelogicLexer
{
	Lexer lexer;
    Map<Integer, String> tokenNames;
	    
    public TakmelogicLexer() throws UniquenessException, IOException
    {
    	init(true);
    }

    public TakmelogicLexer(boolean writeDotFiles) throws UniquenessException, IOException
    {
		init(writeDotFiles);
    }
	
    private void init(boolean writeDotFiles) throws UniquenessException, IOException
	{
	    List<LexerRule> rules = new ArrayList<>();
	    Map<String, Integer> tokenVocab = new HashMap<>();
	  
	    tokenVocab.put("KW_ERROR", TakmelogicTokens.KW_ERROR);
	    tokenVocab.put("IDSmall", TakmelogicTokens.IDSmall);
	    tokenVocab.put("IDCap", TakmelogicTokens.IDCap);
	    tokenVocab.put("INT", TakmelogicTokens.INT);
	    tokenVocab.put("STRING", TakmelogicTokens.STRING);
	    tokenVocab.put("COMMA", TakmelogicTokens.COMMA);
	    tokenVocab.put("OPAREN", TakmelogicTokens.OPAREN);
	    tokenVocab.put("CPAREN", TakmelogicTokens.CPAREN);
	    tokenVocab.put("LT", TakmelogicTokens.LT);
	    tokenVocab.put("LE", TakmelogicTokens.LE);
	    tokenVocab.put("GT", TakmelogicTokens.GT);
	    tokenVocab.put("GE", TakmelogicTokens.GE);
	    tokenVocab.put("EQ", TakmelogicTokens.EQ);
	    tokenVocab.put("NE", TakmelogicTokens.NE);
	    tokenVocab.put("IF", TakmelogicTokens.IF);
	    tokenVocab.put("DOT", TakmelogicTokens.DOT);
	    tokenVocab.put("WS", TakmelogicTokens.WS);
	    tokenVocab.put("COMMENT", TakmelogicTokens.COMMENT);
	    tokenVocab.put("COMMENT2", TakmelogicTokens.COMMENT2);


	    tokenNames = Utils.invertMap(tokenVocab);

	    // The keywords come before IDSmall since
	    // longest match win, in case of tie first-in-rules-list wins
	    r(rules, "KW_ERROR", str("error"));

	    r(rules, "IDSmall", seq(
	                range('a', 'z'),
	                star(chClass(rng('a', 'z'), rng('A', 'Z'), rng('0', '9'), sng('_')))
	                            ));


	    r(rules, "IDCap", seq(
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
	    r(rules, "OPAREN", str("("));
	    r(rules, "CPAREN", str(")"));
	    
	    r(rules, "LT", str("<"));
	    r(rules, "LE", str("<="));
	    r(rules, "GT", str(">"));
	    r(rules, "GE", str(">="));
	    r(rules, "EQ", str("=="));
	    r(rules, "NE", str("!="));
	    
	    r(rules, "IF", str(":-"));
	    r(rules, "DOT", str("."));

	    r_skip(rules, "WS", pluz(orr(
	                           single(' '),
	                           single('\r'),
	                           single('\n'),
	                           single('\t')
	                       )));
	    
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
