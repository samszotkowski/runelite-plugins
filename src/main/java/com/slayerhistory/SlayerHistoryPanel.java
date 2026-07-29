package com.slayerhistory;

import com.google.inject.Inject;
import com.slayerhistory.localstorage.SlayerHistoryRecord;
import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class SlayerHistoryPanel extends PluginPanel
{
	private final ArrayList<SlayerHistoryRecordBox> recordBoxes = new ArrayList<SlayerHistoryRecordBox>();
	private final JPanel recordBoxPanel = new JPanel();
	private final JLabel tasksLoggedLabel = new JLabel("Tasks logged: 0");
	private final SlayerHistoryConfig config;
	public SimpleDateFormat shortTimeFormat = new SimpleDateFormat("MMM dd, h:mm a");
	SlayerHistoryPlugin plugin;
	private final ClientThread clientThread;
	private final ItemManager itemManager;

	@Inject
	SlayerHistoryPanel(SlayerHistoryPlugin plugin, SlayerHistoryConfig config, ClientThread clientThread, ItemManager itemManager)
	{
		super();
		this.plugin = plugin;
		this.config = config;
		this.clientThread = clientThread;
		this.itemManager = itemManager;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(6, 6, 6, 6));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBorder(new EmptyBorder(5, 8, 5, 8));
		titlePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JLabel titleLabel = new JLabel("Slayer History");

		tasksLoggedLabel.setBorder(new EmptyBorder(2, 0, 0, 0));
		tasksLoggedLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		tasksLoggedLabel.setFont(FontManager.getRunescapeSmallFont());

		titlePanel.add(titleLabel, BorderLayout.WEST);
		titlePanel.add(tasksLoggedLabel, BorderLayout.EAST);

		recordBoxPanel.setLayout(new BoxLayout(recordBoxPanel, BoxLayout.Y_AXIS));

		add(titlePanel);
		add(recordBoxPanel);

		updateConfig();
	}

	public void clearAllTasksView()
	{
		recordBoxPanel.removeAll();
		recordBoxes.clear();
		recordBoxPanel.repaint();
		updateTasksLoggedLabel();
	}

	public void removeCurrent()
	{
		recordBoxPanel.remove(0);
		recordBoxes.remove(0);
	}

	public void addRecord(SlayerHistoryRecord record, boolean current)
	{
		if (config.showSkippedTasks() || !record.isSkipped())
		{
			SwingUtilities.invokeLater(() -> {
				SlayerHistoryRecordBox recordBox = new SlayerHistoryRecordBox(this, record, clientThread, itemManager);
				recordBoxPanel.add(recordBox, 0);
				recordBoxes.add(recordBox);
				if (!current)
				{
					updateTasksLoggedLabel();
				}
			});
		}
	}

	public void updateConfig()
	{
		if (config.timeFormatMode() == TimeFormat.TIME_12H)
		{
			shortTimeFormat = new SimpleDateFormat("MMM dd, h:mm a");
		}
		else
		{
			shortTimeFormat = new SimpleDateFormat("MMM dd, HH:mm");
		}
	}

	public void updateAllRecordBoxes()
	{
		recordBoxes.forEach(SlayerHistoryRecordBox::update);
	}

	public void updateTasksLoggedLabel()
	{
		tasksLoggedLabel.setText(String.format("Tasks logged: %,d", recordBoxes.size()));
	}
}