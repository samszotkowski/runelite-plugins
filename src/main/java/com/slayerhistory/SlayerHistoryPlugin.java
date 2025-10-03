package com.slayerhistory;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.slayerhistory.localstorage.SlayerHistoryLocalStorage;
import com.slayerhistory.localstorage.SlayerHistoryRecord;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
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

	@Setter
	private SlayerHistoryPanel panel;
	private NavigationButton navButton;

	@Override
	protected void startUp() throws Exception
	{
		panel = new SlayerHistoryPanel(this, config, clientThread, itemManager);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "slayer_history_icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Slayer History")
			.icon(icon)
			.panel(panel)
			.priority(7)
			.build();

		clientToolbar.addNavigation(navButton);

		if (!this.isLoggedIn())
		{
			return;
		}
		loadPreviousTasks();
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

	private boolean isLoggedIn()
	{
		return client.getAccountHash() != -1;
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

	public void addTask(final SlayerHistoryRecord record)
	{
		localStorage.addSlayerHistoryRecord(record);
		panel.addRecord(record);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		GameState state = gameStateChanged.getGameState();
		if (state == GameState.LOGGED_IN)
		{
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

		if (localStorage.setAccountFolderName(folderName))
		{
			loadPreviousTasks();
		}
	}
}