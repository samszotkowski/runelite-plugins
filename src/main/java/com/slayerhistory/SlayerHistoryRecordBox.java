package com.slayerhistory;

import com.google.inject.Inject;
import com.slayerhistory.localstorage.SlayerHistoryRecord;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Constants;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.AsyncBufferedImage;

public class SlayerHistoryRecordBox extends JPanel
{
	private final SlayerHistoryPanel panel;
	private final SlayerHistoryRecord record;
	private final ItemManager itemManager;
	private final ClientThread clientThread;

	private final JLabel taskNameLabel = new JLabel();
	private final JLabel taskQtyLabel = new JLabel();
	private final JLabel taskMasterLabel = new JLabel();
	private final JLabel taskCompletionTimeLabel = new JLabel();
	private final JLabel taskIconLabel = new JLabel();

	@Inject
	SlayerHistoryRecordBox(SlayerHistoryPanel panel, SlayerHistoryRecord record, ClientThread clientThread, ItemManager itemManager)
	{
		this.itemManager = itemManager;
		this.clientThread = clientThread;
		this.panel = panel;
		this.record = record;
		buildSlayerHistoryPanel();
	}

	private void buildSlayerHistoryPanel()
	{
		this.setBorder(new EmptyBorder(6, 0, 0, 0)); //spacing between cards
		this.setBackground(ColorScheme.DARK_GRAY_COLOR);
		this.setLayout(new BorderLayout());

		// image on the left of the card
		JPanel imageBox = new JPanel();
		imageBox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		imageBox.setLayout(new BorderLayout());
		imageBox.setBorder(new EmptyBorder(0, 8, 0, 2));
		imageBox.add(taskIconLabel);

		// words on the right of the card
		JPanel taskInfo = new JPanel();
		taskInfo.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		taskInfo.setLayout(new BoxLayout(taskInfo, BoxLayout.Y_AXIS));
		taskInfo.setBorder(new EmptyBorder(5, 0, 5, 0));

		JPanel taskNameQtyPanel = new JPanel();
		taskNameQtyPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		taskNameQtyPanel.setLayout(new BoxLayout(taskNameQtyPanel, BoxLayout.X_AXIS));
		taskNameQtyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		taskNameQtyPanel.add(taskNameLabel);
		taskNameQtyPanel.add(taskQtyLabel);

		JPanel taskMasterCompletionPanel = new JPanel();
		taskMasterCompletionPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		taskMasterCompletionPanel.setLayout(new BoxLayout(taskMasterCompletionPanel, BoxLayout.Y_AXIS));
		taskMasterCompletionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		taskMasterCompletionPanel.add(taskMasterLabel);
		taskMasterCompletionPanel.add(taskCompletionTimeLabel);

		taskInfo.add(taskNameQtyPanel);
		taskInfo.add(taskMasterCompletionPanel);

		this.add(imageBox, BorderLayout.WEST);
		this.add(taskInfo);

		taskNameLabel.setFont(FontManager.getRunescapeBoldFont());
		taskQtyLabel.setFont(FontManager.getRunescapeSmallFont());
		taskMasterLabel.setFont(FontManager.getRunescapeSmallFont());
		taskCompletionTimeLabel.setFont(FontManager.getRunescapeSmallFont());

		taskQtyLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		taskMasterLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		taskCompletionTimeLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);

		taskIconLabel.setMinimumSize(new Dimension(Constants.ITEM_SPRITE_WIDTH, Constants.ITEM_SPRITE_HEIGHT));

		update();
	}

	void update()
	{
		taskNameLabel.setText(record.taskName);
		taskQtyLabel.setText(String.format(" x %d", record.taskQuantity));
		taskMasterLabel.setText(record.taskMaster);
		taskCompletionTimeLabel.setText(panel.shortTimeFormat.format(record.taskCompletionTime));

		clientThread.invokeLater(() ->
		{
			AsyncBufferedImage taskImage = itemManager.getImage(Task.getItemSpriteId(record.taskName));
			taskImage.addTo(taskIconLabel);
		});
	}
}