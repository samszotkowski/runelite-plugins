package com.gatheringloot;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GatheringLootPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GatheringLootPlugin.class);
		RuneLite.main(args);
	}
}