package takmelogic.ast;

public class Atom implements Ast
{
	public final String Name;
	public final java.util.List<Term> Parts;

	public Atom(String _Name, java.util.List<Term> _Parts)
	{
		Name = _Name;
		Parts = _Parts;
	}

	@Override
	public String toString()
	{
		return String.format("%s(%s)", Name, utils_takmela.Utils.join(Parts, ", "));
	}
}
