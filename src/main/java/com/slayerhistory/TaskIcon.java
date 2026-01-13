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

import java.util.Map;
import net.runelite.api.gameval.ItemID;

class TaskIcon
{
	private static final Map<String, Integer> tasks = Map.<String, Integer>ofEntries(
		Map.entry("aberrant spectres", ItemID.SLAYERGUIDE_ABERRANTSPECTER),
		Map.entry("abyssal demons", ItemID.SLAYERGUIDE_ABYSSALDEMON),
		Map.entry("the abyssal sire", ItemID.ABYSSALSIRE_PET),
		Map.entry("the alchemical hydra", ItemID.HYDRAPET),
		Map.entry("ankou", ItemID.ANKOU_HEAD),
		Map.entry("araxxor", ItemID.ARAXXORPET),
		Map.entry("araxytes", ItemID.POG_SLAYER_DUMMY_ARAXYTES),
		Map.entry("aviansies", ItemID.ARCEUUS_CORPSE_AVIANSIE_INITIAL),
		Map.entry("bandits", ItemID.PICKPOCKET_GUIDE_DESERT_BANDIT),
		Map.entry("banshees", ItemID.SLAYERGUIDE_BANSHEE),
		Map.entry("barrows brothers", ItemID.BARROWS_KARIL_HEAD),
		Map.entry("basilisks", ItemID.SLAYERGUIDE_BASILISK),
		Map.entry("bats", ItemID.RAIDS_BAT2_COOKED),
		Map.entry("bears", ItemID.ARCEUUS_CORPSE_BEAR_INITIAL),
		Map.entry("birds", ItemID.FEATHER),
		Map.entry("black demons", ItemID.BLACK_DEMON_MASK),
		Map.entry("black dragons", ItemID.DRAGONMASK_BLACK),
		Map.entry("black knights", ItemID.BLACK_FULL_HELM),
		Map.entry("bloodveld", ItemID.SLAYERGUIDE_BLOODVELD),
		Map.entry("blue dragons", ItemID.DRAGONMASK_BLUE),
		Map.entry("brine rats", ItemID.OLAF2_BRINE_RAT_INV),
		Map.entry("callisto", ItemID.CALLISTO_PET),
		Map.entry("catablepon", ItemID.SOS_HALF_SKULL2),
		Map.entry("cave bugs", ItemID.SWAMP_CAVE_BUG),
		Map.entry("cave crawlers", ItemID.SLAYERGUIDE_CAVECRAWLER),
		Map.entry("cave horrors", ItemID.SLAYERGUIDE_HARMLESS_CAVE_HORROR),
		Map.entry("cave kraken", ItemID.CERT_EADGAR_FADE_TO_BLACK_INV),
		Map.entry("cave slimes", ItemID.SWAMP_CAVE_SLIME),
		Map.entry("cerberus", ItemID.HELL_PET),
		Map.entry("chaos druids", ItemID.ELDERCHAOS_HOOD),
		Map.entry("the chaos elemental", ItemID.CHAOSELEPET),
		Map.entry("the chaos fanatic", ItemID.STAFF_OF_ZAROS),
		Map.entry("cockatrice", ItemID.SLAYERGUIDE_COCKATRICE),
		Map.entry("cows", ItemID.COW_MASK),
		Map.entry("crabs", ItemID.HUNDRED_PIRATE_CRAB_SHELL_GAUNTLET),
		Map.entry("crawling hands", ItemID.SLAYERGUIDE_CRAWLINGHAND),
		Map.entry("crazy archaeologists", ItemID.FEDORA),
		Map.entry("crocodiles", ItemID.GREEN_SALAMANDER),
		Map.entry("custodian stalkers", ItemID.SLAYERGUIDE_CUSTODIAN_STALKER_MATURE),
		Map.entry("dagannoth", ItemID.POH_DAGGANOTH),
		Map.entry("dagannoth kings", ItemID.PRIMEPET),
		Map.entry("dark beasts", ItemID.SLAYERGUIDE_DARK_BEAST),
		Map.entry("dark warriors", ItemID.BLACK_MED_HELM),
		Map.entry("dogs", ItemID.POH_GUARD_DOG),
		Map.entry("drakes", ItemID.SLAYERGUIDE_DRAKE),
		Map.entry("duke sucellus", ItemID.DUKESUCELLUSPET),
		Map.entry("dust devils", ItemID.SLAYERGUIDE_DUSTDEVIL),
		Map.entry("dwarves", ItemID.EMPTY_OBJECT),  // we will load our own image
		Map.entry("earth warriors", ItemID.BRONZE_FULL_HELM_TRIM),
		Map.entry("elves", ItemID.PICKPOCKET_GUIDE_WOODELF),
		Map.entry("ents", ItemID.POH_TREE_2),
		Map.entry("fever spiders", ItemID.SLAYERGUIDE_FEVER_SPIDER),
		Map.entry("fire giants", ItemID.RTBRANDAPET),
		Map.entry("fleshcrawlers", ItemID.ARCEUUS_CORPSE_SCORPION_INITIAL),
		Map.entry("fossil island wyverns", ItemID.SLAYERGUIDE_FOSSILWYVERN),
		Map.entry("gargoyles", ItemID.SLAYERGUIDE_GARGOYLE),
		Map.entry("general graardor", ItemID.BANDOSPET),
		Map.entry("ghosts", ItemID.AMULET_OF_GHOSTSPEAK),
		Map.entry("ghouls", ItemID.TRICK_OR_TREAT_HEAD),
		Map.entry("the giant mole", ItemID.MOLEPET),
		Map.entry("goblins", ItemID.ARCEUUS_CORPSE_GOBLIN_INITIAL),
		Map.entry("greater demons", ItemID.GREATER_DEMON_MASK),
		Map.entry("green dragons", ItemID.DRAGONMASK_GREEN),
		Map.entry("the grotesque guardians", ItemID.DUSKPET),
		Map.entry("harpie bug swarms", ItemID.SLAYERGUIDE_SWARM),
		Map.entry("hellhounds", ItemID.POH_HELLHOUND),
		Map.entry("hill giants", ItemID.ARCEUUS_CORPSE_GIANT_INITIAL),
		Map.entry("hobgoblins", ItemID.POH_HOBGOBLIN),
		Map.entry("hydras", ItemID.SLAYERGUIDE_HYDRA),
		Map.entry("icefiends", ItemID.FD_ICEDIAMOND),
		Map.entry("ice giants", ItemID.RTELDRICPET),
		Map.entry("ice warriors", ItemID.MITHRIL_FULL_HELM_TRIM),
		Map.entry("infernal mages", ItemID.SLAYERGUIDE_INFERNALMAGE),
		Map.entry("tztok-jad", ItemID.JAD_PET),
		Map.entry("jellies", ItemID.SLAYERGUIDE_JELLY),
		Map.entry("jungle horrors", ItemID.ARCEUUS_CORPSE_HORROR_INITIAL),
		Map.entry("kalphites", ItemID.POH_KALPHITE_SOLDIER),
		Map.entry("the kalphite queen", ItemID.KQPET_WALKING),
		Map.entry("killerwatts", ItemID.SLAYERGUIDE_KILLERWATT),
		Map.entry("the king black dragon", ItemID.KBDPET),
		Map.entry("the cave kraken boss", ItemID.KRAKENPET),
		Map.entry("kree'arra", ItemID.ARMADYLPET),
		Map.entry("k'ril tsutsaroth", ItemID.ZAMORAKPET),
		Map.entry("kurask", ItemID.SLAYERGUIDE_KURASK),
		Map.entry("lava dragons", ItemID.LAVA_SCALE),
		Map.entry("lesser demons", ItemID.LESSER_DEMON_MASK),
		Map.entry("lesser nagua", ItemID.SLAYERGUIDE_LESSER_NAGUA),
		Map.entry("lizardmen", ItemID.LIZARDMAN_FANG),
		Map.entry("lizards", ItemID.SLAYERGUIDE_LIZARD),
		Map.entry("magic axes", ItemID.IRON_BATTLEAXE),
		Map.entry("mammoths", ItemID.BARBASSAULT_ATT_HORN_01),
		Map.entry("metal dragons", ItemID.POH_STEEL_DRAGON),
		Map.entry("minotaurs", ItemID.ARCEUUS_CORPSE_MINOTAUR_INITIAL),
		Map.entry("mogres", ItemID.SLAYERGUIDE_MOGRE),
		Map.entry("molanisks", ItemID.SLAYERGUIDE_MOLANISK),
		Map.entry("monkeys", ItemID.ARCEUUS_CORPSE_MONKEY_INITIAL),
		Map.entry("moss giants", ItemID.MOSSY_KEY),
		Map.entry("mutated zygomites", ItemID.SLAYER_ZYGOMITE_OBJECT),
		Map.entry("nechryael", ItemID.SLAYERGUIDE_NECHRYAEL),
		Map.entry("ogres", ItemID.ARCEUUS_CORPSE_OGRE_INITIAL),
		Map.entry("otherworldly beings", ItemID.SECRET_GHOST_HAT),
		Map.entry("the phantom muspah", ItemID.MUSPAHPET),
		Map.entry("pirates", ItemID.BREW_RED_PIRATE_HAT),
		Map.entry("pyrefiends", ItemID.SLAYERGUIDE_PYRFIEND),
		Map.entry("rats", ItemID.RATS_TAIL),
		Map.entry("red dragons", ItemID.POH_DRAGON),
		Map.entry("revenants", ItemID.WILD_CAVE_BRACELET_CHARGED),
		Map.entry("rockslugs", ItemID.SLAYERGUIDE_ROCKSLUG),
		Map.entry("rogues", ItemID.ROGUESDEN_HELM),
		Map.entry("sarachnis", ItemID.SARACHNISPET),
		Map.entry("scabarites", ItemID.NTK_SCARAB_GOLD),
		Map.entry("scorpia", ItemID.SCORPIA_PET),
		Map.entry("scorpions", ItemID.ARCEUUS_CORPSE_SCORPION_INITIAL),
		Map.entry("sea snakes", ItemID.CERT_FISHING_SPOT_ICON_DUMMY),
		Map.entry("shades", ItemID.BLACKROBETOP),
		Map.entry("shadow warriors", ItemID.BLACK_FULL_HELM),
		Map.entry("skeletal wyverns", ItemID.SLAYERGUIDE_SKELETALWYVERN),
		Map.entry("skeletons", ItemID.POH_SKELETON_GUARD),
		Map.entry("smoke devils", ItemID.CERT_GUIDE_ICON_DUMMY),
		Map.entry("sourhogs", ItemID.PORCINE_SOURHOG_TROPHY),
		Map.entry("spiders", ItemID.POH_SPIDER),
		Map.entry("spiritual creatures", ItemID.DRAGON_BOOTS),
		Map.entry("suqahs", ItemID.SUQKA_TOOTH),
		Map.entry("terror dogs", ItemID.SLAYERGUIDE_TERRORDOG),
		Map.entry("the leviathan", ItemID.LEVIATHANPET),
		Map.entry("the thermonuclear smoke devil", ItemID.SMOKEPET),
		Map.entry("the whisperer", ItemID.WHISPERERPET),
		Map.entry("trolls", ItemID.POH_TROLL),
		Map.entry("turoth", ItemID.SLAYERGUIDE_TUROTH),
		Map.entry("tzhaar", ItemID.ARCEUUS_CORPSE_TZHAAR_INITIAL),
		Map.entry("vampyres", ItemID.SLAYERGUIDE_VAMPYRE),
		Map.entry("vardorvis", ItemID.VARDORVISPET),
		Map.entry("venenatis", ItemID.VENENATIS_PET),
		Map.entry("vet'ion", ItemID.VETION_PET),
		Map.entry("vorkath", ItemID.VORKATHPET),
		Map.entry("wall beasts", ItemID.SWAMP_WALLBEAST),
		Map.entry("warped creatures", ItemID.POG_SLAYER_DUMMY_WARPED_TERRORBIRD),
		Map.entry("waterfiends", ItemID.WATER_ORB),
		Map.entry("werewolves", ItemID.DAGGER_WOLFBANE),
		Map.entry("wolves", ItemID.GREY_WOLF_FUR),
		Map.entry("wyrms", ItemID.SLAYERGUIDE_WYRM),
		Map.entry("commander zilyana", ItemID.SARADOMINPET),
		Map.entry("zombies", ItemID.TRICK_OR_TREAT_HEAD),
		Map.entry("tzkal-zuk", ItemID.INFERNOPET_ZUK),
		Map.entry("zulrah", ItemID.SNAKEPET),
		Map.entry("aquanites", ItemID.SLAYERGUIDE_AQUANITE),
		Map.entry("gryphons", ItemID.GRYPHON_FEATHER_5),
		Map.entry("the shellbane gryphon", ItemID.SLAYERGUIDE_GRYPHON),
		Map.entry("frost dragons", ItemID.FROST_DRAGON_BONES)
	);

	static int getItemSpriteId(String taskName)
	{
		return tasks.getOrDefault(taskName.toLowerCase(), ItemID.SLAYER_GEM);
	}
}