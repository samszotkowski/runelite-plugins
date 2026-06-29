package com.gatheringloot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.config.RuneScapeProfileType;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.PluginLootReceived;
import net.runelite.client.util.Text;
import net.runelite.http.api.loottracker.LootRecordType;

@Slf4j
@PluginDescriptor(
	name = "Gathering loot tracker",
	description = "Send gathering loot to built-in loot tracker",
	tags = {"drops", "skilling"}
)
public class GatheringLootPlugin extends Plugin
{
	private static final String WC_TREENAME_STANDARD = "Tree";
	private static final String WC_TREENAME_CHARCOAL = "Burnt tree";
	private static final String WC_TREENAME_SULLIUSCEP = "Sulliuscep";
	private static final String WC_TREENAME_INF_ROOT = "Infected root";
	private static final String WC_TREENAME_ACHEY = "Achey Tree";

	// To group trees together by drops, first check object ID then check name
	private static final Map<Integer, String> WC_TREEID_MAPPING = Map.of(
		ObjectID.FOSSIL_DEADTREE_LARGE1, WC_TREENAME_CHARCOAL,  // "Burnt tree" gives charcoal instead of logs
		ObjectID.FOSSIL_DEADTREE_SMALL1, WC_TREENAME_CHARCOAL,
		ObjectID.DEADTREE_BURNT, WC_TREENAME_CHARCOAL
	);

	private static final Map<String, String> WC_TREENAME_MAPPING = Map.of(
		"Dead tree", WC_TREENAME_STANDARD,
		"Dying tree", WC_TREENAME_STANDARD,
		"Evergreen tree", WC_TREENAME_STANDARD,
		"Jungle tree", WC_TREENAME_STANDARD,
		"Burnt tree", WC_TREENAME_STANDARD
	);

	private static final Map<String, Integer> WC_LOGMESSAGE_ITEMID = Map.ofEntries(
		Map.entry("You get some teak logs.", ItemID.TEAK_LOGS),
		Map.entry("You get some ironwood logs.", ItemID.IRONWOOD_LOGS),
		Map.entry("You get some redwood logs.", ItemID.REDWOOD_LOGS),
		Map.entry("You get some yew logs.", ItemID.YEW_LOGS),
		Map.entry("You get some magic logs.", ItemID.MAGIC_LOGS),
		Map.entry("You get some willow logs.", ItemID.WILLOW_LOGS),
		Map.entry("You get some rosewood logs.", ItemID.ROSEWOOD_LOGS),
		Map.entry("You get some oak logs.", ItemID.OAK_LOGS),
		Map.entry("You get some maple logs.", ItemID.MAPLE_LOGS),
		Map.entry("You get some camphor logs.", ItemID.CAMPHOR_LOGS),
		Map.entry("You get some blisterwood logs.", ItemID.BLISTERWOOD_LOGS),
		Map.entry("You get some mahogany logs.", ItemID.MAHOGANY_LOGS),
		Map.entry("You get some arctic pine. logs", ItemID.ARCTIC_PINE_LOG),
		Map.entry("You get some jatoba logs.", ItemID.JATOBA_LOGS),
		Map.entry("You get some scrapey tree. logs", ItemID.BREW_SCRAPEY_LOGS),
		Map.entry("You get some juniper logs.", ItemID.JUNIPER_LOGS),
		Map.entry("You get some bark.", ItemID.HOLLOW_BARK)
	);

	// special cases
	private static final String WC_LOGMESSAGE_STANDARD = "You get some logs.";
	private static final String WC_LOGMESSAGE_CHARCOAL = "You get some charcoal.";
	private static final int WC_SULLIUSCEP_MIN_XP = 127;
	private static final int WC_SULLIUSCEP_MAX_XP = 144;  // 127 * 1.1 (2h axe) * 1.025 (outfit)
	private static final List<List<Integer>> WC_SULLIUSCEP_LOCS = List.of(
		List.of(3678, 3806),
		List.of(3663, 3802),
		List.of(3663, 3781),
		List.of(3683, 3775),
		List.of(3683, 3758),
		List.of(3678, 3733)
	);

	// Woodcutting main drops
	private static final String WC_NO_LOG_MESSAGE = "You strike a clean cut without gathering any material.";
	private static final String WC_NEST_MESSAGE = "<col=ff0000>A bird's nest falls out of the tree.</col>";
	private static final String WC_ENT_SEED_MESSAGE = "An ent seed falls out of the tree!";
	private static final String WC_DEMONTEAR_MESSAGE = "You retrieve a demon tear from the root.";
	private static final String WC_TREK_VINE_MESSAGE = "You slice a vine from the tree.";

	// Woodcutting tertiary
	private static final String WC_NATURE_OFFERINGS_MESSAGE = "The nature offerings enabled you to chop an extra log.";
	private static final String WC_KANDARIN_HEADGEAR_MESSAGE = "Your Kandarin headgear provides you with an additional log.";
	private static final String WC_SECATEURS_MESSAGE = "Your secateurs attachment enabled you to gather extra leaves.";

	private static final Map<String, Integer> WC_LEAFMESSAGE_ITEMID = Map.of(
		"Some oak leaves fall to the ground and you place them into your Forestry kit.", ItemID.LEAVES_OAK,
		"Some maple leaves fall to the ground and you place them into your Forestry kit.", ItemID.LEAVES_MAPLE,
		"Some willow leaves fall to the ground and you place them into your Forestry kit.", ItemID.LEAVES_WILLOW,
		"Some yew leaves fall to the ground and you place them into your Forestry kit.", ItemID.LEAVES_YEW,
		"Some magic leaves fall to the ground and you place them into your Forestry kit.", ItemID.LEAVES_MAGIC
	);

	// Misc.
	private static final String MOON_KEY_LOOP_HALF_MESSAGE = "You find a key half!";

	@Inject
	private Client client;

	@Inject
	private EventBus eventBus;

	private String lastTreeClicked;
	private int lastTreeClickedId;
	private Map<Integer, Integer> pendingLoot;
	private boolean pendingInvGroundCollection;
	private int pendingLogId;
	private int pendingLogCount;
	private int pendingLeafId;
	private int pendingLeafCount;

	private long lastAccountHash;
	private RuneScapeProfileType lastWorldType;
	private boolean initializeWcExperience;
	private int lastWcExperience;

	@Override
	protected void startUp()
	{
		initializeWcExperience = true;
	}

	@Override
	protected void shutDown()
	{
		resetState();
	}

	private void resetState()
	{
		pendingLoot = null;
		pendingLogId = 0;
		pendingLogCount = 0;
		pendingLeafId = 0;
		pendingLeafCount = 0;
		pendingInvGroundCollection = false;
	}

	private static boolean isObjectOp(MenuAction menuAction)
	{
		final int id = menuAction.getId();
		return (id >= MenuAction.GAME_OBJECT_FIRST_OPTION.getId() && id <= MenuAction.GAME_OBJECT_FOURTH_OPTION.getId())
			|| id == MenuAction.GAME_OBJECT_FIFTH_OPTION.getId();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		String targetName = Text.removeTags(event.getMenuEntry().getTarget());
		int targetId = event.getMenuEntry().getIdentifier();

		if (isObjectOp(event.getMenuAction())
			&& (targetName.toLowerCase().endsWith("tree") || targetName.equals(WC_TREENAME_SULLIUSCEP) || targetName.equals(WC_TREENAME_INF_ROOT)))
		{
			if (WC_TREEID_MAPPING.containsKey(targetId))
			{
				lastTreeClicked = WC_TREEID_MAPPING.get(targetId);
			}
			else if (WC_TREENAME_MAPPING.containsKey(targetName))
			{
				lastTreeClicked = WC_TREENAME_MAPPING.get(targetName);
			}
			else
			{
				lastTreeClicked = targetName;
			}
			lastTreeClickedId = targetId;
		}
	}

	public void appendLoot(int itemId, int qty)
	{
		if (pendingLoot == null)
		{
			pendingLoot = new HashMap<>();
		}

		pendingLoot.put(itemId, pendingLoot.getOrDefault(itemId, 0) + qty);
	}

	public void setLoot(int itemId, int qty)
	{
		if (pendingLoot == null)
		{
			pendingLoot = new HashMap<>();
		}

		pendingLoot.put(itemId, qty);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		ChatMessageType messageType = event.getType();
		String message = event.getMessage();

		if (messageType != ChatMessageType.GAMEMESSAGE
			&& messageType != ChatMessageType.SPAM
			&& messageType != ChatMessageType.MESBOX)
		{
			return;
		}

		// logs are not always visible to client, so figure out how many via messages alone
		if (WC_LOGMESSAGE_ITEMID.containsKey(message))
		{
			pendingLogId = WC_LOGMESSAGE_ITEMID.get(message);
			pendingLogCount++;
		}
		else if (message.equals(WC_LOGMESSAGE_STANDARD))
		{
			if (lastTreeClicked.equals(WC_TREENAME_ACHEY))
			{
				pendingLogId = ItemID.ACHEY_TREE_LOGS;
				pendingLogCount++;
			}
			else if (lastTreeClicked.equals(WC_TREENAME_STANDARD) || lastTreeClicked.equals(WC_TREENAME_INF_ROOT))
			{
				pendingLogId = ItemID.LOGS;
				pendingLogCount++;
			}
			else
			{
				log.warn("Message: \"You get some logs.\" from unknown tree.");
			}
		}
		else if (WC_NO_LOG_MESSAGE.equals(message))
		{
			appendLoot(ItemID.BANK_FILLER, 1);
		}
		// extra logs: this message happens after LOGMESSAGE so no need to determine pendingLogId
		else if (WC_NATURE_OFFERINGS_MESSAGE.equals(message)
			|| WC_KANDARIN_HEADGEAR_MESSAGE.equals(message))
		{
			pendingLogCount++;
		}
		// leaves are never visible to client, so figure out how many via messages alone
		else if (WC_LEAFMESSAGE_ITEMID.containsKey(message))
		{
			pendingLeafId = WC_LEAFMESSAGE_ITEMID.get(message);
			pendingLeafCount++;
		}
		// extra leaves: this message happens after LEAFMESSAGE so no need to determine pendingLeafId
		else if (WC_SECATEURS_MESSAGE.equals(message))
		{
			pendingLeafCount++;
		}
		// This stuff is always visible to client so queue a ground/invy item search at the end of the tick
		else if (WC_NEST_MESSAGE.equals(message)
			|| WC_ENT_SEED_MESSAGE.equals(message)
			|| MOON_KEY_LOOP_HALF_MESSAGE.equals(message)
			|| WC_LOGMESSAGE_CHARCOAL.equals(message)
			|| WC_DEMONTEAR_MESSAGE.equals(message)
			|| WC_TREK_VINE_MESSAGE.equals(message))
		{
			pendingInvGroundCollection = true;
		}
	}

	private boolean withinDistance(List<List<Integer>> locXYs, int r)
	{
		WorldPoint location = client.getLocalPlayer().getWorldLocation();
		int x = location.getX();
		int y = location.getY();
		for (List<Integer> locXY : locXYs)
		{
			int locX = locXY.get(0);
			int locY = locXY.get(1);
			if (x >= locX - r && x <= locX + r && y >= locY - r && y <= locY + r)
			{
				return true;
			}
		}
		return false;
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (initializeWcExperience)
		{
			return;
		}

		if (event.getSkill().equals(Skill.WOODCUTTING))
		{
			int xpDiff = event.getXp() - lastWcExperience;
			if (xpDiff == 0)
			{
				return;
			}
			lastWcExperience = event.getXp();
			log.info("{}", xpDiff);

			if (lastTreeClicked.equals(WC_TREENAME_SULLIUSCEP)
				&& xpDiff >= WC_SULLIUSCEP_MIN_XP && xpDiff <= WC_SULLIUSCEP_MAX_XP
				&& withinDistance(WC_SULLIUSCEP_LOCS, 2))
			{
				pendingInvGroundCollection = true;
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState state = event.getGameState();
		if (state.equals(GameState.LOGGED_IN))
		{
			RuneScapeProfileType worldType = RuneScapeProfileType.getCurrent(client);
			if (client.getAccountHash() != lastAccountHash || lastWorldType != worldType)
			{
				lastAccountHash = client.getAccountHash();
				lastWorldType = worldType;
				resetState();
			}
		}
		else if (state.equals(GameState.LOGGING_IN) || state.equals(GameState.HOPPING))
		{
			initializeWcExperience = true;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (pendingInvGroundCollection)
		{
			// TODO: add all of (current inventory+ground items) - (old inventory+ground items) to pendingLoot
		}
		if (pendingLogId != 0)
		{
			// overwrite log+leaf ground/invy items with direct message observations
			setLoot(pendingLogId, pendingLogCount);
		}
		if (pendingLeafId != 0)
		{
			setLoot(pendingLeafId, pendingLeafCount);
		}

		if (pendingLoot != null)
		{
			List<ItemStack> loot = pendingLoot.entrySet().stream()
				.map(e -> new ItemStack(e.getKey(), e.getValue()))
				.collect(Collectors.toList());

			eventBus.post(PluginLootReceived.builder()
				.source(this)
				.name(lastTreeClicked)
				.type(LootRecordType.EVENT)
				.items(loot)
				.metadata(lastTreeClickedId)
				.build());
		}

		if (initializeWcExperience)
		{
			lastWcExperience = client.getSkillExperience(Skill.WOODCUTTING);
			initializeWcExperience = false;
		}
		resetState();
	}
}