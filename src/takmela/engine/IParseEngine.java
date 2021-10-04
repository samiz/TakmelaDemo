package takmela.engine;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import takmela.lexer.Token;
import takmela.lexer.TokenVocab;
import takmela.tree.Node;
import utils_takmela.fn.IProc3;

public interface IParseEngine
{
	void initTokenVocab(TokenVocab tokenVocab);
	void parse(String startSymbol, List<Token> input, Consumer<Node> onSuccessfulParse, IProc3<Set<Expected>, String, Integer> onFail);
	void setTracer(TakmelaTracer tracer);
}
