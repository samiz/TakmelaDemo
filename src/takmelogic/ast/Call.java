package takmelogic.ast;

public class Call implements Ast, SubGoal
{
	public final Atom Calling;

	public Call(Atom _Calling)
	{
		Calling = _Calling;
	}

	@Override
	public String toString()
	{
		return String.format("%s", Calling);
	}
}
