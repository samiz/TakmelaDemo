package takmela.viz.webdoc.tdom.parser;

import java.util.List;

public interface ProcessingOwner
{
	void addProcessing(Processing p);
	List<Processing> getProcessings();
}
