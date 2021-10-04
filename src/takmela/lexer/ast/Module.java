package takmela.lexer.ast;

import takmela.lexer.ast.LexerRule;

public class Module implements takmela.lexer.ast.Ast
{
	public final java.util.List<LexerRule> Rules;

	public Module(java.util.List<LexerRule> _Rules)
	{
		this.Rules = _Rules;
	}

	@Override public String toString()
	{
		return String.format("Module(%s)", utils_takmela.Utils.surroundJoin(this.Rules, "[", "]", ", "));
	}
}
