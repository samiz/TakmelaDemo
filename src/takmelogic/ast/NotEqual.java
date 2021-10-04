package takmelogic.ast;

public class NotEqual implements SubGoal, Ast
{
	public final Term A;
	public final Term B;

	public NotEqual(Term _A, Term _B)
	{
		A = _A;
		B = _B;
	}

	@Override
	public String toString()
	{
		return String.format("!=(%s, %s)", A, B);
	}
}