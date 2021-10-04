package takmela.viz.webdoc.tdom.datalog;

import java.util.List;

public interface ProcessingOwner
{
	void addProcessing(Processing p);
	List<Processing> getProcessings();
}
