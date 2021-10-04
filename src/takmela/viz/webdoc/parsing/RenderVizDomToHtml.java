package takmela.viz.webdoc.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import takmela.engine.Call;
import takmela.engine.Cont;
import takmela.tree.Node;
import takmela.tree.Treeish;
import takmela.viz.TraceUtils;
import takmela.viz.webdoc.tdom.parser.Iteration;
import takmela.viz.webdoc.tdom.parser.JoinFromSuccessStep;
import takmela.viz.webdoc.tdom.parser.JoinFromNewCallStep;
import takmela.viz.webdoc.tdom.parser.Processing;
import takmela.viz.webdoc.tdom.parser.ProcessingOp;
import takmela.viz.webdoc.tdom.parser.Step;
import takmela.viz.webdoc.tdom.parser.StepStatus;
import takmela.viz.webdoc.tdom.parser.Success;
import takmela.viz.webdoc.tdom.parser.TopLevelCallStep;
import utils_takmela.Box;
import utils_takmela.CodeWriter;
import utils_takmela.Pair;
import utils_takmela.Utils;

public class RenderVizDomToHtml
{
	public static final int ProcessTreeIndent = 20;
	
	public static String render(TraceDOM dom, WebTraceConfiguration config) throws IOException
	{
		CodeWriter w = new CodeWriter();
				
		if(config.TemplatePath == null)
		{
			w.print("<!doctype html><html><head><meta charset=\"utf-8\"><link rel=\"stylesheet\" href=\"../../dom.css\"><title>Takmela trace</title>");
			w.print("<script src=\"../../dom.js\"></script></head><body>");
		
			renderDiv(w, dom, config);
		
			w.print("</body></html>");
			return w.buildString();
		}
		else
		{
			String templ = Utils.readAllFile(config.TemplatePath);
			renderDiv(w, dom, config);
			String div = w.buildString();
			String html = templ.replace("{{trace_is_here}}", div);
			return html;
		}
	}
	
	public static String renderDiv(TraceDOM dom, WebTraceConfiguration config)
	{
		CodeWriter w = new CodeWriter();
		renderDiv(w, dom, config);
		return w.buildString();
	}
	
	public static void renderDiv(CodeWriter w, TraceDOM dom, WebTraceConfiguration config)
	{
		w.print("<div class=\"takmela-trace\">");
		
		w.print("<table>");
		
		Box<Integer> imgIndex = new Box<>(0);
		Box<Integer> stepNum = new Box<>(0);
		List<Success> successesSoFar = new ArrayList<>();
		
		w.printf("<tr><td colspan=\"2\" class=\"iterationHeader\">Call start nonterminal </span></td></tr>");
		buildSteps(w, dom.InitialTopLevelCalls, imgIndex, successesSoFar, stepNum, config);
		
		int i = 0;
		for(Iteration iter : dom.Iterations)
		{
			w.printf("<tr><td colspan=\"2\" class=\"iterationHeader\">Iteration %s%s</span></td></tr>", i, iter.ReachedFixedPoint? "  [Fixed point reached]": "");
			summarizeWorklist(w, iter, config);
			
			if(iter.Steps.size() == 0 && !iter.ReachedFixedPoint)
			{
				w.print("<tr><td>(No new calls or successes that can be further processed)</td></tr>");
			}
			
			buildSteps(w, iter, imgIndex, successesSoFar, stepNum, config);
			
			++i;
		}
		w.print("</table>");
		w.print("</div>");
	}

	private static void summarizeWorklist(CodeWriter w, Iteration iter, WebTraceConfiguration config)
	{
		if(iter.SuccessWorklist.size() != 0 || iter.KWorkList.size() !=0)
		{
			w.printf("<tr><td colspan=\"2\"><table width=\"100%%\"><tr><td width=\"40%%\"><span class=\"worklistHeader\">Worklist - successes</span>");
				for(Entry<takmela.engine.Call, List<Pair<Integer, Set<Treeish>>>> kv : iter.SuccessWorklist.entrySet())
				{
					takmela.engine.Call call = kv.getKey();
					for(Pair<Integer, Set<Treeish>> p : kv.getValue())
					{
						int pos = p.a;
						Set<Treeish> trees = p.b;
						w.printf("<div>"); 
						
						Pair<Call, Pair<Integer, Set<Treeish>>> key = new Pair<>(call, p);
						
						boolean check = false;
						
						if(iter.IdleSuccesses.contains(key))
						{
							w.printf("<span class=\"idleSuccess\">nJ</span>");
							check = true;
						}
						
						if(iter.ActiveSuccesses.contains(key))
						{
							// not 'else if', because we want to discover any bugs
							// that make a success marked both active and idle
							w.printf("<span class=\"activeSuccess\">jS</span>");
							check = true;
						}
						if(iter.UsedSuccesses.contains(key))
						{
							w.printf("<span class=\"activeSuccess\">⤙K</span>");
							check = true;
						}
						if(!check)
						{
							throw new RuntimeException("Invariant: a worklist item must be active, idle, or used!");
						}
						w.printf("<span class=\"success\">%s</span></div>", formatSuccess(call, pos, trees, 
								config.ShowForestWithSuccessesInWorklist, config.TreeDepthShift));
					}
				}
			w.printf("</td><td><span class=\"worklistHeader\">Worklist - continuations</span>");
				for(Entry<takmela.engine.Call, Set<Cont>> kv : iter.KWorkList.entrySet())
				{
					takmela.engine.Call call = kv.getKey();
					for(Cont cont : kv.getValue())
					{
						
						w.printf("<div>");
						
						Pair<Call, Cont> key = new Pair<>(call, cont);
						boolean check = false;
						
						if(iter.IdleNewCalls.contains(key))
						{
							w.printf("<span class=\"idleNewCall\">nJ</span>");
							check = true;
						}
						if(iter.ActiveNewCalls.contains(key))
						{
							// not 'else if', for the same reason as above
							w.printf("<span class=\"activeNewCall\">jK</span>");
							check = true;
						}
						if(iter.UsedNewCalls.contains(key))
						{
							w.printf("<span class=\"usedNewCall\">S⤚</span>");
							check = true;
						}
						
						if(!check)
						{
							throw new RuntimeException("Invariant: a worklist item must be active, idle, or used!");
						}
						
						w.printf("<span class=\"snippet\">%s</span> → <span class=\"snippet\">%s</span></div>" , 
								call, formatK(cont, config.ShowFullContinuationsInWorklist));
					}
				}
			w.printf("</td></tr></table></td></tr>");
		}
	}

	private static void buildSteps(CodeWriter w, Iteration iter, Box<Integer> imgIndex, List<Success> successesSoFar,
			Box<Integer> stepNum, WebTraceConfiguration config)
	{
		boolean first = true;
		for(Step step : iter.Steps)
		{
			w.printf("<tr class=\"step-tr %s\"><td><img class=\"graph\" src=\"%strace%s.png\"></td><td rowspan=\"2\">",
					first? "first-step-tr" : "",
					config.GraphImgPathPrefix,
					Utils.pad(imgIndex.Value++, 3));
			
			first = false;
			
			String desc = "#" + (stepNum.Value++) + ": " + formatDescription(step, config);
			w.printf("<div><div class=\"step\">%s</div>", desc);
			
			if(step.getStatus() == StepStatus.ResumingRootNoSuccess)
			{
				w.printf("<span class=\"cross\">×</span>Didn't reach end of input, not a successful parse");
			}
			else if(step.getStatus() == StepStatus.ResumingRootSuccess)
			{
				w.printf("<span class=\"tick\">✓</span>Reached end of input, successful parse!");
			}
			else if(step.getStatus() == StepStatus.Processing)
			{
				List<Processing> procs = step.getProcessings();
				for(Processing proc : procs)
				{
					// TODO: can this happen? A step without a processing tree?
					buildProcessingTree(proc, w, config);
				}
				
				if(!step.getNewConts().isEmpty())
				{
					w.print("New continuations:<hr style=\"border:1px dotted black\">");
					for(Entry<takmela.engine.Call, Set<Cont>> kv : step.getNewConts().entrySet())
					{
						takmela.engine.Call call = kv.getKey();
						Set<Cont> ks = kv.getValue();
						for(Cont k : ks)
						{
							w.printf("<div><span class=\"snippet\">%s</span> → <span class=\"snippet\">%s</span></div>" , 
									call, formatK(k, config.ShowFullContinuationsInNewContinuations));
						}
					}
				}
				
				w.print("</div>");
				w.print("</td>");
			}
			else
			{
				throw new RuntimeException("Unknown step status: " + step.getStatus());
			}
			
			w.print("<tr><td class=\"successes-cell\"><div class=\"successes\">");
			for(Success s : successesSoFar)
			{
				w.printf("<span class=\"success\">%s%s</span>", 
						formatSuccess(s.Exec, s.InputPosNow, s.Tree, config.ShowTreesWithSuccesses, config.TreeDepthShift),
						s.Root ? "<span class=\"tick\">✓</span>": "");
				
			}
			for(Success s : step.getSuccesses())
			{
				w.printf("<span class=\"new-success\">%s%s</span>", 
						formatSuccess(s.Exec, s.InputPosNow, s.Tree, config.ShowTreesWithSuccesses, config.TreeDepthShift),
						s.Root ? "<span class=\"tick\">✓</span>": "");
			}
			successesSoFar.addAll(step.getSuccesses());
			
			w.print("</div></td></tr></tr>");
		}
	}

	private static Object formatSuccess(Call exec, int inputPosNow, Node tree, 
			boolean showTreeWithSuccesses,
			boolean treeDepthShift)
	{
		if(showTreeWithSuccesses)
		{
			return String.format("%s → %s, %s", exec, inputPosNow, TraceUtils.formatSuccessTreeRainbowHtml(tree, treeDepthShift));
		}
		else
		{
			return String.format("%s → %s", exec, inputPosNow);
		}
	}

	private static String formatDescription(Step step, WebTraceConfiguration config)
	{
		if(step instanceof TopLevelCallStep)
		{
			TopLevelCallStep s = (TopLevelCallStep) step;
			return "Called <span class=\"snippet\">" + s.Callee + "</span> at the top level (will process)";
		}
		else if(step instanceof JoinFromNewCallStep)
		{
			JoinFromNewCallStep s = (JoinFromNewCallStep) step;

			return String.format("JoinK: <span class=\"snippet\">%s</span> will use <span class=\"snippet\">%s</span> to resume " + 
			"<span class=\"snippet\">%s</span>", 
				s.Callee,
				formatSuccess(s.Callee, s.InputPosAfterSuccess, s.SuccessTrees, config.ShowForestWithSuccessesInJoins,
						config.TreeDepthShift), 
				formatK(s.Cont, config.ShowFullContinuationsInJoins));
		}
		else if(step instanceof JoinFromSuccessStep)
		{
			JoinFromSuccessStep s = (JoinFromSuccessStep) step;
			return String.format("JoinS: <span class=\"snippet\">%s</span> will resume cont: <span class=\"snippet\">%s</span>",
						formatSuccess(s.Succeeded, s.InputPosAfterSuccess, s.SuccessTrees, 
								config.ShowForestWithSuccessesInJoins, config.TreeDepthShift), 
						formatK(s.Cont, config.ShowFullContinuationsInJoins));
			
		}
		else
		{
			throw new RuntimeException("Option not handled: " + step);
		}
	}

	private static Object formatSuccess(Call succeeded, int inputPosAfterSuccess, Collection<? extends Node> successTrees,
			SuccessTreeDisplay showForestWithSuccesses, boolean depthShift)
	{
		String forest = null;
		
		switch(showForestWithSuccesses)
		{
		case Count:
			forest = String.format(" (%s木)", successTrees.size());
			break;
		case Full:
			forest = String.format(" %s", formatSuccessTreesHtml(successTrees, depthShift));
			break;
		case None:
			forest = "";
			break;
		default:
			throw new RuntimeException("Option not handled: " + showForestWithSuccesses);
		
		}
		return String.format("%s  → %s%s", succeeded, inputPosAfterSuccess, forest);
	}

	private static Object formatK(Cont cont, boolean showFullK)
	{
		return TraceUtils.formatK(cont, showFullK);
	}

	private static String formatSuccessTreesPlain(Collection<? extends Node> successTrees)
	{
		return Utils.joinMap(successTrees, ", ", t->TraceUtils.formatSuccessTreePlain(t));
	}
	
	private static String formatSuccessTreesHtml(Collection<? extends Node>  successTrees, boolean depthShift)
	{
		return Utils.joinMap(successTrees, "", t-> "<span class=\"forest\"><span class=\"treeSep\">木</span>" + 
												TraceUtils.formatSuccessTreeRainbowHtml(t, depthShift) +
												"</span>");
	}
	
	private static void buildProcessingTree(Processing proc, CodeWriter w, WebTraceConfiguration config)
	{
		buildProcessingTree(proc, w, 0, config);
	}
	
	private static void buildProcessingTree(Processing proc, CodeWriter w, int level, WebTraceConfiguration config)
	{
		w.printf("<div class=\"processing\" style=\"margin-left:%spx;\">", level * ProcessTreeIndent);
		w.printf("<div class=\"processingHeader\">Processing <span class=\"snippet\">%s</span> " 
				+"codePos <span class=\"snippet\">%s</span> inputPos <span class=\"snippet\">%s</span></div>",
				proc.Callee, proc.CodePos, proc.InputPos); 
		
		for(ProcessingOp p : proc.Operations)
		{
			w.print("<div class=\"processingOp\">");
			if(p instanceof takmela.viz.webdoc.tdom.parser.Call)
			{
				takmela.viz.webdoc.tdom.parser.Call call = (takmela.viz.webdoc.tdom.parser.Call) p;
				w.printf("Calling: <span class=\"snippet\">%s</span> with Cont <span class=\"snippet\">%s</span> [%s]", 
						call.Callee, formatK(call.Cont, config.ShowFullContinuationsInProcessCall), call.WillProcess? "will process" : "already processed");
				if(call.WillProcess)
				{
					for(Processing pr : call.Processings)
					{
						buildProcessingTree(pr, w, level+1, config);
					}
				}
			}
			else if(p instanceof takmela.viz.webdoc.tdom.parser.Match)
			{
				takmela.viz.webdoc.tdom.parser.Match m = (takmela.viz.webdoc.tdom.parser.Match) p;
				if(m.Succeeded)
				{
					w.printf("Match <span class=\"snippet\">%s</span> to <span class=\"snippet\">%s</span>, matched; " + 
							 "inputPos now <span class=\"snippet\">%s</span>", m.InputToMatch, m.TokenToMatch, m.InputPosAfter);
				}
				else
				{
					w.printf("Match <span class=\"snippet\">%s</span> to <span class=\"snippet\">%s</span>, fail", m.InputToMatch, m.TokenToMatch);
				}
			}
			else if(p instanceof takmela.viz.webdoc.tdom.parser.Success)
			{
				takmela.viz.webdoc.tdom.parser.Success s = (Success) p;
				w.printf("Reached end of rule, success! <span class=\"snippet newItem\">%s → %s</span>",
						s.Exec, s.InputPosNow);
			}
			else
			{
				throw new RuntimeException("Option not handled:" + p);
			}
			w.print("</div>"); // processingOp
		}
		w.print("</div>"); // processing
	}
}
