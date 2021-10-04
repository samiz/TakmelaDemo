package takmela.lexer.automata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils_takmela.Pair;
import utils_takmela.Utils;

public class FA
{
	public Map<Integer, List<Pair<Trans, Integer>>> tg = new HashMap<>();
	public int startState;
	public Set<Integer> acceptingStates;

	public FA(int startState, int acceptingState)
	{
		this.startState = startState;
		this.acceptingStates = Utils.set(acceptingState);
	}

	public FA(int startState, Set<Integer> acceptingStates)
	{
		this.startState = startState;
		this.acceptingStates = Utils.set(acceptingStates);
	}

	public FA(int startState, int acceptingState, Map<Integer, List<Pair<Trans, Integer>>> tg)
	{
		this.startState = startState;
		this.acceptingStates = Utils.set(acceptingState);
		this.tg = tg;
	}

	public FA(int startState, Set<Integer> acceptingStates, Map<Integer, List<Pair<Trans, Integer>>> tg)
	{
		this.startState = startState;
		this.acceptingStates = Utils.set(acceptingStates);
		this.tg = tg;
	}

	public FA trans(int from, int to, Trans t)
	{
		Utils.addMapList(tg, from, new Pair<>(t, to));
		return this;
	}

	public FA trans(int from, int to, List<Trans> ts)
	{
		for (Trans t : ts)
		{
			Utils.addMapList(tg, from, new Pair<>(t, to));
		}
		return this;
	}

	public FA merge(FA fa)
	{
		tg.putAll(fa.tg);
		return this;
	}

	public FA merge(List<FA> fas)
	{
		for (FA fa : fas)
		{
			tg.putAll(fa.tg);
		}
		return this;
	}

	public List<Pair<Trans, Integer>> outTrans(int state)
	{
		return tg.getOrDefault(state, new ArrayList<>());
	}
}
