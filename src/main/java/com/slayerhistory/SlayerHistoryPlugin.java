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
	private static final Map<Integer, String> SLAYER_MASTERS = Map.ofEntries(
		Map.entry(0, "None"),
		Map.entry(1, "Turael/Aya"),
		Map.entry(2, "Mazchna/Achtryn"),
		Map.entry(3, "Vannaka"),
		Map.entry(4, "Chaeldar"),
		Map.entry(5, "Duradel/Kuradal"),
		Map.entry(6, "Nieve/Steve"),
		Map.entry(7, "Krystilia"),
		Map.entry(8, "Konar quo Maten"),
		Map.entry(9, "Spria"),
		Map.entry(10, "Mortimer")
	);

	// VarbitID.SLAYER_MODIFIER_ID is index of https://abextm.github.io/cache2/#/viewer/dbtable/131
	private static final int MORTIFIER_QUANTITY = 2;

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

	// TODO: clean up all this garbage and just use currentRecord for as much as possible
	private int oldStreak;
	private int oldWildyStreak;
	private int taskInitialQuantity;
	private boolean loggingIn;
	private boolean hasCurrent;
	private String taskMaster;

	private SlayerHistoryRecord currentRecord;

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
			updateFolderName();

			clientThread.invokeLater(() -> {
				oldStreak = client.getVarbitValue(VarbitID.SLAYER_TASKS_COMPLETED);
				oldWildyStreak = client.getVarbitValue(VarbitID.SLAYER_WILDERNESS_TASKS_COMPLETED);
				taskInitialQuantity = client.getVarpValue(VarPlayerID.SLAYER_COUNT_ORIGINAL);

				taskMaster = SLAYER_MASTERS.get(client.getVarbitValue(VarbitID.SLAYER_MASTER));
				if (taskMaster.equals("Mortimer") && client.getVarbitValue(VarbitID.SLAYER_MODIFIER_ID) == MORTIFIER_QUANTITY)
				{
					boolean isNegative = client.getVarbitValue(VarbitID.SLAYER_MODIFIER_NEGATIVE) == 1;
					int modifierValue = client.getVarbitValue(VarbitID.SLAYER_MODIFIER_VALUE);
					taskInitialQuantity += isNegative ? -modifierValue : modifierValue;
				}

				addCurrentTask();
			});
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
		if (hasCurrent) {
			panel.removeCurrent();
			oldStreak = -1;
			oldWildyStreak = -1;
			taskInitialQuantity = -1;
			loggingIn = true;
			hasCurrent = false;
		}
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
			clientThread.invokeLater(this::addCurrentTask);
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
				if (loggingIn)
				{
					updateFolderName();
					clientThread.invokeLater(() -> {
						oldStreak = client.getVarbitValue(VarbitID.SLAYER_TASKS_COMPLETED);
						oldWildyStreak = client.getVarbitValue(VarbitID.SLAYER_WILDERNESS_TASKS_COMPLETED);
					});
				}
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
		if (!loggingIn && varpId == VarPlayerID.SLAYER_COUNT)
		{
			clientThread.invokeLater(() -> {
				int newStreak = client.getVarbitValue(VarbitID.SLAYER_TASKS_COMPLETED);
				int newWildyStreak = client.getVarbitValue(VarbitID.SLAYER_WILDERNESS_TASKS_COMPLETED);
				log.debug("standard streak: {}->{}, wildy streak: {}->{}", oldStreak, newStreak, oldWildyStreak, newWildyStreak);

				if (varbitChanged.getValue() == 0)
				{
					addTaskFromCurrent(newStreak == oldStreak && newWildyStreak == oldWildyStreak);
				}

				oldStreak = newStreak;
				oldWildyStreak = newWildyStreak;
			});
		}
		// Turael skipping: SLAYER_MASTER changes before SLAYER_COUNT
		else if (varbitChanged.getVarbitId() == VarbitID.SLAYER_MASTER)
		{
			String newMaster = SLAYER_MASTERS.get(varbitChanged.getValue());
			if (!loggingIn && !taskMaster.equals("None") && newMaster.equals("Turael/Aya"))
			{
				clientThread.invokeLater(() -> addTaskFromCurrent(true));
			}
			taskMaster = newMaster;
		}
		// Cancel task via interface: SLAYER_COUNT_ORIGINAL does not change
		// Complete task normally:    SLAYER_COUNT_ORIGINAL does not change
		// Cancel task via dialogue:  SLAYER_COUNT_ORIGINAL set to 0... so we need to keep it in memory
		else if (varpId == VarPlayerID.SLAYER_COUNT_ORIGINAL && varbitChanged.getValue() != 0)
		{
			taskInitialQuantity = varbitChanged.getValue();
			clientThread.invokeLater(() -> {
				String taskMaster = SLAYER_MASTERS.get(client.getVarbitValue(VarbitID.SLAYER_MASTER));
				if (taskMaster.equals("Mortimer") && client.getVarbitValue(VarbitID.SLAYER_MODIFIER_ID) == MORTIFIER_QUANTITY)
				{
					boolean isNegative = client.getVarbitValue(VarbitID.SLAYER_MODIFIER_NEGATIVE) == 1;
					int modifierValue = client.getVarbitValue(VarbitID.SLAYER_MODIFIER_VALUE);
					taskInitialQuantity += isNegative ? -modifierValue : modifierValue;
				}

				if (!hasCurrent)
				{
					addCurrentTask();
				}
			});
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

		localStorage.setAccountFolderName(folderName);
		loadPreviousTasks();
	}

	private synchronized void loadPreviousTasks()
	{
		panel.clearAllTasksView();
		hasCurrent = false;
		ArrayList<SlayerHistoryRecord> taskHistory = localStorage.loadSlayerHistoryRecords();
		if (!taskHistory.isEmpty())
		{
			for (SlayerHistoryRecord r : taskHistory)
			{
				panel.addRecord(r, false);
			}
		}
	}

	private String getTaskName(int taskId)
	{
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
				return null;
			}
			taskDBRow = (Integer) client.getDBTableField(bossRows.get(0), DBTableID.SlayerTaskSublist.COL_TASK, 0)[0];
		}
		else
		{
			var taskRows = client.getDBRowsByValue(DBTableID.SlayerTask.ID, DBTableID.SlayerTask.COL_ID, 0, taskId);
			if (taskRows.isEmpty())
			{
				return null;
			}
			taskDBRow = taskRows.get(0);
		}

		return (String) client.getDBTableField(taskDBRow, DBTableID.SlayerTask.COL_NAME_UPPERCASE, 0)[0];
	}

	private void addCurrentTask()
	{
		int taskCount = client.getVarpValue(VarPlayerID.SLAYER_COUNT);
		if (taskCount < 1)  // -1 when you have boss task without having qty yet, 0 when no task
		{
			return;
		}

		int taskId = client.getVarpValue(VarPlayerID.SLAYER_TARGET);

		String taskName = getTaskName(taskId);
		String taskMaster = SLAYER_MASTERS.get(client.getVarbitValue(VarbitID.SLAYER_MASTER));
		if (taskName == null)
		{
			log.warn("Unable to find task name");
			return;
		}
		log.debug("{}, {}, {}", taskName, taskMaster, taskInitialQuantity);

		currentRecord = new SlayerHistoryRecord(
			-1,
			taskMaster,
			taskName,
			taskInitialQuantity,
			false,
			-1
		);
		panel.addRecord(currentRecord, true);
		hasCurrent = true;
	}

	private void addTaskFromCurrent(boolean skipped)
	{
		int streak;
		if (currentRecord.getTaskMaster().equals("Krystilia"))
		{
			streak = client.getVarbitValue(VarbitID.SLAYER_WILDERNESS_TASKS_COMPLETED);
		}
		else
		{
			streak = client.getVarbitValue(VarbitID.SLAYER_TASKS_COMPLETED);
		}

		SlayerHistoryRecord record = new SlayerHistoryRecord(
			Instant.now().toEpochMilli(),
			currentRecord.getTaskMaster(),
			currentRecord.getTaskName(),
			currentRecord.getTaskQuantity(),
			skipped,
			streak
		);
		localStorage.addSlayerHistoryRecord(record);
		panel.removeCurrent();
		hasCurrent = false;
		panel.addRecord(record, false);
	}
}