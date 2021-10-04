package takmela.viz.graphicsElements;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;

public class RenderMetrics
{
	private GraphicsConfiguration grfxConfig;
	private FontMetrics fontMetrics;
	private Graphics fontMetricsGraphics;
	
	public RenderMetrics(GraphicsConfiguration gfxConfig)
	{
	    
		this.grfxConfig = gfxConfig;
		BufferedImage tmp = grfxConfig.createCompatibleImage(1, 1);
		fontMetricsGraphics = tmp.getGraphics();
		fontMetrics = fontMetricsGraphics.getFontMetrics();
	}
	
	public void dispose()
	{
		disposeFontMetricsGraphics();
	}
	
	public GraphicsConfiguration grfxConfig()
	{
		return grfxConfig;
	}

	public FontMetrics fontMetrics()
	{
		return fontMetrics;
	}

	public Graphics fontMetricsGraphics()
	{
		return fontMetricsGraphics;
	}
	
	@Override public void finalize()
	{
		dispose();
	}

	private void disposeFontMetricsGraphics()
	{
		if (fontMetricsGraphics != null)
		{
			fontMetricsGraphics.dispose();
			fontMetricsGraphics = null;
		}
	}
}
