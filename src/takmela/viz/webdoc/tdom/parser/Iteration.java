package takmela.viz.webdoc.tdom.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import takmela.engine.Call;
import takmela.engine.Cont;
import takmela.tree.Treeish;
import utils_takmela.Pair;

public class Iteration
{
	public List<Step> Steps = new ArrayList<>();
	
	public boolean ReachedFixedPoint = false;
	public Map<takmela.engine.Call, List<Pair<Integer, Set<Treeish>>>> SuccessWorklist = new HashMap<>();
	public Map<Call, Set<Cont>> KWorkList = new HashMap<>();
	
	public Set<Pair<Call, Pair<Integer, Set<Treeish>>>> IdleSuccesses = new HashSet<>();
	public Set<Pair<Call, Cont>> IdleNewCalls = new HashSet<>();
	
	public Set<Pair<Call, Pair<Integer, Set<Treeish>>>> ActiveSuccesses = new HashSet<>();
	public Set<Pair<Call, Cont>> ActiveNewCalls = new HashSet<>();
	
	public Set<Pair<Call, Pair<Integer, Set<Treeish>>>> UsedSuccesses = new HashSet<>();
	public Set<Pair<Call, Cont>> UsedNewCalls = new HashSet<>();
}
