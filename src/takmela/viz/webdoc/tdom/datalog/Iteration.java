package takmela.viz.webdoc.tdom.datalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import takmelogic.engine.Call;
import takmelogic.engine.Cont;
import utils_takmela.Pair;

public class Iteration
{
	public List<Step> Steps = new ArrayList<>();
	public boolean ReachedFixedPoint = false;
	
	public Map<Call, Set<List<Object>>> SuccessWorklist = new HashMap<>();
	public Map<Call, Set<Cont>> KWorkList = new HashMap<>();
	
	public Set<Pair<Call, List<Object>>> IdleSuccesses = new HashSet<>();
	public Set<Pair<Call, Cont>> IdleNewCalls = new HashSet<>();
	public Set<Pair<Call, List<Object>>> ActiveSuccesses = new HashSet<>();
	public Set<Pair<Call, Cont>> ActiveNewCalls = new HashSet<>();
	public Set<Pair<Call, List<Object>>> UsedSuccesses = new HashSet<>();
	public Set<Pair<Call, Cont>> UsedNewCalls = new HashSet<>();
	public Set<Pair<Call, List<Object>>> FailedMatchSuccesses = new HashSet<>();
	public Set<Pair<Call, Cont>> FailedMatchNewCalls = new HashSet<>();
}
