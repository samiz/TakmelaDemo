package takmela.tool;

import java.util.HashMap;
import java.util.Map;

public class LabellerInt<T>
{
	Map<T, Integer> _labels = new HashMap<>();
	int _count = 0;
	
	public int labelFor(T value)
	{
		Integer r = _labels.get(value);
		if(r == null)
		{
			r = _count++;
			_labels.put(value, r);
		}
		return r;
	}
}
