package takmela.lexer;

import java.util.ArrayList;
import java.util.List;

import utils_takmela.Pair;

public class LexerUtils
{
	public static int[] linePositions(String text)
	{
		List<Integer> lst = new ArrayList<>();
		
		if(text.length() > 0)
		{
			lst.add(0);
		}			
		
		for(int i=0; i<text.length(); ++i)
		{
			if(text.charAt(i) == '\n' && text.length() > i+1)
			{
				lst.add(i+1);
			}			
		}
		return intListToArray(lst);
	}
	
	public static Pair<Integer, Integer> lineCol(int pos, int[] linePositions)
	{
		int line = binarySearchNotGreaterThan(linePositions, pos);
		int col = pos - linePositions[line];
		return new Pair<>(line, col);
	}

	// Find the first element less than or equal 'val', return its position
	// Return -1 if no element less than 'val' (this is useful if we're using this for line/pos info; APL-like uses too?)
	// Return -1 if array is empty
	// TODO make binarySearchNotGreaterThan actually binary search
	private static int binarySearchNotGreaterThan(int[] arr, int val)
	{
		for(int i=0; ; ++i)
		{
			if(i==arr.length || arr[i] > val)
			{
				return i-1;
			}
		}
	}
	
	private static int[] intListToArray(List<Integer> lst)
	{
		int[] ret = new int[lst.size()];
		for(int i=0; i<lst.size(); ++i)
		{
			ret[i] = lst.get(i);
		}
		return ret;
	}
}
