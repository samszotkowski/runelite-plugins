package com.mlmactivity;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MlmActivityPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MlmActivityPlugin.class);
		RuneLite.main(args);
	}
}