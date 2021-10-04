package utils_takmela;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LLst<T> implements Iterable<T>
{
	public final T head;
	public final LLst<T> tail;

	public LLst(T val, LLst<T> next)
	{
		this.head = val;
		this.tail = next;
	}
	
	public T get(int index)
	{
		// A null reference exception will be eventually thrown if index is negative or out of range
		if(index == 0)
		{
			return head;
		}
		return tail.get(index-1);
	}
	
	public Iterator<T> iterator()
	{
		final LLst<T> l = this;
		return new Iterator<T>() {
			private LLst<T> lst = l;
			@Override public boolean hasNext()
			{
				return lst != null;
			}

			@Override public T next()
			{
				T ret = lst.head;
				lst = lst.tail;
				return ret;
			}
		};
	}
	
	public int size()
	{
		int n = 0;
		LLst<T> lst = this;
		while(lst != null)
		{
			n++;
			lst = lst.tail;
		}
		return n;
	}
	
	public List<T> toList()
	{
		List<T> ret = new ArrayList<>();
		for(T v : this)
		{
			ret.add(v);
		}
		return ret;
	}
	
	public static<T> LLst<T> fromList(List<T> list)
	{
		LLst<T> ret = null;
		for(int i=list.size()-1; i>=0; --i)
		{
			ret = new LLst<>(list.get(i), ret);
		}
		return ret;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		String sep = "";
		LLst<T> lst = this;
		while(lst != null)
		{
			sb.append(sep + lst.head.toString());
			sep = ", ";
			lst = lst.tail;
		}
		sb.append("]");
		return sb.toString();
	}

	public static<T> Iterable<T> iterate(LLst<T> lst)
	{
		if(lst == null)
		{
			return new Iterable<T>() {
				@Override public Iterator<T> iterator()	{
					return new Iterator<T>() {
						@Override public boolean hasNext()
						{
							return false;
						}

						@Override public T next()
						{
							return null;
						}
					};
				}
			};
		}
		else
		{
			return lst;
		}
	}

	public static<T> String toString(LLst<T> a)
	{
		if(a == null)
		{
			return "[]";
		}
		return a.toString();
	}
}
