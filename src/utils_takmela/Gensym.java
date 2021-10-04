package utils_takmela;

import java.util.HashMap;
import java.util.Map;

public class Gensym
{
	private Map<String, Integer> symCount = new HashMap<>();
	
	public String sym(String prefix)
	{
		Integer c = symCount.get(prefix);
		if(c == null)
		{
			c = 0;
		}
		
		symCount.put(prefix, c+1);
		return prefix + c;
	}
}
