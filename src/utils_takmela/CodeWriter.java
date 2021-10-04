package utils_takmela;

public class CodeWriter
{
	private int indentLevel;
	private StringBuilder sb;
	private boolean startOfLine;
	
	public CodeWriter()
	{
		indentLevel = 0;
		sb = new StringBuilder();
		startOfLine = true;
	}
	
	public void indent() { indentLevel++; }
	public void dedent() { indentLevel--; }
	public void print(String str) { printIndented(str);}
	public void printf(String str, Object... args) { printIndented(String.format(str, args)); }
	
	public void nl()
	{ 
		sb.append("\n");
		startOfLine = true;
	}
	
	private void printIndented(String str)
	{
		if(startOfLine)
		{
			sb.append(Utils.repeat(" ", indentLevel*4));
		}
		sb.append(str + "\n") ;
		startOfLine = true;
	}

	public void emit(String str) 
	{
		if(str.equals("\n"))
		{
			nl();
			return;
		}
		if(startOfLine)
		{
			sb.append(Utils.repeat(" ", indentLevel*4));
		}
		
		if(!str.contains("\n"))
		{
			sb.append(str);
			startOfLine = false;
		}
		else
		{
			throw new IllegalArgumentException("emit/emitf cannot contain newlines in their arguments, use nl");
		}
	}
	public void emitf(String str, Object... args) 
	{ 
		emit(String.format(str, args)); 
	}

	public String buildString()
	{
		return sb.toString();
	}
}
