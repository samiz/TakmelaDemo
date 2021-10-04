package takmela.ast;

public class Rule implements takmela.ast.Ast
{
	public final String Name;
	public final java.util.List<java.util.List<Expr>> Options;
	public final java.util.List<String> Labels;

	public Rule(String _Name, java.util.List<java.util.List<Expr>> _Options, java.util.List<String> _Labels)
	{
		this.Name = _Name;
		this.Options = _Options;
		this.Labels = _Labels;
	}

	@Override public String toString()
	{
		return String.format("Rule(%s, %s, %s)", this.Name, utils_takmela.Utils.surroundJoin(this.Options, "[", "]", ", "),
				utils_takmela.Utils.surroundJoin(this.Labels, "[", "]", ", "));
	}
}
