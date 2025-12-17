package com.slayerhistory;

import com.google.inject.Inject;
import com.slayerhistory.localstorage.SlayerHistoryRecord;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Constants;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

public class SlayerHistoryRecordBox extends JPanel
{
	private static final String DWARF_IMAGE_PATH = "null.png";
	private static final String DWARF_TASK_NAME = "dwarves";

	private final SlayerHistoryPanel panel;
	private final SlayerHistoryRecord record;
	private final ItemManager itemManager;
	private final ClientThread clientThread;

	private final JLabel taskNameLabel = new JLabel();
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
		imageBox.setBorder(new EmptyBorder(0, 8, 0, 0));
		imageBox.add(taskIconLabel);

		// words on the right of the card
		JPanel taskInfo = new JPanel();
		taskInfo.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		taskInfo.setLayout(new BoxLayout(taskInfo, BoxLayout.Y_AXIS));
		taskInfo.setBorder(new EmptyBorder(5, 0, 5, 0));

		JPanel taskMasterCompletionPanel = new JPanel();
		taskMasterCompletionPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		taskMasterCompletionPanel.setLayout(new BoxLayout(taskMasterCompletionPanel, BoxLayout.Y_AXIS));
		taskMasterCompletionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		taskMasterCompletionPanel.add(taskMasterLabel);
		taskMasterCompletionPanel.add(taskCompletionTimeLabel);

		taskInfo.add(taskNameLabel);
		taskInfo.add(taskMasterCompletionPanel);

		this.add(imageBox, BorderLayout.WEST);
		this.add(taskInfo);

		taskNameLabel.setFont(FontManager.getRunescapeBoldFont());
		taskMasterLabel.setFont(FontManager.getRunescapeSmallFont());
		taskCompletionTimeLabel.setFont(FontManager.getRunescapeSmallFont());

		taskMasterLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		taskCompletionTimeLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);

		taskIconLabel.setMinimumSize(new Dimension(Constants.ITEM_SPRITE_WIDTH, Constants.ITEM_SPRITE_HEIGHT));

		update();
	}

	void update()
	{
		if (record.skipped)
		{
			taskNameLabel.setForeground(new Color(183, 38, 21));
		}
		taskNameLabel.setText(record.taskName);
		taskMasterLabel.setText(record.taskMaster);
		taskCompletionTimeLabel.setText(panel.shortTimeFormat.format(record.taskCompletionTime));

		clientThread.invokeLater(() ->
		{
			BufferedImage taskImage = itemManager.getImage(TaskIcon.getItemSpriteId(record.taskName), record.taskQuantity, true);

			// custom image just for dwarves. combining with empty item image bc that handles the quantity superscript
			if (record.taskName.equalsIgnoreCase(DWARF_TASK_NAME))
			{
				BufferedImage dwarfImage = ImageUtil.loadImageResource(getClass(), DWARF_IMAGE_PATH);
				BufferedImage combined = new BufferedImage(taskImage.getWidth(), taskImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

				int dwarfOffsetX = 2;
				int dwarfOffsetY = (taskImage.getHeight() - dwarfImage.getHeight()) / 2;

				Graphics g = combined.createGraphics();
				g.drawImage(dwarfImage, dwarfOffsetX, dwarfOffsetY, null);
				g.drawImage(taskImage, 0, 0, null);
				g.dispose();

				taskIconLabel.setIcon(new ImageIcon(combined));
			}
			else if (record.skipped)
			{
				BufferedImage nothingImage = itemManager.getImage(ItemID.BANK_FILLER, record.taskQuantity, true);
				taskIconLabel.setIcon(new ImageIcon(nothingImage));
			}
			else
			{
				taskIconLabel.setIcon(new ImageIcon(taskImage));
			}
		});
	}
}