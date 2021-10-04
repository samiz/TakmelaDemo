package utils_takmela.fn;

import java.util.Collection;

public interface ContainsFn<V, C extends Collection<V>>
{
	public boolean contains(C collection, V value);
}
