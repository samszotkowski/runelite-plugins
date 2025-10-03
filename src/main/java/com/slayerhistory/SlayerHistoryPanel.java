package com.slayerhistory;

import com.google.inject.Inject;
import com.slayerhistory.localstorage.SlayerHistoryRecord;
import java.awt.BorderLayout;
import java.awt.Color;
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
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class SlayerHistoryPanel extends PluginPanel
{
	private final ArrayList<SlayerHistoryRecordBox> recordBoxes = new ArrayList<SlayerHistoryRecordBox>();
	private final JPanel recordBoxPanel = new JPanel();
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
		setBorder(new EmptyBorder(6, 6, 6, 6));  // border that goes around record box cards
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JLabel titleLabel = new JLabel("Slayer History");
		titleLabel.setForeground(Color.WHITE);

		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBorder(new EmptyBorder(5, 3, 4, 0));
		titlePanel.add(titleLabel, BorderLayout.WEST);

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
	}

	public void addRecord(SlayerHistoryRecord record)
	{
		SwingUtilities.invokeLater(() -> {
			SlayerHistoryRecordBox recordBox = new SlayerHistoryRecordBox(this, record, clientThread, itemManager);
			recordBoxPanel.add(recordBox, 0);
			recordBoxes.add(recordBox);
		});
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
}