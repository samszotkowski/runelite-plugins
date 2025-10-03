package com.slayerhistory;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(SlayerHistoryConfig.CONFIG_GROUP)
public interface SlayerHistoryConfig extends Config
{
	String CONFIG_GROUP = "slayerhistory";

	@ConfigItem(
		keyName = "logTimeFormat",
		name = "Time Format",
		description = "Display completion times in 12 or 24 hour time format."
	)
	default TimeFormat timeFormatMode()
	{
		return TimeFormat.TIME_24H;
	}
}
