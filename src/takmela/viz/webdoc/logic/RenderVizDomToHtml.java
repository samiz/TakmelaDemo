package takmela.viz.webdoc.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import takmela.viz.TraceUtils;
import takmela.viz.webdoc.logic.WebTraceConfiguration;
import takmela.viz.webdoc.tdom.datalog.Iteration;
import takmela.viz.webdoc.tdom.datalog.JoinFromNewCallFailedBindingStep;
import takmela.viz.webdoc.tdom.datalog.JoinFromSuccessStep;
import takmela.viz.webdoc.tdom.datalog.JoinFromNewCallStep;
import takmela.viz.webdoc.tdom.datalog.JoinFromSuccessFailedBindingStep;
import takmela.viz.webdoc.tdom.datalog.Processing;
import takmela.viz.webdoc.tdom.datalog.ProcessingOp;
import takmela.viz.webdoc.tdom.datalog.Step;
import takmela.viz.webdoc.tdom.datalog.Success;
import takmela.viz.webdoc.tdom.datalog.TopLevelCallStep;
import takmelogic.ast.Comparison;
import takmelogic.engine.Call;
import utils_takmela.Box;
import utils_takmela.CodeWriter;
import utils_takmela.Pair;
import utils_takmela.Utils;

public class RenderVizDomToHtml
{
	public static final int ProcessTreeIndent = 15;
	
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
	
	public static void renderDiv(CodeWriter w, TraceDOM dom, WebTraceConfiguration config)
	{
		w.print("<div class=\"takmela-trace\">");
		w.print("<table>");
		
		Box<Integer> imgIndex = new Box<>(0);
		Box<Integer> stepNum = new Box<>(0);
		List<Success> successesSoFar = new ArrayList<>();
		
		w.printf("<tr><td colspan=\"2\" class=\"iterationHeader\">Call start rule");
		buildSteps(w, dom.InitialTopLevelCalls, imgIndex, successesSoFar, stepNum, config);

		int i = 0;
		
		for(Iteration iter : dom.Iterations)
		{
			w.printf("<tr><td colspan=\"2\" class=\"iterationHeader\">Iteration %s", i);
			
			w.printf("%s</span></td></tr>", iter.ReachedFixedPoint? "  [Fixed point reached]": "");
			
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
				for(Entry<Call, Set<List<Object>>> kv : iter.SuccessWorklist.entrySet())
				{
					Call call = kv.getKey();
					for(List<Object> tuple : kv.getValue())
					{
						w.printf("<div>");
						
						Pair<Call, List<Object>> key = new Pair<>(call, tuple);
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
						
						if(iter.FailedMatchSuccesses.contains(key))
						{
							w.printf("<span class=\"failedMatchSuccess\">F</span>");
							check = true;
						}
						
						if(!check)
						{
							throw new RuntimeException("Invariant: a worklist item must be active, idle, or used! -- " + key);
						}
						
						w.printf("<span class=\"success\">%s → (%s)</span></div>", call, Utils.join(tuple, ", "));
					}
				}
			w.printf("</td><td><span class=\"worklistHeader\">Worklist - continuations</span>");
				for(Entry<Call, Set<takmelogic.engine.Cont>> kv : iter.KWorkList.entrySet())
				{
					Call call = kv.getKey();
					for(takmelogic.engine.Cont cont : kv.getValue())
					{
						w.printf("<div>"); 
						
						Pair<Call, takmelogic.engine.Cont> key = new Pair<>(call, cont);
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
						if(iter.FailedMatchNewCalls.contains(key))
						{
							w.printf("<span class=\"failedMatchNewCall\">F</span>");
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
			w.printf("<tr class=\"step-tr %s\"><td><img class=\"graph\" src=\"trace%s.png\"></td><td rowspan=\"2\">",
					first? "first-step-tr" : "",
					Utils.pad(imgIndex.Value++, 3));
			
			first = false;
			
			String desc = "#" + (stepNum.Value++) + ": " + formatDescription(step, config);
			w.printf("<div><div class=\"step\">%s</div>", desc);
			
			List<Processing> procs = step.getProcessings();
			for(Processing proc : procs)
			{
				// TODO: can this happen? A step without a processing tree?
				buildProcessingTree(proc, w);
			}
			
			w.print("</div>");
			w.print("</td>");
			
			w.printf("<tr><td class=\"successes-cell\"><div class=\"successes %s\">", 
					config.BiigSuccessesPanel? "biig" : "");
			for(Success s : successesSoFar)
			{
				w.printf("<span class=\"success\">%s → %s%s</span>", s.Exec, s.S, s.Root ? "<span class=\"tick\">✓</span>": "");
			}
			for(Success s : step.getSuccesses())
			{
				w.printf("<span class=\"new-success\">%s → %s%s</span>", s.Exec, s.S, s.Root ? "<span class=\"tick\">✓</span>": "");
			}
			successesSoFar.addAll(step.getSuccesses());
			
			w.print("</div></td></tr></tr>");
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
			return String.format("JoinK: <span class=\"snippet\">%s</span> will use <span class=\"snippet\">%s → %s</span> to resume " + 
					"<span class=\"snippet\">%s</span><br>%s",
					s.Callee, s.Callee, s.S, formatK(s.Cont, config.ShowFullContinuationsInJoins),
					s.NewBindings.isEmpty()? "" : "<br>newVars: <span class=\"snippet\">" + s.NewBindings + "</span>");
		}
		else if(step instanceof JoinFromSuccessStep)
		{
			JoinFromSuccessStep s = (JoinFromSuccessStep) step;
			
			return String.format("JoinS: <span class=\"snippet\">%s  → %s</span> will resume cont: <span class=\"snippet\">%s</span>%s",
					s.Succeeded, s.S, formatK(s.Cont, config.ShowFullContinuationsInJoins),
			s.NewBindings.isEmpty()? "" : "<br>newVars: <span class=\"snippet\">" + s.NewBindings + "</span>");
		}
		else if(step instanceof JoinFromNewCallFailedBindingStep)
		{
			JoinFromNewCallFailedBindingStep s = (JoinFromNewCallFailedBindingStep) step;
			return String.format("JoinK: <span class=\"snippet\">%s</span> trying to resume " +   
					"<span class=\"snippet\">%s</span> with <span class=\"snippet\">%s → %s</span>  \" + <br>failed to bind variables",
					s.Callee, formatK(s.Cont, config.ShowFullContinuationsInJoins), s.Callee, s.S);
		}
		else if(step instanceof JoinFromSuccessFailedBindingStep)
		{
			JoinFromSuccessFailedBindingStep s = (JoinFromSuccessFailedBindingStep) step;
			return String.format("JoinS: <span class=\"snippet\">%s  → %s</span> and cont: <span class=\"snippet\">%s</span>" +
					"<br> failed to bind variables", s.Succeeded, s.S, formatK(s.Cont, config.ShowFullContinuationsInJoins));
		}
		else
		{
			throw new RuntimeException("Option not handled: " + step);
		}
	}

	private static void buildProcessingTree(Processing proc, CodeWriter w)
	{
		buildProcessingTree(proc, w, 0);
	}
	
	private static void buildProcessingTree(Processing proc, CodeWriter w, int level)
	{
		w.printf("<div class=\"processing\" style=\"margin-left:%spx;\">", level * ProcessTreeIndent);
		w.printf("<div class=\"processingHeader\">Processing <span class=\"snippet\">%s</span> " 
				+"codePos <span class=\"snippet\">%s</span></div>",
				proc.Callee, TraceUtils.formatInfusedCodePosForHtml(proc.CodePos, proc.BindingsSoFar)); 
		
		for(ProcessingOp p : proc.Operations)
		{
			w.print("<div class=\"processingOp\">");
			if(p instanceof takmela.viz.webdoc.tdom.datalog.Call)
			{
				takmela.viz.webdoc.tdom.datalog.Call call = (takmela.viz.webdoc.tdom.datalog.Call) p;
				w.printf("Calling: <span class=\"snippet\">%s</span> [%s]", call.Callee, call.WillProcess? "will process" : "already processed");
				if(call.WillProcess)
				{
					for(Processing pr : call.Processings)
					{
						buildProcessingTree(pr, w, level+1);
					}
				}
			}
			else if(p instanceof takmela.viz.webdoc.tdom.datalog.CallAsFact)
			{
				takmela.viz.webdoc.tdom.datalog.CallAsFact call = (takmela.viz.webdoc.tdom.datalog.CallAsFact) p;
				w.printf("Calling fact: <span class=\"snippet\">%s</span>", call.callee);
			}
			else if(p instanceof takmela.viz.webdoc.tdom.datalog.Success)
			{
				takmela.viz.webdoc.tdom.datalog.Success s = (Success) p;
				w.printf("Reached end of rule, success! <span class=\"snippet newItem\">%s → %s</span>",
						s.Exec, s.S);
			}
			else if(p instanceof takmela.viz.webdoc.tdom.datalog.SuccessFact)
			{
				takmela.viz.webdoc.tdom.datalog.SuccessFact s = (takmela.viz.webdoc.tdom.datalog.SuccessFact) p;
				w.printf("Found fact: <span class=\"snippet newItem\">%s → %s</span>",
						s.Exec, s.S);
			}
			else if(p instanceof takmela.viz.webdoc.tdom.datalog.TestEq)
			{
				takmela.viz.webdoc.tdom.datalog.TestEq s = (takmela.viz.webdoc.tdom.datalog.TestEq) p;
				w.printf("Testing: <span class=\"snippet newItem\">%s == %s</span>, %s",
						TraceUtils.formatInfusedTermForHtml(s.A, s.Env),
						TraceUtils.formatInfusedTermForHtml(s.B, s.Env),
						s.TestSuccess? "Succeeded" : "Failed"
						);
			}
			else if(p instanceof takmela.viz.webdoc.tdom.datalog.TestNe)
			{
				takmela.viz.webdoc.tdom.datalog.TestNe s = (takmela.viz.webdoc.tdom.datalog.TestNe) p;
				w.printf("Testing: <span class=\"snippet newItem\">%s != %s</span>, %s",
						TraceUtils.formatInfusedTermForHtml(s.A, s.Env),
						TraceUtils.formatInfusedTermForHtml(s.B, s.Env),
						s.TestSuccess? "Succeeded" : "Failed"
						);
			}
			else if(p instanceof takmela.viz.webdoc.tdom.datalog.TestComp)
			{
				takmela.viz.webdoc.tdom.datalog.TestComp s = (takmela.viz.webdoc.tdom.datalog.TestComp) p;
				w.printf("Testing: <span class=\"snippet\">%s %s %s</span>, %s",
						TraceUtils.formatInfusedTermForHtml(s.A, s.Env),
						Comparison.formatComparisonOpHtml(s.Op),
						TraceUtils.formatInfusedTermForHtml(s.B, s.Env),
						s.TestSuccess? "Succeeded" : "Failed"
						);
			}
			else
			{
				throw new RuntimeException("Option not handled:" + p);
			}
			w.print("</div>"); // processingOp
		}
		
		w.print("</div>"); // processing
	}
	
	private static String formatK(takmelogic.engine.Cont cont, boolean showFullContinuation)
	{
		if(showFullContinuation)
		{
			//return String.format("(%s ; %s ; %s)", cont.call, cont.codePos, cont.bindingsSoFar);
			return TraceUtils.formatInfusedKForHtml(cont);
		}
		else
		{
			return String.format("(%s)", cont.CodePos);
		}
	}
}
