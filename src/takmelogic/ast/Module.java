package takmelogic.ast;

public class Module implements takmela.ast.Ast
{
	public final java.util.List<TopLevel> Definitions;

	public Module(java.util.List<TopLevel> _Definitions)
	{
		this.Definitions = _Definitions;
	}

	@Override public String toString()
	{
		return String.format("Module(%s)", utils_takmela.Utils.surroundJoin(this.Definitions, "[", "]", ", "));
	}
}
