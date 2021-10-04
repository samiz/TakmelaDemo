package takmela.viz.graphicsElements;

import java.util.Map;
import utils_takmela.CodeWriter;
import utils_takmela.LabellerInt;
import utils_takmela.Triple;
import java.util.Set;

public class WriteDot
{
	// THIS IS NOT A GENERAL DOT SERIALIZER
	// This code is specifically for writing graphs that trace a Takmela program
	// Therefore we take shortcuts according to that assumption; e.g we don't
	// try to escape every possible kind of string, etc.
	public static String generateDotSpec(Set<String> nodes, Set<Triple<String,String,String>> edges, Map<String, String> outNodeLabelToId)
	{
		CodeWriter cw = new CodeWriter();
		LabellerInt<String> labeller = new LabellerInt<>();
		
		cw.print("digraph g {");
		cw.indent();
		
		//cw.print("graph[ranksep=1.3];");
		
		for(String n : nodes)
		{
			String id = "n" + labeller.labelFor(n);
			outNodeLabelToId.put(n, id);
			cw.printf("%s [label=\"%s\"];", id, n);
		}
		
		for(Triple<String, String, String>  kv : edges)
		{
			String n1 = kv.a;
			String n2 = kv.b;
			String label = kv.c;
			
			String id1 = "n" + labeller.labelFor(n1);
			String id2 = "n" + labeller.labelFor(n2);
			
			cw.printf("%s -> %s[label=\"%s\"];", id1, id2, label);
		}
		cw.dedent();
		cw.print("}");
		
		return cw.buildString();
	}
}

