package takmela.ast;

import takmela.lexer.ast.LexerRule;

public class Module implements takmela.ast.Ast
{
	public final java.util.List<Rule> Rules;
	public final java.util.List<LexerRule> LexerRules;
	public final String LexerErrorRule;

	public Module(java.util.List<Rule> _Rules, java.util.List<LexerRule> _LexerRules, String _LexerErrorRule)
	{
		this.Rules = _Rules;
		this.LexerRules = _LexerRules;
		this.LexerErrorRule = _LexerErrorRule;
	}

	@Override public String toString()
	{
		return String.format("Module(%s, %s, %s)", utils_takmela.Utils.surroundJoin(this.Rules, "[", "]", ", "),
				utils_takmela.Utils.surroundJoin(this.LexerRules, "[", "]", ", ")
				);
	}
}
