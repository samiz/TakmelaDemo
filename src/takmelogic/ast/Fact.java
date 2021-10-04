package takmelogic.ast;

public class Fact implements Ast, TopLevel
{
	public final Atom Tuple;

	public Fact(Atom _Tuple)
	{
		Tuple = _Tuple;
	}

	@Override
	public String toString()
	{
		return String.format("%s", Tuple);
	}
}
