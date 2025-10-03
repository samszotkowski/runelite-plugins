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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;

@Getter
enum Task
{
	ABERRANT_SPECTRES("Aberrant spectres", ItemID.SLAYERGUIDE_ABERRANTSPECTER),
	ABYSSAL_DEMONS("Abyssal demons", ItemID.SLAYERGUIDE_ABYSSALDEMON),
	ABYSSAL_SIRE("The Abyssal Sire", ItemID.ABYSSALSIRE_PET),
	ALCHEMICAL_HYDRA("The Alchemical Hydra", ItemID.HYDRAPET),
	ANKOU("Ankou", ItemID.ANKOU_HEAD),
	ARAXXOR("Araxxor", ItemID.ARAXXORPET),
	ARAXYTES("Araxytes", ItemID.POH_ARAXYTE_HEAD),
	AVIANSIES("Aviansies", ItemID.ARCEUUS_CORPSE_AVIANSIE_INITIAL),
	BANDITS("Bandits", ItemID.PICKPOCKET_GUIDE_DESERT_BANDIT),
	BANSHEES("Banshees", ItemID.SLAYERGUIDE_BANSHEE),
	BARROWS_BROTHERS("Barrows Brothers", ItemID.BARROWS_KARIL_HEAD),
	BASILISKS("Basilisks", ItemID.SLAYERGUIDE_BASILISK),
	BATS("Bats", ItemID.RAIDS_BAT2_COOKED),
	BEARS("Bears", ItemID.ARCEUUS_CORPSE_BEAR_INITIAL),
	BIRDS("Birds", ItemID.FEATHER),
	BLACK_DEMONS("Black demons", ItemID.BLACK_DEMON_MASK),
	BLACK_DRAGONS("Black dragons", ItemID.DRAGONMASK_BLACK),
	BLACK_KNIGHTS("Black Knights", ItemID.BLACK_FULL_HELM),
	BLOODVELD("Bloodveld", ItemID.SLAYERGUIDE_BLOODVELD),
	BLUE_DRAGONS("Blue dragons", ItemID.DRAGONMASK_BLUE),
	BRINE_RATS("Brine rats", ItemID.OLAF2_BRINE_RAT_INV),
	CALLISTO("Callisto", ItemID.CALLISTO_PET),
	CATABLEPON("Catablepon", ItemID.SOS_HALF_SKULL2),
	CAVE_BUGS("Cave bugs", ItemID.SWAMP_CAVE_BUG),
	CAVE_CRAWLERS("Cave crawlers", ItemID.SLAYERGUIDE_CAVECRAWLER),
	CAVE_HORRORS("Cave horrors", ItemID.SLAYERGUIDE_HARMLESS_CAVE_HORROR),
	CAVE_KRAKEN("Cave kraken", ItemID.CERT_EADGAR_FADE_TO_BLACK_INV),
	CAVE_SLIMES("Cave slimes", ItemID.SWAMP_CAVE_SLIME),
	CERBERUS("Cerberus", ItemID.HELL_PET),
	CHAOS_DRUIDS("Chaos druids", ItemID.ELDERCHAOS_HOOD),
	CHAOS_ELEMENTAL("The Chaos Elemental", ItemID.CHAOSELEPET),
	CHAOS_FANATIC("The Chaos Fanatic", ItemID.STAFF_OF_ZAROS),
	COCKATRICE("Cockatrice", ItemID.SLAYERGUIDE_COCKATRICE),
	COWS("Cows", ItemID.COW_MASK),
	CRABS("Crabs", ItemID.HUNDRED_PIRATE_CRAB_SHELL_GAUNTLET),
	CRAWLING_HANDS("Crawling hands", ItemID.SLAYERGUIDE_CRAWLINGHAND),
	CRAZY_ARCHAEOLOGIST("Crazy Archaeologists", ItemID.FEDORA),
	CROCODILES("Crocodiles", ItemID.GREEN_SALAMANDER),
	CUSTODIAN_STALKERS("Custodian Stalkers", ItemID.SLAYERGUIDE_CUSTODIAN_STALKER_MATURE),
	DAGANNOTH("Dagannoth", ItemID.POH_DAGGANOTH),
	DAGANNOTH_KINGS("Dagannoth Kings", ItemID.PRIMEPET),
	DARK_BEASTS("Dark beasts", ItemID.SLAYERGUIDE_DARK_BEAST),
	DARK_WARRIORS("Dark warriors", ItemID.BLACK_MED_HELM),
	DERANGED_ARCHAEOLOGIST("Deranged Archaeologist", ItemID.FOSSIL_SWAMP_DIARY),
	DOGS("Dogs", ItemID.POH_GUARD_DOG),
	DRAKES("Drakes", ItemID.SLAYERGUIDE_DRAKE),
	DUKE_SUCELLUS("Duke Sucellus", ItemID.DUKESUCELLUSPET),
	DUST_DEVILS("Dust devils", ItemID.SLAYERGUIDE_DUSTDEVIL),
	DWARVES("Dwarves", ItemID.GRIM_WEAR_HELMET),
	EARTH_WARRIORS("Earth warriors", ItemID.BRONZE_FULL_HELM_TRIM),
	ELVES("Elves", ItemID.PICKPOCKET_GUIDE_WOODELF),
	ENTS("Ents", ItemID.POH_TREE_2),
	FEVER_SPIDERS("Fever spiders", ItemID.SLAYERGUIDE_FEVER_SPIDER),
	FIRE_GIANTS("Fire giants", ItemID.RTBRANDAPET),
	FLESH_CRAWLERS("Fleshcrawlers", ItemID.ARCEUUS_CORPSE_SCORPION_INITIAL),
	FOSSIL_ISLAND_WYVERNS("Fossil island wyverns", ItemID.SLAYERGUIDE_FOSSILWYVERN),
	GARGOYLES("Gargoyles", ItemID.SLAYERGUIDE_GARGOYLE),
	GENERAL_GRAARDOR("General Graardor", ItemID.BANDOSPET),
	GHOSTS("Ghosts", ItemID.AMULET_OF_GHOSTSPEAK),
	GHOULS("Ghouls", ItemID.TRICK_OR_TREAT_HEAD),
	GIANT_MOLE("The Giant Mole", ItemID.MOLEPET),
	GOBLINS("Goblin", ItemID.ARCEUUS_CORPSE_GOBLIN_INITIAL),
	GREATER_DEMONS("Greater demons", ItemID.GREATER_DEMON_MASK),
	GREEN_DRAGONS("Green dragons", ItemID.DRAGONMASK_GREEN),
	GROTESQUE_GUARDIANS("The Grotesque Guardians", ItemID.DUSKPET),
	HARPIE_BUG_SWARMS("Harpie bug swarms", ItemID.SLAYERGUIDE_SWARM),
	HELLHOUNDS("Hellhounds", ItemID.POH_HELLHOUND),
	HILL_GIANTS("Hill giants", ItemID.ARCEUUS_CORPSE_GIANT_INITIAL),
	HOBGOBLINS("Hobgoblins", ItemID.POH_HOBGOBLIN),
	HYDRAS("Hydras", ItemID.SLAYERGUIDE_HYDRA),
	ICEFIENDS("Icefiends", ItemID.FD_ICEDIAMOND),
	ICE_GIANTS("Ice giants", ItemID.RTELDRICPET),
	ICE_WARRIORS("Ice warriors", ItemID.MITHRIL_FULL_HELM_TRIM),
	INFERNAL_MAGES("Infernal mages", ItemID.SLAYERGUIDE_INFERNALMAGE),
	JAD("TzTok-Jad", ItemID.JAD_PET),
	JELLIES("Jellies", ItemID.SLAYERGUIDE_JELLY),
	JUNGLE_HORROR("Jungle horrors", ItemID.ARCEUUS_CORPSE_HORROR_INITIAL),
	KALPHITE("Kalphite", ItemID.POH_KALPHITE_SOLDIER),
	KALPHITE_QUEEN("The Kalphite Queen", ItemID.KQPET_WALKING),
	KILLERWATTS("Killerwatts", ItemID.SLAYERGUIDE_KILLERWATT),
	KING_BLACK_DRAGON("The King Black Dragon", ItemID.KBDPET),
	KRAKEN("The Cave Kraken Boss", ItemID.KRAKENPET),
	KREEARRA("Kree'arra", ItemID.ARMADYLPET),
	KRIL_TSUTSAROTH("K'ril Tsutsaroth", ItemID.ZAMORAKPET),
	KURASK("Kurask", ItemID.SLAYERGUIDE_KURASK),
	LAVA_DRAGONS("Lava Dragons", ItemID.LAVA_SCALE),
	LESSER_DEMONS("Lesser demons", ItemID.LESSER_DEMON_MASK),
	LESSER_NAGUA("Lesser Nagua", ItemID.SLAYERGUIDE_LESSER_NAGUA),
	LIZARDMEN("Lizardmen", ItemID.LIZARDMAN_FANG),
	LIZARDS("Lizards", ItemID.SLAYERGUIDE_LIZARD),
	MAGIC_AXES("Magic axes", ItemID.IRON_BATTLEAXE),
	MAMMOTHS("Mammoths", ItemID.BARBASSAULT_ATT_HORN_01),
	METAL_DRAGONS("Metal dragons", ItemID.POH_STEEL_DRAGON),
	MINIONS_OF_SCABARAS("Minions of scabaras", ItemID.NTK_SCARAB_GOLD),
	MINOTAURS("Minotaurs", ItemID.ARCEUUS_CORPSE_MINOTAUR_INITIAL),
	MOGRES("Mogres", ItemID.SLAYERGUIDE_MOGRE),
	MOLANISKS("Molanisks", ItemID.SLAYERGUIDE_MOLANISK),
	MONKEYS("Monkeys", ItemID.ARCEUUS_CORPSE_MONKEY_INITIAL),
	MOSS_GIANTS("Moss giants", ItemID.MOSSY_KEY),
	MUTATED_ZYGOMITES("Mutated zygomites", ItemID.SLAYER_ZYGOMITE_OBJECT),
	NECHRYAEL("Nechryael", ItemID.SLAYERGUIDE_NECHRYAEL),
	OGRES("Ogres", ItemID.ARCEUUS_CORPSE_OGRE_INITIAL),
	OTHERWORLDLY_BEING("Otherworldly beings", ItemID.SECRET_GHOST_HAT),
	PHANTOM_MUSPAH("The Phantom Muspah", ItemID.MUSPAHPET),
	PIRATES("Pirates", ItemID.BREW_RED_PIRATE_HAT),
	PYREFIENDS("Pyrefiends", ItemID.SLAYERGUIDE_PYRFIEND),
	RATS("Rats", ItemID.RATS_TAIL),
	RED_DRAGONS("Red dragons", ItemID.POH_DRAGON),
	REVENANTS("Revenants", ItemID.WILD_CAVE_BRACELET_CHARGED),
	ROCKSLUGS("Rockslugs", ItemID.SLAYERGUIDE_ROCKSLUG),
	ROGUES("Rogues", ItemID.ROGUESDEN_HELM),
	SARACHNIS("Sarachnis", ItemID.SARACHNISPET),
	SCORPIA("Scorpia", ItemID.SCORPIA_PET),
	SCORPIONS("Scorpions", ItemID.ARCEUUS_CORPSE_SCORPION_INITIAL),
	SEA_SNAKES("Sea snakes", ItemID.HUNDRED_ILM_SNAKE_CORPSE),
	SHADES("Shades", ItemID.BLACKROBETOP),
	SHADOW_WARRIORS("Shadow warriors", ItemID.BLACK_FULL_HELM),
	SKELETAL_WYVERNS("Skeletal wyverns", ItemID.SLAYERGUIDE_SKELETALWYVERN),
	SKELETONS("Skeletons", ItemID.POH_SKELETON_GUARD),
	SMOKE_DEVILS("Smoke devils", ItemID.CERT_GUIDE_ICON_DUMMY),
	SOURHOGS("Sourhogs", ItemID.PORCINE_SOURHOG_TROPHY),
	SPIDERS("Spiders", ItemID.POH_SPIDER),
	SPIRITUAL_CREATURES("Spiritual creatures", ItemID.DRAGON_BOOTS),
	SUQAHS("Suqahs", ItemID.SUQKA_TOOTH),
	TERROR_DOGS("Terror dogs", ItemID.SLAYERGUIDE_TERRORDOG),
	THE_LEVIATHAN("The Leviathan", ItemID.LEVIATHANPET),
	THE_WHISPERER("The Whisperer", ItemID.WHISPERERPET),
	THERMONUCLEAR_SMOKE_DEVIL("The Thermonuclear Smoke Devil", ItemID.SMOKEPET),
	TROLLS("Trolls", ItemID.POH_TROLL),
	TUROTH("Turoth", ItemID.SLAYERGUIDE_TUROTH),
	TZHAAR("Tzhaar", ItemID.ARCEUUS_CORPSE_TZHAAR_INITIAL),
	VAMPYRES("Vampyres", ItemID.STAKE),
	VARDORVIS("Vardorvis", ItemID.VARDORVISPET),
	VENENATIS("Venenatis", ItemID.VENENATIS_PET),
	VETION("Vet'ion", ItemID.VETION_PET),
	VORKATH("Vorkath", ItemID.VORKATHPET),
	WALL_BEASTS("Wall beasts", ItemID.SWAMP_WALLBEAST),
	WARPED_CREATURES("Warped Creatures", ItemID.POG_SLAYER_DUMMY_WARPED_TERRORBIRD),
	WATERFIENDS("Waterfiends", ItemID.WATER_ORB),
	WEREWOLVES("Werewolves", ItemID.DAGGER_WOLFBANE),
	WOLVES("Wolves", ItemID.GREY_WOLF_FUR),
	WYRMS("Wyrms", ItemID.SLAYERGUIDE_WYRM),
	ZILYANA("Commander Zilyana", ItemID.SARADOMINPET),
	ZOMBIES("Zombies", ItemID.TRICK_OR_TREAT_HEAD),
	ZUK("TzKal-Zuk", ItemID.INFERNOPET_ZUK),
	ZULRAH("Zulrah", ItemID.SNAKEPET);

	private static final Map<String, Integer> tasks;

	private final String name;
	private final int itemSpriteId;

	static
	{
		ImmutableMap.Builder<String, Integer> builder = new ImmutableMap.Builder<>();

		for (Task task : values())
		{
			builder.put(task.getName(), task.getItemSpriteId());
		}

		tasks = builder.build();
	}

	Task(String name, int itemSpriteId)
	{
		Preconditions.checkArgument(itemSpriteId >= 0);
		this.name = name;
		this.itemSpriteId = itemSpriteId;
	}

	static int getItemSpriteId(String taskName)
	{
		return tasks.getOrDefault(taskName, ItemID.SLAYER_GEM);
	}
}