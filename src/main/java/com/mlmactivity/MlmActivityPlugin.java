package com.mlmactivity;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@Slf4j
@PluginDescriptor(
	name = "MLM activity timer",
	description = "Adds a timer in Motherlode Mine showing how long you've been mining a vein.",
	tags = {"timer", "mlm", "motherlode", "mine", "paydirt", "pay-dirt", "activity", "idle", "afk"}
)
public class MlmActivityPlugin extends Plugin
{
	private static final Set<Integer> MOTHERLODE_MAP_REGIONS = ImmutableSet.of(14679, 14680, 14681, 14935, 14936, 14937, 15191, 15192, 15193);
	private static final Set<Integer> MINE_SPOTS = ImmutableSet.of(
		ObjectID.MOTHERLODE_DEPLETED_SINGLE,
		ObjectID.MOTHERLODE_DEPLETED_LEFT,
		ObjectID.MOTHERLODE_DEPLETED_MIDDLE,
		ObjectID.MOTHERLODE_DEPLETED_RIGHT);
	private static final Set<Integer> ORES = ImmutableSet.of(
		ItemID.PAYDIRT,
		ItemID.UNCUT_SAPPHIRE,
		ItemID.UNCUT_EMERALD,
		ItemID.UNCUT_RUBY,
		ItemID.UNCUT_DIAMOND
	);

	static final int MIN_DURATION_UPSTAIRS = 60;
	static final int MIN_DURATION_DOWNSTAIRS = 38;

	private static final int UPPER_FLOOR_HEIGHT = -490;
	private static final int ANIM_TIMEOUT = 3;
	private static final int TICK_TOCK_SFX_ID = 3120;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private MlmActivityConfig config;

	private MlmActivityCounter counter;

	@Getter
	private int timer;

	private long oresInInventory;
	private boolean isMining;
	private boolean timerIsRunning;
	private int recentlyMined;
	private boolean inMlm;

	@Provides
	MlmActivityConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MlmActivityConfig.class);
	}

	@Override
	protected void startUp()
	{
		inMlm = checkInMlm();
		if (client.getGameState() == GameState.LOGGED_IN && inMlm)
		{
			addCounter();
			clientThread.invokeLater(() -> countOresInInventory());
		}
	}

	@Override
	protected void shutDown()
	{
		stopMining();
		oresInInventory = 0;
		removeCounter();
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event)
	{
		if (!inMlm)
		{
			return;
		}

		int objectId = event.getWallObject().getId();
		if (!MINE_SPOTS.contains(objectId))
		{
			return;
		}

		Player local = client.getLocalPlayer();
		if (local == null)
		{
			return;
		}

		LocalPoint playerLocation = local.getLocalLocation();
		int playerX = playerLocation.getX() / 128;
		int playerY = playerLocation.getY() / 128;
		int interactableX;
		int interactableY;
		int playerOrientation = local.getOrientation() / 512;
		switch (playerOrientation)
		{
			case 0:
				interactableX = playerX;
				interactableY = playerY - 1;
				break;
			case 1:
				interactableX = playerX - 1;
				interactableY = playerY;
				break;
			case 2:
				interactableX = playerX;
				interactableY = playerY + 1;
				break;
			case 3:
				interactableX = playerX + 1;
				interactableY = playerY;
				break;
			default:
				interactableX = -1;
				interactableY = -1;
		}

		LocalPoint wallLocation = event.getWallObject().getLocalLocation();
		int wallX = wallLocation.getX() / 128;
		int wallY = wallLocation.getY() / 128;
		if (wallX == interactableX && wallY == interactableY)
		{
			stopMining();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			inMlm = checkInMlm();
			if (inMlm)
			{
				addCounter();
				countOresInInventory();
				return;
			}
		}
		removeCounter();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (!inMlm || event.getContainerId() != InventoryID.INV)
		{
			return;
		}

		ItemContainer inv = event.getItemContainer();
		if (inv.count() == 28)
		{
			stopMining();
			return;
		}

		long prevOresInInventory = oresInInventory;
		countOresInInventory(inv);
		if (isMining && oresInInventory > prevOresInInventory)
		{
			timerIsRunning = true;
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!inMlm)
		{
			return;
		}

		Player local = client.getLocalPlayer();

		if (event.getActor() != local)
		{
			return;
		}

		if (MiningAnimation.MINING_ANIMATIONS.contains(local.getAnimation()))
		{
			startMining();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!inMlm)
		{
			return;
		}

		if (MiningAnimation.MINING_ANIMATIONS.contains(client.getLocalPlayer().getAnimation()))
		{
			startMining();
		}
		else
		{
			recentlyMined--;
			if (recentlyMined == 0)
			{
				stopMining();
			}
		}

		if (isMining && timerIsRunning)
		{
			timer++;
			counter.setCount(timer);

			if (config.soundOn())
			{
				if ((isUpstairs() && timer == MIN_DURATION_UPSTAIRS) || (!isUpstairs() && timer == MIN_DURATION_DOWNSTAIRS))
				{
					playSounds();
				}
			}
		}
	}

	private void playSounds()
	{
		LocalPoint localPoint = client.getLocalPlayer().getLocalLocation();
		int x = localPoint.getX() / 128;
		int y = localPoint.getY() / 128;
		client.playSoundEffect(TICK_TOCK_SFX_ID, x, y, 1, 0);
		client.playSoundEffect(TICK_TOCK_SFX_ID, x, y, 1, 50);
		client.playSoundEffect(TICK_TOCK_SFX_ID, x, y, 1, 100);
	}

	private void addCounter()
	{
		BufferedImage paydirtImg = itemManager.getImage(ItemID.PAYDIRT);
		counter = new MlmActivityCounter(paydirtImg, this, 0);
		infoBoxManager.addInfoBox(counter);
	}

	private void removeCounter()
	{
		infoBoxManager.removeIf(entry -> entry instanceof MlmActivityCounter);
	}

	private void stopMining()
	{
		isMining = false;
		recentlyMined = 0;
		timer = 0;
		timerIsRunning = false;
		counter.setCount(0);
	}

	private void startMining()
	{
		recentlyMined = ANIM_TIMEOUT;
		isMining = true;
	}

	private void countOresInInventory()
	{
		ItemContainer inv = client.getItemContainer(InventoryID.INV);
		if (inv != null)
		{
			countOresInInventory(inv);
		}
	}

	private void countOresInInventory(ItemContainer inv)
	{
		oresInInventory = Arrays.stream(inv.getItems())
			.filter(item -> ORES.contains(item.getId()))
			.count();
	}

	private boolean checkInMlm()
	{
		GameState gameState = client.getGameState();
		if (gameState != GameState.LOGGED_IN
			&& gameState != GameState.LOADING)
		{
			return false;
		}

		int[] currentMapRegions = client.getMapRegions();

		for (int region : currentMapRegions)
		{
			if (!MOTHERLODE_MAP_REGIONS.contains(region))
			{
				return false;
			}
		}

		return true;
	}

	boolean isUpstairs()
	{
		return isUpstairs(client.getLocalPlayer().getLocalLocation());
	}

	boolean isUpstairs(LocalPoint localPoint)
	{
		return Perspective.getTileHeight(client, localPoint, 0) < UPPER_FLOOR_HEIGHT;
	}
}
