package takmelogic.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import takmelogic.ast.Int;
import takmelogic.ast.Str;
import takmelogic.ast.Symbol;
import takmelogic.ast.Term;
import takmelogic.ast.Var;
import utils_takmela.Box;
import utils_takmela.Utils;

public class InterpreterTools
{
	// unboxTermsForCall & unboxTermForCall convert a list of terms into a row, ready
	// to be passes as arguments. Unbound variables (whether alone or inside compound terms)
	// become 'null'
	public static List<Object> unboxTermsForCall(List<Term> terms, Map<String, Object> bindings)
	{
		List<Object> tuple = new ArrayList<>(terms.size());
		for(int i=0; i<terms.size(); ++i)
		{
			Term _t = terms.get(i);
			tuple.add(unboxTermForCall(_t, bindings));
		}
		return tuple;
	}
	
	// Converts a term to a runtime value
	// suitable for passing as call args; i.e all unresolved Vars become null
	// whether alone or inside a compound term
	public static Object unboxTermForCall(Term _t, Map<String, Object> bindings)
	{
		if(_t instanceof Var)
		{
			Var t = (Var) _t;
			return bindings.get(t.N); // get(..) can return null, which means it's a free variable so okay
		}
		else
		{
			return unboxGroundTerm(_t);
		}
	}
	
	// Unbox when we are sure there are no vars (bound or free)
	// e.g when feeding external data into the system
	public static Object unboxGroundTerm(Term _t)
	{
		if(_t instanceof Int)
		{
			Int t = (Int) _t;
			return t.V;
		}
		else if(_t instanceof Symbol)
		{
			Symbol t = (Symbol) _t;
			return new S(t.V);
		}
		else if(_t instanceof Str)
		{
			Str t = (Str) _t;
			return t.V;
		}
		else if(_t instanceof Var)
		{
			throw new RuntimeException("unboxGroundTerm: cannot have vars");
		}
		else
		{
			throw new RuntimeException("Option not handled " + _t);
		}
	}
	
	public static List<Object> resolvedTerms(List<Term> ts, Map<String, Object> bindings)
	{
		return Utils.map(ts, a->resolvedTerm(a, bindings));
	}
	
	// The same as resolved (returns a 'final', externally usable value), but works
	// with terms rather than runtime values
	public static Object resolvedTerm(Term _t, Map<String, Object> bindings)
	{
		if(_t instanceof Int)
		{
			Int t = (Int) _t;
			return t.V;
		}
		else if(_t instanceof Symbol)
		{
			Symbol t = (Symbol) _t;
			return new S(t.V);
		}
		else if(_t instanceof Str)
		{
			Str t = (Str) _t;
			return t.V;
		}
		else if(_t instanceof Var)
		{
			Var v = (Var) _t;
			return resolveVar(v.N, bindings);
		}
		else
		{
			throw new RuntimeException("Option not handled " + _t);
		}
	}
	
	private static Object resolveVar(String vn, Map<String, Object> bindings)
	{
		return Utils.mustGet(bindings, vn);
	}
	
	public static List<Term> boxValues(List<Object> args)
	{
		Box<Integer> vcount = new Box<>(0);
		return Utils.map(args, a->boxValue(a, vcount));
	}

	public static Term boxValue(Object a, Box<Integer> vcount)
	{
		if(a == null)
		{
			return new Var("V" + vcount.Value++);
		}
		if(a instanceof Integer)
		{
			return new Int((int) a);
		}
		else if(a instanceof String)
		{
			return new Str((String) a);
		}
		else if(a instanceof S)
		{
			return new Symbol(((S) a).Value);
		}
		else 
		{
			throw new RuntimeException("Option not handled:" + a);
		}
	}
}
