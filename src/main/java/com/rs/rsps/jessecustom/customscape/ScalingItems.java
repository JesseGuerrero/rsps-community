package com.rs.rsps.jessecustom.customscape;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.handlers.NPCDropHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PluginEventHandler
public class ScalingItems {
	public static Object[] getAllScalingItemsByID() {
		List<Object> allScalingItems = new ArrayList<>();
		allScalingItems.addAll(Arrays.asList(getMeleeStrengthScalingItemsByID()));
		allScalingItems.addAll(Arrays.asList(getMeleeAttackScalingItemsByID()));
		allScalingItems.addAll(Arrays.asList(getRangeStrengthScalingItemsByID()));
		allScalingItems.addAll(Arrays.asList(getRangeAttackScalingItemsByID()));
		allScalingItems.addAll(Arrays.asList(getDefensiveScalingItemsByID()));
		allScalingItems.addAll(Arrays.asList(getMagicDefenseScalingItemsByID()));
		allScalingItems.addAll(Arrays.asList(getMagicAttackScalingItemsByID()));
		return allScalingItems.stream().distinct().toArray();
	}

	public static List<Object> getItemsByID(String name) {
		List<Object> items = new ArrayList<>();
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			if (!ItemDefinitions.getDefs(i).getName().toLowerCase().contains(name.toLowerCase())
					|| ItemDefinitions.getDefs(i).isLended() || ItemDefinitions.getDefs(i).isNoted()) {
				continue;
			}
			if(ItemDefinitions.getDefs(i).isStackable()) {
				continue;
			}
			items.add(i);
		}
		return items;
	}

	public static Object[] getMeleeAttackScalingItemsByID() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByID("sword"));
		items.addAll(getItemsByID("battleaxe"));
		items.addAll(getItemsByID("abyssal whip"));
		items.addAll(getItemsByID("spear"));
		items.addAll(getItemsByID("hilt"));
		items.addAll(getItemsByID("dagger"));
		items.addAll(getItemsByID("warhammer"));
		items.addAll(getItemsByID("scimitar"));
		items.addAll(getItemsByID("hatchet"));
		items.addAll(getItemsByID("regen bracelet"));
		items.addAll(getItemsByID("Saradomin's whisper"));
		return items.stream().distinct().toArray();
	}

	public static Object[] getMeleeStrengthScalingItemsByID() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByID("amulet of power"));
		items.addAll(getItemsByID("bandos"));
		items.addAll(getItemsByID("Saradomin's whisper"));
		items.addAll(getItemsByID("Regen bracelet"));
		items.addAll(getItemsByID("torva"));
		return items.stream().distinct().toArray();
	}

	public static Object[] getRangeAttackScalingItemsByID() {

		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByID("Amulet of power"));
		items.addAll(getItemsByID("bow"));
		items.addAll(getItemsByID("armadyl"));
		items.addAll(getItemsByID("royal"));
		items.addAll(getItemsByID("hide"));
		items.addAll(getItemsByID("Regen bracelet"));
		items.addAll(getItemsByID("Saradomin's murmer"));
		items.addAll(getItemsByID("pernix"));
		return items.stream().distinct().toArray();
	}

	public static Object[] getRangeStrengthScalingItemsByID() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByID("Amulet of power"));
		items.addAll(getItemsByID("armadyl"));
		return items.stream().distinct().toArray();
	}

	public static Object[] getDefensiveScalingItemsByID() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByID("Amulet of power"));
		items.addAll(getItemsByID("body"));
		items.addAll(getItemsByID("plate"));
		items.addAll(getItemsByID("bandos"));
		items.addAll(getItemsByID("subjugation"));
		items.addAll(getItemsByID("armadyl"));
		items.addAll(getItemsByID("sigil"));
		items.addAll(getItemsByID("helmet"));
		items.addAll(getItemsByID("helm"));
		items.addAll(getItemsByID("shield"));
		items.addAll(getItemsByID("boots"));
		items.addAll(getItemsByID("glove"));
		items.addAll(getItemsByID("chap"));
		items.addAll(getItemsByID("coif"));
		items.addAll(getItemsByID("Regen bracelet"));
		items.addAll(getItemsByID("pernix"));
		items.addAll(getItemsByID("torva"));
		items.addAll(getItemsByName("virtus"));
		return items.stream().distinct().toArray();
	}
	public static Object[] getMagicDefenseScalingItemsByID() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByID("Amulet of power"));
		items.addAll(getItemsByID("body"));
		items.addAll(getItemsByID("plate"));
		items.addAll(getItemsByID("bandos"));
		items.addAll(getItemsByID("subjugation"));
		items.addAll(getItemsByID("armadyl"));
		items.addAll(getItemsByID("sigil"));
		items.addAll(getItemsByID("helmet"));
		items.addAll(getItemsByID("helm"));
		items.addAll(getItemsByID("shield"));
		items.addAll(getItemsByID("boots"));
		items.addAll(getItemsByID("glove"));
		items.addAll(getItemsByID("chap"));
		items.addAll(getItemsByID("coif"));
		items.addAll(getItemsByID("Regen bracelet"));
		items.addAll(getItemsByID("pernix"));
		items.addAll(getItemsByName("virtus"));
		return items.stream().distinct().toArray();
	}
	public static Object[] getMagicAttackScalingItemsByID() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByID("Amulet of power"));
		items.addAll(getItemsByID("subjugation"));
		items.addAll(getItemsByID("armadyl"));
		items.addAll(getItemsByID("sigil"));
		items.addAll(getItemsByID("mystic"));
		items.addAll(getItemsByID("staff"));
		items.addAll(getItemsByID("shield"));
		items.addAll(getItemsByID("boots"));
		items.addAll(getItemsByID("glove"));
		items.addAll(getItemsByID("wand"));
		items.addAll(getItemsByID("wizard"));
		items.addAll(getItemsByID("Regen bracelet"));
		items.addAll(getItemsByName("virtus"));
		return items.stream().distinct().toArray();
	}
	
	
	public static List<Object> getItemsByName(String name) {
		List<Object> items = new ArrayList<>();
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			if (!ItemDefinitions.getDefs(i).getName().toLowerCase().contains(name.toLowerCase())
					|| ItemDefinitions.getDefs(i).isLended() || ItemDefinitions.getDefs(i).isNoted()) {
				continue;
			}
			if(ItemDefinitions.getDefs(i).isStackable()) {
				continue;
			}
			items.add(ItemDefinitions.getDefs(i).getName());
		}
		return items;
	}

	public static Object[] getMeleeAttackScalingItems() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByName("sword"));
		items.addAll(getItemsByName("battleaxe"));
		items.addAll(getItemsByName("abyssal whip"));
		items.addAll(getItemsByName("spear"));
		items.addAll(getItemsByName("hilt"));
		items.addAll(getItemsByName("dagger"));
		items.addAll(getItemsByName("warhammer"));
		items.addAll(getItemsByName("scimitar"));
		items.addAll(getItemsByName("hatchet"));
		items.addAll(getItemsByName("regen bracelet"));
		items.addAll(getItemsByName("saradomin's whisper"));
		return items.stream().distinct().toArray();
	}

	public static Object[] getMeleeStrengthScalingItems() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByName("amulet of power"));
		items.addAll(getItemsByName("bandos"));
		items.addAll(getItemsByName("Saradomin's whisper"));
		items.addAll(getItemsByName("Regen bracelet"));
		items.addAll(getItemsByName("torva"));
		return items.stream().distinct().toArray();
	}

	public static Object[] getRangeAttackScalingItems() {

		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByName("Amulet of power"));
		items.addAll(getItemsByName("bow"));
		items.addAll(getItemsByName("armadyl"));
		items.addAll(getItemsByName("royal"));
		items.addAll(getItemsByName("hide"));
		items.addAll(getItemsByName("Regen bracelet"));
		items.addAll(getItemsByName("Saradomin's murmer"));
		items.addAll(getItemsByName("pernix"));
		return items.stream().distinct().toArray();
	}

	public static Object[] getRangeStrengthScalingItems() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByName("Amulet of power"));
		items.addAll(getItemsByName("armadyl"));
		return items.stream().distinct().toArray();
	}

	public static Object[] getDefensiveScalingItems() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByName("Amulet of power"));
		items.addAll(getItemsByName("body"));
		items.addAll(getItemsByName("plate"));
		items.addAll(getItemsByName("bandos"));
		items.addAll(getItemsByName("subjugation"));
		items.addAll(getItemsByName("armadyl"));
		items.addAll(getItemsByName("sigil"));
		items.addAll(getItemsByName("helmet"));
		items.addAll(getItemsByName("helm"));
		items.addAll(getItemsByName("shield"));
		items.addAll(getItemsByName("boots"));
		items.addAll(getItemsByName("glove"));
		items.addAll(getItemsByName("chap"));
		items.addAll(getItemsByName("coif"));
		items.addAll(getItemsByName("Regen bracelet"));
		items.addAll(getItemsByName("pernix"));
		items.addAll(getItemsByName("torva"));
		items.addAll(getItemsByName("virtus"));
		return items.stream().distinct().toArray();
	}
	public static Object[] getMagicDefenseScalingItems() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByName("Amulet of power"));
		items.addAll(getItemsByName("body"));
		items.addAll(getItemsByName("plate"));
		items.addAll(getItemsByName("bandos"));
		items.addAll(getItemsByName("subjugation"));
		items.addAll(getItemsByName("armadyl"));
		items.addAll(getItemsByName("sigil"));
		items.addAll(getItemsByName("helmet"));
		items.addAll(getItemsByName("helm"));
		items.addAll(getItemsByName("shield"));
		items.addAll(getItemsByName("boots"));
		items.addAll(getItemsByName("glove"));
		items.addAll(getItemsByName("chap"));
		items.addAll(getItemsByName("coif"));
		items.addAll(getItemsByName("Regen bracelet"));
		items.addAll(getItemsByName("pernix"));
		items.addAll(getItemsByName("virtus"));
		return items.stream().distinct().toArray();
	}
	public static Object[] getMagicAttackScalingItems() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByName("Amulet of power"));
		items.addAll(getItemsByName("subjugation"));
		items.addAll(getItemsByName("armadyl"));
		items.addAll(getItemsByName("sigil"));
		items.addAll(getItemsByName("mystic"));
		items.addAll(getItemsByName("staff"));
		items.addAll(getItemsByName("shield"));
		items.addAll(getItemsByName("boots"));
		items.addAll(getItemsByName("glove"));
		items.addAll(getItemsByName("wand"));
		items.addAll(getItemsByName("wizard"));
		items.addAll(getItemsByName("Regen bracelet"));
		items.addAll(getItemsByName("virtus"));
		return items.stream().distinct().toArray();
	}
	


	public static NPCDropHandler addMetas = new NPCDropHandler(null, ScalingItems.getAllScalingItemsByID(), e -> {
		if(e.getNPC().getTempAttribs() == null)
			return;
		if(e.getNPC() instanceof DungeonNPC)
			return;
		if(!CustomScape.isPlayerCustomScape(e.getPlayer()))
			return;
		int playerScale = e.getPlayer().getI("CustomScapeScale", 0);
		int id = e.getNPC().getId();
		if(id == 6260 || id == 6203 || id == 6222 || id == 6247 || id == 1158)
			playerScale*=4;
		if(id == 13447 || id == 8133)
			playerScale*=8;
		double playerCombatScale = 1 + (playerScale/10.0);

		double npcScale = e.getNPC().getTempAttribs().getI("CustomScapeScale");
		double npcCombatScale = 1 + (npcScale/10.0);
		double scale = 1;
		if(npcCombatScale >= playerCombatScale) {
			scale = playerCombatScale;
			if(Settings.getConfig().isDebug())
				e.getPlayer().sendMessage("This one");
		}
		if(npcCombatScale < playerCombatScale) {
			scale = npcCombatScale;
			if(Settings.getConfig().isDebug())
				e.getPlayer().sendMessage("This two " + e.getNPC().getTempAttribs().getD("CustomScapeScale"));
		}
		if(Settings.getConfig().isDebug())
			e.getPlayer().sendMessage("scale " + scale);
		CustomScape.scaleEquipmentBonus(e.getItem(), scale);
	});

}
