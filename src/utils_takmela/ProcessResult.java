package utils_takmela;

public class ProcessResult
{
	public final int Exitv;
	public final String StdOut, StdErr;
	public ProcessResult(int exitv, String stdOut, String stdErr)
	{
		super();
		Exitv = exitv;
		StdOut = stdOut;
		StdErr = stdErr;
	}
}
