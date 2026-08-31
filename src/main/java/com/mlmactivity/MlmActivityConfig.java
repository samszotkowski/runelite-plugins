package com.mlmactivity;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("mlm-activity-counter")
public interface MlmActivityConfig extends Config
{
	@ConfigItem(
		keyName = "soundOn",
		name = "Play area sounds",
		position = 1,
		description = "Play a 'tick-tock' sound when the ore vein is almost depleted."
	)
	default boolean soundOn()
	{
		return true;
	}
}
