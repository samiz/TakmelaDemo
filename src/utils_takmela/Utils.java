package utils_takmela;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import utils_takmela.fn.ContainsFn;

public class Utils
{
	public static<T> List<T> list()
	{
		List<T> ret = new ArrayList<>();
		return ret;
	}
	
	public static<T> List<T> list(T element)
	{
		List<T> ret = new ArrayList<>();
		ret.add(element);
		return ret;
	}
	
	public static<T> List<T> list(T a, T b)
	{
		List<T> ret = new ArrayList<>();
		ret.add(a);
		ret.add(b);
		return ret;
	}
	
	public static<T> List<T> list(Collection<? extends T> coll)
	{
		List<T> ret = new ArrayList<>();
		ret.addAll(coll);
		return ret;
	}
	
	public static<T> List<T> listFromArray(T[] coll)
	{
		List<T> ret = new ArrayList<>();
		for(T el : coll)
		{
			ret.add(el);
		}
		return ret;
	}

	public static<T> Set<T> set(Iterable<? extends T> elements)
	{
		Set<T> ret = new HashSet<>();
		for(T value : elements)
		{
			ret.add(value);
		}
		return ret;
	}
	
	public static<T> Set<T> set(T element)
	{
		Set<T> ret = new HashSet<>();
		ret.add(element);
		return ret;
	}
	
	public static<T> Set<T> set()
	{
		Set<T> ret = new HashSet<>();
		return ret;
	}
	
	public static<T> T last(List<T> lst)
	{
		// Always assumes list has at least one element	
		return lst.get(lst.size()-1);
	}
	
	public static<T> List<T> appendElement(Collection<T> a, T b)
	{
		List<T> ret = new ArrayList<T>(a.size() + 1);
		
		ret.addAll(a);
		ret.add(b);
		
		return ret;
	}
	
	public static<T> LLst<T> appendElement(LLst<T> a, T b)
	{
		if(a == null)
		{
			return new LLst<T>(b, null);
		}
		else
		{
			return new LLst<T>(a.head, appendElement(a.tail, b));
		}
	}
	
	public static<T> List<T> concat(Collection<? extends T> a, Collection<? extends T> b)
	{
		ArrayList<T> ret = new ArrayList<>();
		ret.addAll(a);
		ret.addAll(b);
		return ret;
	}
	
	public static <TK,TV> TV mustGet(Map<TK, TV> map, TK key)
	{
		TV ret = map.get(key);
		if(ret == null)
		{
			throw new RuntimeException(String.format("Key %s must exist in map", key));
		}
		return ret;
	}
	
	public static <TK, TV> void addMapList(Map<TK, List<TV>> map, TK key, TV value)
	{
		List<TV> lst = null;
		if(!map.containsKey(key))
		{
			lst = new ArrayList<TV>();
			map.put(key, lst);
		}
		else
		{
			lst = map.get(key);
		}
		lst.add(value);
	}
	
	public static<Tk, Tv> void addAllMapList(Map<Tk, List<Tv>> map, Tk key, Collection<? extends Tv> values)
	{
		List<Tv> lst = null;
		
		if(!map.containsKey(key))
		{
			lst = new ArrayList<Tv>();
			map.put(key, lst);
		}
		else
		{
			lst = map.get(key);
		}
		lst.addAll(values);
	}
	
	public static <TK, TV> void addMapSet(Map<TK, Set<TV>> map, TK key, TV value)
	{
		Set<TV> set = null;
		if(!map.containsKey(key))
		{
			set = new HashSet<>();
			map.put(key, set);
		}
		else
		{
			set = map.get(key);
		}
		set.add(value);
	}
	
	public static<Tk1, Tk2, Tv> void addMapSet(Map<Pair<Tk1, Tk2>, Set<Tv>> map, Tk1 k1, Tk2 k2, Tv v)
	{
		Set<Tv> set = null;
		Pair<Tk1, Tk2> key = new Pair<>(k1, k2);
		if(!map.containsKey(key))
		{
			set = new HashSet<>();
			map.put(key, set);
		}
		else
		{
			set = map.get(key);
		}
		set.add(v);
	}
	
	public static<Tk, Tv> void merge(Map<Tk, Set<Tv>> map1, Map<Tk, Set<Tv>> map2)
	{
		for (Entry<Tk, Set<Tv>> kv : map2.entrySet())
		{
			for (Tv val : kv.getValue())
			{
				Utils.addMapSet(map1, kv.getKey(), val);
			}
		}
	}
	
	public static<T> Set<T> setDiff(Set<T> s1, Set<T> s2)
	{
		Set<T> ret = new HashSet<>();
		for(T t : s1)
		{
			if(!s2.contains(t))
			{
				ret.add(t);
			}
		}
		return ret;
	}
	
	public static<T> Set<T> intersect(Set<T> s1, Set<T> s2)
	{
		if(s1.size() > s2.size())
		{
			Set<T> tmp = s1;
			s1 = s2;
			s2 = tmp;
		}
		Set<T> result = new HashSet<>();
		for(T t : s1)
		{
			if(s2.contains(t))
			{
				result.add(t);
			}
		}
		return result;
	}
	
	public static<K, V> V putUnique(Map<K, V> map, K key, V value) throws UniquenessException
	{
		if(map.containsKey(key))
		{
			throw new UniquenessException(String.format("The key '%s' already exists in the map", key));
		}
		return map.put(key, value);
	}
	
	public static<Tk, Tv> Map<Tk, Set<Tv>> mapSetDiff(Map<Tk, Set<Tv>> a, Map<Tk, Set<Tv>> b)
	{
		Map<Tk, Set<Tv>> ret = new HashMap<>();
		for(Entry<Tk, Set<Tv>> kv : a.entrySet())
		{
			Tk key = kv.getKey();
			Set<Tv> valueSet = kv.getValue();
			Set<Tv> bValueSet = b.getOrDefault(key, new HashSet<>());
			valueSet = setDiff(valueSet, bValueSet);
			ret.put(key, valueSet);
		}
		return ret;
	}
	
	public static<Tk, Tv> Map<Tv, Tk> invertMap(Map<Tk, Tv> map) throws UniquenessException
	{
		Map<Tv, Tk> ret = new HashMap<>();
		for(Entry<Tk, Tv> kv : map.entrySet())
		{
			Tk k = kv.getKey();
			Tv v = kv.getValue();
			putUnique(ret, v, k);
		}
		return ret;
	}
	
	public static<T1,T2> List<T2> map(List<T1> list, java.util.function.Function<T1, T2> fn)
	{
		List<T2> ret = new ArrayList<>();
		for(T1 value : list)
		{
			ret.add(fn.apply(value));
		}
		return ret;
	}
	
	public static<T1,T2> LLst<T2> map(LLst<T1> list, java.util.function.Function<T1, T2> fn)
	{
		if(list == null) { return null; }
		return new LLst<>(fn.apply(list.head), map(list.tail, fn));
	}
	
	public static<T1,T2> Set<T2> map(Set<T1> set, java.util.function.Function<T1, T2> fn)
	{
		Set<T2> ret = new HashSet<>();
		for(T1 value : set)
		{
			ret.add(fn.apply(value));
		}
		return ret;
	}
	
	public static<T1, T2> List<T2> mapB(List<Pair<T1, T2>> list)
	{
		List<T2> ret = new ArrayList<>();
		for(Pair<T1, T2> value : list)
		{
			ret.add(value.b);
		}
		return ret;
	}
	
	public static<T1, T2, T3> List<T3> zip(List<T1> as, List<T2> bs, java.util.function.BiFunction<T1, T2, T3> combiner)
	{
		assert(as.size() == bs.size());
		List<T3> ret = new ArrayList<>();
		for(int i=0; i<as.size(); ++i)
		{
			ret.add(combiner.apply(as.get(i), bs.get(i)));
		}
		return ret;
	}
	
	public static<Tk, Tobj> Map<Tk, List<Tobj>> groupBy(Collection<? extends Tobj> collection, java.util.function.Function<Tobj, Tk> fn)
	{
		Map<Tk, List<Tobj>> ret = new HashMap<>();
		for(Tobj obj : collection)
		{
			Tk key = fn.apply(obj);
			Utils.addMapList(ret, key, obj);
		}
		return ret;
	}
	
	public static<T> T fixedPoint(T data, java.util.function.Function<T, Pair<T, Boolean>> fn)
	{
		while(true)
		{
			Pair<T,Boolean> result = fn.apply(data);
			if(result.b == false)
			{
				break;
			}
			data = result.a;
		}
		return data;
	}
	
	public static<T> Set<T> transitiveClosure(
			T root, 
			final Map<T, ? extends Collection<T>> related, 
			final ContainsFn<T, Set<T>> contains)
	{
		return transitiveClosure(
				root, 
				new java.util.function.Function<T, Collection<T>>() {
					public Collection<T> apply(T value) {
						return Utils.mustGet(related, value);
				}},
				contains
		);
	}
	
	public static<T> Set<T> transitiveClosure(
			T root, 
			java.util.function.Function<T, ? extends Collection<T>> related)
	{
		return transitiveClosure(root, related, new ContainsFn<T, Set<T>>() {
					public boolean contains(Set<T> v1, T v2) {
						return v1.contains(v2);
				}});
	}
	
	public static<T> Set<T> transitiveClosure(
			T root, 
			java.util.function.Function<T, ? extends Collection<T>> related,
			final ContainsFn<T, Set<T>> containsChecker)
	{
		Set<T> ret = new HashSet<>();
		
		Stack<T> stack = new Stack<>();
		stack.push(root);
		while(stack.size() != 0)
		{
			T t = stack.pop();

			if(ret.contains(t))
			{
				continue; // we've already processed it
			}
			ret.add(t);
			
			Collection<T> trelated = related.apply(t);
			for(T t2 : trelated)
			{
				if(!ret.contains(t2))
				{
					stack.push(t2);
				}
			}
		}
		return ret;
	}
	
	// Different from Java's String.subString in that it doesn't throw
	// if n is larger than the string length, but just returns as many
	// characters as possible
	public static String left(String text, int n)
	{
		n = n <=text.length() ? n : text.length();
		return text.substring(0, n);
	}
	
	// Different from Java's String.subString in that it doesn't throw
	// if n is larger than the string length, but just returns as many
	// characters as possible
	public static String mid(String text, int i, int n)
	{
		int endIndex = i + n;
		if(endIndex > text.length())
		{
			endIndex = text.length();
		}
		return text.substring(i, endIndex);
	}
	
	public static String strIf(String s)
	{
		if(s == null) { return ""; }
		return s;
	}
	
	public static String spIf(String a, String b)
	{
		return spIf(a, b, " ");
	}
	
	public static String spIf(String a, String b, String delim)
	{
		if(!a.equals("") && !b.equals(""))
		{
			return a + delim + b;
		}
		else if(a.equals(""))
		{
			return b;
		}
		else
		{
			return a;
		}
	}
	
	public static<T> String join(Iterable<T> exprs, String separator)
	{
		Iterator<T> iter = exprs.iterator();
		if(!iter.hasNext())
			return "";
		
		StringBuilder sb = new StringBuilder();
		String delim = "";
		while(iter.hasNext())
		{
			T value = iter.next();
			sb.append(delim);
			delim = separator;
			sb.append(value);
		}
		
		return sb.toString();
	}
	
	public static<T> String surroundJoin(Collection<T> exprs, String surround, String separator)
	{
		if(exprs.size() == 0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		String delim = "";
		for(T value : exprs)
		{
			sb.append(delim);
			delim = separator;
			sb.append(surround);
			sb.append(value);
			sb.append(surround);
		}
		
		return sb.toString();
	}
	
	public static<T> String surroundJoin(Collection<T> exprs, String surroundLeft, String surroundRight, String separator)
    {
        if(exprs.size() == 0)
            return "";
        
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for(T value : exprs)
        {
            sb.append(delim);
            delim = separator;
            sb.append(surroundLeft);
            sb.append(value);
            sb.append(surroundRight);
        }
        
        return sb.toString();
    }
	
	public static<T> String joinMap(Collection<T> args, String sep, java.util.function.Function<T, String> fn)
	{
		if(args.size() == 0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		String delim = "";
		for(T value : args)
		{
			sb.append(delim);
			delim = sep;
			sb.append(fn.apply(value));
		}
		
		return sb.toString();
	}
	
	public static<T> String joinMap(T[] args, String sep, java.util.function.Function<T, String> fn)
	{
		if(args.length == 0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		String delim = "";
		for(T value : args)
		{
			sb.append(delim);
			delim = sep;
			sb.append(fn.apply(value));
		}
		
		return sb.toString();
	}	
	
	public static<T> String joinMap(Collection<T> args, String sep, java.util.function.BiFunction<T, Integer, String> fn)
	{
		if(args.size() == 0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		String delim = "";
		int i=0;
		for(T value : args)
		{
			sb.append(delim);
			delim = sep;
			sb.append(fn.apply(value, i));
			++i;
		}
		
		return sb.toString();
	}
	
	public static String limitString(String s, int n)
	{
		if(s.length() <=n)
		{
			return s;
		}
		return s.substring(0, n-3) + "...";
	}
	
	public static String repeat(String str, int n)
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<n; ++i)
		{
			sb.append(str);
		}
		
		return sb.toString();
	}
	
	public static String pad(int n, int width)
	{
		String s = "" + n;
		
		int missing = width - s.length();
		if(missing > 0)
		{
			s = repeat("0", missing) + s;
		}
		return s;
	}
	
	public static String readAllFile(String filename) throws IOException
	{
		BufferedReader r = new BufferedReader(new FileReader(filename));
		StringBuilder sb = new StringBuilder();
		String line;
		String delim = "";
		while((line = r.readLine()) != null)
		{
			sb.append(delim + line);
			delim = "\n";
		}
		r.close();
		return sb.toString();
	}
	
	public static void writeToFile(String filename, String text) throws IOException
	{
		BufferedWriter w = new BufferedWriter(new FileWriter(filename));
		w.write(text);
		w.close();
	}
	
	public static ProcessResult runProcessAndWait(String... cmdLine) throws InterruptedException, IOException
	{
		ProcessBuilder builder = new ProcessBuilder(cmdLine);
		builder.inheritIO().redirectError(ProcessBuilder.Redirect.PIPE);
		Process process = builder.start();
		
		StringBuilder sb2 = new StringBuilder();
		
		BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));		
		new Thread(new Runnable() {
			@Override public void run()
			{
				String line;
	
				try
				{
					while(null != (line = br2.readLine()))
					{
						System.out.println(line);
						sb2.append(line);
					}
					br2.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					return;
				}
			}
		}).run();

		int exitv = process.waitFor();
		return new ProcessResult(exitv, null, sb2.toString());
	}
	
	public static ProcessResult runProcessAndWait(String stdIn, String[] cmdLine) throws InterruptedException, IOException
	{
		ProcessBuilder builder = new ProcessBuilder(cmdLine);
		builder.redirectError(ProcessBuilder.Redirect.PIPE);
		builder.redirectInput(ProcessBuilder.Redirect.PIPE);
		builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
		
		
		Process process = builder.start();
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		w.write(stdIn);
		w.close();
		
		StringBuilder sb1 = new StringBuilder(), sb2 = new StringBuilder();
		
		BufferedReader br1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		
		new Thread(new Runnable() {
			@Override public void run()
			{
				String line;
	
				try
				{
					while(null != (line = br2.readLine()))
					{
						//System.out.println(line);
						sb2.append(line);
					}
					br2.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					return;
				}
			}
		}).run();
		
		new Thread(new Runnable() {
			@Override public void run()
			{
				String line;
	
				try
				{
					while(null != (line = br1.readLine()))
					{
						//System.out.println(line);
						sb1.append(line);
					}
					br1.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					return;
				}
			}
		}).run();

		int exitv = process.waitFor();
		return new ProcessResult(exitv, sb1.toString(), sb2.toString());
	}

	public static<T> Set<T> setUnion(Set<T> a, Set<T> b)
	{
		Set<T> result = new HashSet<>();
		result.addAll(a);
		result.addAll(b);
		return result;
	}

	public static String toStringIfNotNull(Object v)
	{
		return v != null ? v.toString() : "";
	}

	public static<Tk, Tv> Map<Tk, Set<Tv>> copyMapSet(Map<Tk, Set<Tv>> m)
	{
		Map<Tk, Set<Tv>> result = new HashMap<>();
		for(Entry<Tk, Set<Tv>> kv : m.entrySet())
		{
			result.put(kv.getKey(), set(kv.getValue()));
		}
		return result;
	}

	public static String parentDir(String file)
	{
		String ret = new File(file).getParent();
		return ret;
	}
	
	public static String combinePath(String path, String filename)
	{
		return new File(new File(path), filename).getAbsolutePath();
	}

	// Diff is done using KEYS ONLY
	public static<Tk, Tv> Map<Tk, Tv> diffByKeys(Map<Tk, Tv> a, Map<Tk, Tv> b)
	{
		Map<Tk, Tv> result = new HashMap<>();
		
		for(Entry<Tk, Tv> kv : a.entrySet())
		{
			Tk key = kv.getKey();
			if(!b.containsKey(key))
			{
				result.put(key, kv.getValue());
			}
		}
		return result;
	}
}
