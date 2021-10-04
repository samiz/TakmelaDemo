package takmela.tree;

import utils_takmela.Utils;

public class TreeFormatting
{
	public static String formatMultiLine(Node n)
	{
		/*
		 e.g 
		      Expr
		      ...Expr
		      ......Term
		      .........5
		      ...'+'
		      ...Term
		      ......5
		 */
		StringBuilder sb = new StringBuilder();
		formatMultiLine(n, sb, 0);
		return sb.toString();
	}
	
	public static String formatBracketed(Node n)
	{
		StringBuilder sb = new StringBuilder();
		formatBracketed(n, sb);
		return sb.toString();
	}
	
	public static void formatBracketed(Node n, StringBuilder sb)
	{
		if(n instanceof Leaf)
		{
			Leaf l = (Leaf) n;
			sb.append(String.format("Tx[%s]", Utils.limitString(l.token.getText(), 20)));
		}
		else if(n instanceof Tree)
		{
			Tree t = (Tree) n;
			sb.append(String.format("#%s(", t.Label));
			for(int i=0; i<t.Children.size(); ++i)
			{
				formatBracketed(t.Children.get(i), sb);
			}
			sb.append(")");
		}
		else
		{
			throw new RuntimeException("Option not handled: " + n);
		}
	}
	
	private static void formatMultiLine(Node n, StringBuilder sb, int indent)
	{
		if(n instanceof Leaf)
		{
			Leaf l = (Leaf) n;
			prni(sb, indent, Utils.limitString(l.token.getText(), 20));
		}
		else if(n instanceof Tree)
		{
			Tree t = (Tree) n;
			prni(sb, indent, String.format("%s"), t.Label);
			for(int i=0; i<t.Children.size(); ++i)
			{
				formatMultiLine(t.Children.get(i), sb, indent+1);
			}
		}
		else
		{
			throw new RuntimeException("Option not handled: " + n);
		}
	}
	public static void prn(StringBuilder sb, String fmt, Object...args)
	{
		sb.append(String.format(fmt, args));
		sb.append("\n");
	}
	
	public static void prni(StringBuilder sb, int indent, String fmt, Object...args)
	{
		String sp = Utils.repeat("..", indent);
		sb.append(sp);
		sb.append(String.format(fmt, args));
		sb.append("\n");
	}
}
