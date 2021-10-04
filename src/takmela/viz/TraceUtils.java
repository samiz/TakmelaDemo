package takmela.viz;

import java.util.Map;
import java.util.Stack;

import takmela.engine.Call;
import takmela.engine.CodePos;
import takmela.tree.Leaf;
import takmela.tree.Node;
import takmela.tree.Tree;
import takmelogic.ast.SubGoal;
import takmelogic.ast.Term;
import takmelogic.ast.Var;
import utils_takmela.Triple;
import utils_takmela.Utils;

public class TraceUtils
{
	public static String formatStack(Stack<Triple<Call, CodePos, Integer>> callStack)
	{
		if(callStack.empty()) { return ""; }
		
		Stack<Triple<Call, CodePos, Integer>> b = new Stack<>();
		b.addAll(callStack);
		
		StringBuilder sb = new StringBuilder();
		sb.append("Processing: ");
		
		Triple<Call, CodePos, Integer> p = b.pop(); 
		sb.append(String.format("%s alt %s { %s }", p.a, p.b.ruleAlt, p.b));
		while(!b.empty())
		{
			p = b.pop();
			sb.append(String.format(" ⇒ %s alt %s { %s }", p.a, p.b.ruleAlt, p.b));
		}
		return sb.toString();
	}

	public static String formatDatalogStack(
			Stack<Triple<takmelogic.engine.Call, takmelogic.engine.CodePos, Map<String, Object>>> callStack)
	{
		if(callStack.empty()) { return ""; }
		
		Stack<Triple<takmelogic.engine.Call, takmelogic.engine.CodePos, Map<String, Object>>> b = new Stack<>();
		b.addAll(callStack);
		
		StringBuilder sb = new StringBuilder();
		sb.append("Processing: ");
		
		Triple<takmelogic.engine.Call,takmelogic.engine.CodePos,Map<String,Object>> p = b.pop(); 
		sb.append(String.format("%s { %s }", p.a, p.b));
		while(!b.empty())
		{
			p = b.pop();
			sb.append(String.format(" ⇒ %s { %s }", p.a, p.b));
		}
		return sb.toString();
	}
	
	public static String formatK(takmela.engine.Cont k, boolean showFullK)
	{
		if(showFullK)
		{
			return String.format("%s ; %s ; %s", k.Caller, k.Code, formatSuccessTreePlain(k.TreeSoFar));
		}
		else
		{
			return String.format("%s ; %s ", k.Caller, k.Code);
		}
	}
	
	public static String formatKForGraphEdge(takmela.engine.Cont k, boolean showFullK)
	{
		if(showFullK)
		{
			return k.Code.toString() + "|木" + k.TreeSoFar.toString();
			//return String.format("%s ; %s ; %s", k.Caller, k.Code, formatSuccessTree(k.TreeSoFar));
		}
		else
		{
			return k.Code.toString();
		}
	}
	
	public static String formatSuccessTreePlain(Node tree)
	{
		return tree.toString();
	}
	
	public static String formatSuccessTreeRainbowHtml(Node tree, boolean depthShift)
	{
		StringBuilder sb = new StringBuilder();
		formatSuccessTreeRainbowHtml(sb, tree, 0, depthShift);
		return sb.toString();
	}
	
	private static void formatSuccessTreeRainbowHtml(StringBuilder sb, Node node, int level, boolean depthShift)
	{
		if(node instanceof Leaf)
		{
			sb.append(node.toString());
		}
		else if(node instanceof Tree)
		{
			Tree t = (Tree) node;
			sb.append(t.Label);
			
			if(t.Children.size() > 0)
			{
				String depth = depthShift? ""+level : "nodepth";
				
				sb.append(String.format("<span class=\"rainbow-%s\"><span class=\"open-rainbow-%s\">(</span>", depth, level));
				String sep = "";
				for(int i=0; i<t.Children.size(); ++i)
				{
					sb.append(sep);
					sep = ",";
					formatSuccessTreeRainbowHtml(sb, t.Children.get(i), level+1, depthShift);
				}
				sb.append(String.format("<span class=\"close-rainbow-%s\">)</span></span>", level));
			}
		}
		else
		{
			throw new RuntimeException("Option not handled: " + node);
		}
	}

	public static String formatK(takmelogic.engine.Cont cont, boolean showFullK)
	{
		String contStr;
		if(showFullK)
		{
			String bind = "";
			if(!cont.BindingsSoFar.isEmpty())
			{
				bind = cont.BindingsSoFar.toString();
			}
			contStr = String.format("%s ; %s", cont.CodePos, bind); 
		}
		else
		{
			contStr = cont.CodePos.toString();
		}
		return contStr;
	}
	
	public static String formatInfusedKPlain(takmelogic.engine.Cont cont)
	{
		return formatInfusedK(cont, "", "", false);
	}
	
	public static String formatInfusedKForHtml(takmelogic.engine.Cont cont)
	{
		return formatInfusedK(cont, "<span class=\"contVar\">", "</span>", true);
	}
	
	private static String formatInfusedK(takmelogic.engine.Cont cont, String beforeVar, String afterVar,
			boolean escapeHtml)
	{
		return formatInfusedCodePos(cont.CodePos, cont.BindingsSoFar, beforeVar, afterVar, escapeHtml);
	}
	
	public static String formatInfusedCodePosForHtml(takmelogic.engine.CodePos codePos, Map<String, Object> bindingsSoFar)
	{
		return formatInfusedCodePos(codePos, bindingsSoFar, "<span class=\"contVar\">", "</span>", true);
	}
	
	public static String formatInfusedCodePos(takmelogic.engine.CodePos codePos, Map<String, Object> bindingsSoFar)
	{
		return formatInfusedCodePos(codePos, bindingsSoFar, "", "", false);
	}
	
	private static String formatInfusedCodePos(takmelogic.engine.CodePos codePos, Map<String, Object> bindingsSoFar,
			String beforeVar, String afterVar, boolean escapeHtml)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(codePos.rule.Head);
		sb.append("→");
		
		String sep = "";
		for(int i=0; i<codePos.sgNum; ++i)
		{
			SubGoal sg = codePos.rule.SubGoals.get(i);
			sb.append(sep);
			sep = ", ";
			formatSg(sb, sg, bindingsSoFar, beforeVar, afterVar, escapeHtml);
		}
		sb.append("•");
		sep = "";
		for(int i= codePos.sgNum; i< codePos.rule.SubGoals.size(); ++i)
		{
			SubGoal sg = codePos.rule.SubGoals.get(i);
			sb.append(sep);
			sep = ", ";
			formatSg(sb, sg, bindingsSoFar, beforeVar, afterVar, escapeHtml);
		}
			
		return sb.toString();
	}

	private static void formatSg(StringBuilder sb, SubGoal sg, Map<String, Object> bindingsSoFar, String beforeVar, String afterVar,
			boolean escapeHtml)
	{
		if(sg instanceof takmelogic.ast.Call)
		{
			takmelogic.ast.Call call = (takmelogic.ast.Call) sg;
			sb.append(call.Calling.Name);
			sb.append("(");
			sb.append(Utils.joinMap(call.Calling.Parts, ",", term->{
				return formatInfusedTerm(term, bindingsSoFar, beforeVar, afterVar);
			}));
			sb.append(")");
		}
		else if(sg instanceof takmelogic.ast.IsEqual)
		{
			takmelogic.ast.IsEqual eq = (takmelogic.ast.IsEqual) sg;
			String s = String.format("%s==%s", formatInfusedTerm(eq.A, bindingsSoFar, beforeVar, afterVar), 
					formatInfusedTerm(eq.B, bindingsSoFar, beforeVar, afterVar));
			sb.append(s);
		}
		else if(sg instanceof takmelogic.ast.NotEqual)
		{
			takmelogic.ast.NotEqual ne = (takmelogic.ast.NotEqual) sg;
			String s = String.format("%s==%s", formatInfusedTerm(ne.A, bindingsSoFar, beforeVar, afterVar), 
					formatInfusedTerm(ne.B, bindingsSoFar, beforeVar, afterVar));
			sb.append(s);
		}
		else if(sg instanceof takmelogic.ast.Comparison)
		{
			takmelogic.ast.Comparison comp = (takmelogic.ast.Comparison) sg;
			String s = String.format("%s%s%s", 
					formatInfusedTerm(comp.A, bindingsSoFar, beforeVar, afterVar),
					escapeHtml? takmelogic.ast.Comparison.formatComparisonOpHtml(comp.Op) : takmelogic.ast.Comparison.formatComparisonOp(comp.Op),
					formatInfusedTerm(comp.B, bindingsSoFar, beforeVar, afterVar));
			sb.append(s);
		}
		else
		{
			throw new RuntimeException("Unhandled subgoal type: " + sg);
		}		
	}

	public static String formatInfusedTermPlain(Term term, Map<String, Object> env)
	{
		return formatInfusedTerm(term, env, "", "");
	}
	
	public static String formatInfusedTermForHtml(Term term, Map<String, Object> env)
	{
		return formatInfusedTerm(term, env, "<span class=\"contVar\">", "</span>");
	}
	
	public static String formatInfusedTerm(Term term, Map<String, Object> bindingsSoFar, String beforeVar,
			String afterVar)
	{
		if(term == null)
		{
			return "?";
		}
		if(term instanceof Var)
		{
			Var v = (Var) term;
			Object val = bindingsSoFar.get(v.N);
			if(val == null)
			{
				return (v.N);
			}
			else
			{
				return (v.N + "|" + beforeVar + val + afterVar);
			}
		}
		else
		{
			return term.toString();
		}
	}
}
