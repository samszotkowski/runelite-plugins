package com.mlmactivity;

import static com.mlmactivity.MlmActivityPlugin.MIN_DURATION_DOWNSTAIRS;
import static com.mlmactivity.MlmActivityPlugin.MIN_DURATION_UPSTAIRS;
import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.overlay.infobox.Counter;

class MlmActivityCounter extends Counter
{
	private final MlmActivityPlugin plugin;

	MlmActivityCounter(BufferedImage img, MlmActivityPlugin plugin, int count)
	{
		super(img, plugin, count);
		this.plugin = plugin;
	}

	@Override
	public Color getTextColor()
	{
		if ((plugin.isUpstairs() && plugin.getTimer() >= MIN_DURATION_UPSTAIRS)
			|| (!plugin.isUpstairs() && plugin.getTimer() >= MIN_DURATION_DOWNSTAIRS))
		{
			return Color.RED;
		}
		else if ((plugin.isUpstairs() && plugin.getTimer() >= MIN_DURATION_UPSTAIRS - 10)
			|| (!plugin.isUpstairs() && plugin.getTimer() >= MIN_DURATION_DOWNSTAIRS - 10))
		{
			return Color.ORANGE;
		}
		return Color.WHITE;
	}
}
