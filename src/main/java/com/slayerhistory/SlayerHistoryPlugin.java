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
	private static final int TICKS_TO_WAIT = 3;  // avoid checking varbit stuff immediately upon login

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
			clientThread.invoke(this::updateActiveTaskDetails);
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

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
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
	}

	@Provides
	SlayerHistoryConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlayerHistoryConfig.class);
	}

	public void addTask()
	{
		SlayerHistoryRecord record = new SlayerHistoryRecord(
			Instant.now().toEpochMilli(),
			taskMaster,
			taskName,
			taskInitialQuantity
		);
		localStorage.addSlayerHistoryRecord(record);
		panel.addRecord(record);

		taskMaster = null;
		taskName = null;
		taskInitialQuantity = -1;
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

	private void updateFolderName()
	{
		String folderName = String.valueOf(client.getAccountHash());
		RuneScapeProfileType profileType = RuneScapeProfileType.getCurrent(client);
		if (profileType != RuneScapeProfileType.STANDARD)
		{
			folderName += "-" + Text.titleCase(profileType);
		}

		if (localStorage.setAccountFolderName(folderName) || loggingIn)
		{
			loadPreviousTasks();
		}
	}

	private void updateActiveTaskDetails()
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

			log.info("{}, {}, {}/{}", taskName, taskMaster, taskQuantity, taskInitialQuantity);
		}
		else if (taskQuantity > 0 && !loggingIn)  // task was previously active, now it's not => task complete
		{
			addTask();
		}
		taskQuantity = newTaskQuantity;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		int varpId = varbitChanged.getVarpId();
		int varbitId = varbitChanged.getVarbitId();
		if (varpId == VarPlayerID.SLAYER_COUNT
			|| varpId == VarPlayerID.SLAYER_COUNT_ORIGINAL
			|| varpId == VarPlayerID.SLAYER_TARGET
			|| varbitId == VarbitID.SLAYER_MASTER
		)
		{
			clientThread.invokeLater(this::updateActiveTaskDetails);
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		loggingIn = false;
	}
}