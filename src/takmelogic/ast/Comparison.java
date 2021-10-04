package takmelogic.ast;

public class Comparison implements SubGoal, Ast
{
	public final Term A;
	public final Term B;
	public final ComparisonOp Op;

	public Comparison(ComparisonOp _Op, Term _A, Term _B)
	{
		Op = _Op;
		A = _A;
		B = _B;
	}

	@Override
	public String toString()
	{
		return String.format("%s(%s, %s)", formatComparisonOp(Op), A, B);
	}

	public static String formatComparisonOp(ComparisonOp op)
	{
		switch(op)
		{
		case Ge:
			return ">=";
		case Gt:
			return ">";
		case Le:
			return "<=";
		case Lt:
			return "<";
		default:
			throw new RuntimeException("Option not handled: " + op);
		}
	}
	
	public static Object formatComparisonOpHtml(ComparisonOp op)
	{
		switch(op)
		{
		case Ge:
			return "&gt;=";
		case Gt:
			return "&gt;";
		case Le:
			return "&lt;=";
		case Lt:
			return "&lt;";
		default:
			throw new RuntimeException("Option not handled: " + op);
		}
	}
}