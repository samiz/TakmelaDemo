package takmelogic.ast;

public class Rule implements Ast, TopLevel
{
	public final Atom Head;
	public final java.util.List<SubGoal> SubGoals;

	public Rule(Atom _Head, java.util.List<SubGoal> _SubGoals)
	{
		Head = _Head;
		SubGoals = _SubGoals;
	}

	@Override
	public String toString()
	{
		return String.format("%s :- %s", Head, utils_takmela.Utils.join(SubGoals, ", "));
	}
}
