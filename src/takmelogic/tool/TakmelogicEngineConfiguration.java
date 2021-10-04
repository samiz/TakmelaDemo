package takmelogic.tool;

import java.util.function.Consumer;

import takmelogic.engine.TakmelaDatalogEngine;
import takmelogic.engine.TakmelogicTracer;

public class TakmelogicEngineConfiguration
{
	public boolean Tracing = false;
	public TakmelogicTracer UseTracer = null;
	public Consumer<TakmelaDatalogEngine> onFinish;
}
