package utils_takmela;

import java.util.HashSet;
import java.util.Set;

public class Untangle<T>
{
	// Each Pair<T,T> is an inclusive [a-b] range
	public Set<Pair<T, T>> onlyA = new HashSet<>();
	public Pair<T,T> common;
	public Set<Pair<T, T>> onlyB = new HashSet<>();
	
	
	public static Untangle<Integer> untangle(int aFrom, int aTo, int bFrom, int bTo)
	{
		if(aFrom > bTo || aFrom > aTo)
		{
			Untangle<Integer> result = new Untangle<>();
			result.onlyA.add(new Pair<>(aFrom, aTo));
			result.onlyB.add(new Pair<>(bFrom, bTo));
			result.common = null;
			return result;
		}
		else
		{
			boolean swap = false;
			
			if(aFrom > bFrom)
			{
				swap = true;
				
				int temp = aFrom;
				aFrom = bFrom;
				bFrom = temp;
				
				temp = aTo;
				aTo = bTo;
				bTo = temp;
			}
			
			if(aFrom == bFrom && aTo > bTo)
			{
				swap = true;
				
				int temp = aFrom;
				aFrom = bFrom;
				bFrom = temp;
				
				temp = aTo;
				aTo = bTo;
				bTo = temp;
			}
			
			Untangle<Integer> ret = untangle__(aFrom, aTo, bFrom, bTo);
			if(swap)
			{
				Set<Pair<Integer, Integer>> temp = ret.onlyA;
				ret.onlyA = ret.onlyB;
				ret.onlyB = temp;
			}
			return ret;
		}
	}
	
	/*
	 	Input: - Two INCLUSIVE ranges 
	 	       - ...that must have a part in common
	 	       - ...and must be canonicalized as follows:
	 	       		* a.from <= b.from                  is always true
	 	       		* a.to < b.to if a.from==b.from     is always true
	 	       		* from <= to                        is always true for a & b 					 
	 	Output: Three disjoint sets: (A - common, common, B - common)
	 	        each set can be zero, one or more char ranges
	 	This code is very tricky, needs a lot of testing
	 */
	private static Untangle<Integer> untangle__(int aFrom, int aTo, int bFrom, int bTo)
	{
		Untangle<Integer> ret = new Untangle<>();
		
		int f1 = aFrom;
		int t1 = bFrom -1;
		if(f1 <= t1) { ret.onlyA.add(new Pair<>(f1, t1)); }
		
		f1 = bTo + 1;
		t1 = aTo;
		if(f1 <= t1) { ret.onlyA.add(new Pair<>(f1, t1)); }
			
		f1 = aTo +1;
		t1 = bTo;
		if(f1 <= t1) { ret.onlyB.add(new Pair<>(f1, t1)); }
		
		f1 = bFrom;
		t1 = Math.min(aTo, bTo);
		ret.common = new Pair<>(f1, t1);
		
		return ret;
	}
}
