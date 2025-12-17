/*
 * Copyright (c) 2017, Tyler <https://github.com/tylerthardy>
 * Copyright (c) 2018, Shaun Dreclin <shaundreclin@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.slayerhistory;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.slayerhistory.localstorage.SlayerHistoryLocalStorage;
import com.slayerhistory.localstorage.SlayerHistoryRecord;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.DBTableID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneScapeProfileType;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(name = "Slayer History")
public class SlayerHistoryPlugin extends Plugin
{
	// https://oldschool.runescape.wiki/w/RuneScape:Varbit/4067
	private static final Map<Integer, String> SLAYER_MASTERS = Map.of(
		1, "Turael/Aya",
		2, "Mazchna/Achtryn",
		3, "Vannaka",
		4, "Chaeldar",
		5, "Duradel/Kuradal",
		6, "Nieve/Steve",
		7, "Krystilia",
		8, "Konar quo Maten",
		9, "Spria"
	);

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	@Inject
	private SlayerHistoryConfig config;

	@Inject
	private SlayerHistoryLocalStorage localStorage;

	private SlayerHistoryPanel panel;
	private NavigationButton navButton;

	private String taskName;
	private String taskMaster;
	private int taskInitialQuantity;
	private int taskQuantity;
	private int slayerPoints;

	private boolean loggingIn;

	@Override
	protected void startUp() throws Exception
	{
		loggingIn = true;

		panel = new SlayerHistoryPanel(this, config, clientThread, itemManager);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "slayer_history_icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Slayer History")
			.icon(icon)
			.panel(panel)
			.priority(7)
			.build();

		clientToolbar.addNavigation(navButton);

		if (client.getAccountHash() != -1)
		{
			loadPreviousTasks();
		}

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(() -> updateActiveTaskDetails(false));
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
	}

	@Provides
	SlayerHistoryConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlayerHistoryConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(SlayerHistoryConfig.CONFIG_GROUP))
		{
			return;
		}

		panel.updateConfig();
		if (configChanged.getKey().equals("logTimeFormat"))
		{
			SwingUtilities.invokeLater(panel::updateAllRecordBoxes);
		}
		else if (configChanged.getKey().equals("showSkippedTasks"))
		{
			loadPreviousTasks();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		switch (gameStateChanged.getGameState())
		{
			case HOPPING:
			case LOGGING_IN:
			case CONNECTION_LOST:
				loggingIn = true;
				break;
			case LOGGED_IN:
				updateFolderName();
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		loggingIn = false;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		int varpId = varbitChanged.getVarpId();
		int varbitId = varbitChanged.getVarbitId();
		if (varbitId == VarbitID.SLAYER_POINTS)
		{
			int newPoints = varbitChanged.getValue();
			int pointsDiff = slayerPoints - newPoints;
			if (pointsDiff == 30)
			{
				clientThread.invokeLater(() -> updateActiveTaskDetails(true));
			}
			slayerPoints = newPoints;
		}
		else if (varpId == VarPlayerID.SLAYER_COUNT
			|| varpId == VarPlayerID.SLAYER_COUNT_ORIGINAL
			|| varpId == VarPlayerID.SLAYER_TARGET
			|| varbitId == VarbitID.SLAYER_MASTER
		)
		{
			clientThread.invokeLater(() -> updateActiveTaskDetails(false));
		}
	}

	private void updateFolderName()
	{
		String folderName = String.valueOf(client.getAccountHash());
		RuneScapeProfileType profileType = RuneScapeProfileType.getCurrent(client);
		if (profileType != RuneScapeProfileType.STANDARD)
		{
			folderName += "-" + Text.titleCase(profileType);
		}

		if (localStorage.setAccountFolderName(folderName))
		{
			loadPreviousTasks();
		}
	}

	private synchronized void loadPreviousTasks()
	{
		panel.clearAllTasksView();
		ArrayList<SlayerHistoryRecord> taskHistory = localStorage.loadSlayerHistoryRecords();
		if (!taskHistory.isEmpty())
		{
			taskHistory.forEach(panel::addRecord);
		}
		else
		{
			panel.clearAllTasksView();
		}
	}

	public void addTask(boolean skipped)
	{
		if (taskMaster == null || taskName == null || taskInitialQuantity == -1)
		{
			log.warn("Tried to submit empty task");
		}
		else
		{
			SlayerHistoryRecord record = new SlayerHistoryRecord(
				Instant.now().toEpochMilli(),
				taskMaster,
				taskName,
				taskInitialQuantity,
				skipped
			);
			localStorage.addSlayerHistoryRecord(record);
			panel.addRecord(record);
		}

		taskMaster = null;
		taskName = null;
		taskInitialQuantity = -1;
	}

	private void updateActiveTaskDetails(boolean skipped)
	{
		int newTaskQuantity = client.getVarpValue(VarPlayerID.SLAYER_COUNT);
		if (newTaskQuantity > 0)
		{
			int taskId = client.getVarpValue(VarPlayerID.SLAYER_TARGET);

			int taskDBRow;
			if (taskId == 98 /* Bosses, from [proc,helper_slayer_current_assignment] */)
			{
				var bossRows = client.getDBRowsByValue(
					DBTableID.SlayerTaskSublist.ID,
					DBTableID.SlayerTaskSublist.COL_TASK_SUBTABLE_ID,
					0,
					client.getVarbitValue(VarbitID.SLAYER_TARGET_BOSSID));

				if (bossRows.isEmpty())
				{
					return;
				}
				taskDBRow = (Integer) client.getDBTableField(bossRows.get(0), DBTableID.SlayerTaskSublist.COL_TASK, 0)[0];
			}
			else
			{
				var taskRows = client.getDBRowsByValue(DBTableID.SlayerTask.ID, DBTableID.SlayerTask.COL_ID, 0, taskId);
				if (taskRows.isEmpty())
				{
					return;
				}
				taskDBRow = taskRows.get(0);
			}

			taskMaster = SLAYER_MASTERS.get(client.getVarbitValue(VarbitID.SLAYER_MASTER));
			taskInitialQuantity = client.getVarpValue(VarPlayerID.SLAYER_COUNT_ORIGINAL);
			taskName = (String) client.getDBTableField(taskDBRow, DBTableID.SlayerTask.COL_NAME_UPPERCASE, 0)[0];

			log.debug("{}, {}, {}/{}", taskName, taskMaster, newTaskQuantity, taskInitialQuantity);
		}
		else if (taskQuantity > 0 && !loggingIn)  // task was previously active, now it's not => task complete
		{
			addTask(skipped);
		}
		taskQuantity = newTaskQuantity;
	}
}