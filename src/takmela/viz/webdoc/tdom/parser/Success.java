package takmela.viz.webdoc.tdom.parser;

import takmela.engine.Call;
import takmela.tree.Node;

public class Success implements ProcessingOp
{
	public Call Exec;
	public int InputPosNow;
	public Node Tree;
	public boolean Root;
	
	public Success(Call exec, int inputPosNow, Node tree, boolean root)
	{
		Exec = exec;
		InputPosNow = inputPosNow;
		Tree = tree;
		Root = root;
	}
}
