package com.rs.rsps.jessecustom.bosses;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.handlers.NPCDropHandler;
import com.rs.rsps.jessecustom.CustomScape;
import com.rs.rsps.jessecustom.CustomScripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PluginEventHandler
public class ScalingItems {
	public static Object[] getAllScalingItems() {
		List<Object> allScalingItems = new ArrayList<>();
		allScalingItems.addAll(Arrays.asList(getMeleeStrengthScalingItems()));
		allScalingItems.addAll(Arrays.asList(getMeleeAttackScalingItems()));
		allScalingItems.addAll(Arrays.asList(getRangeStrengthScalingItems()));
		allScalingItems.addAll(Arrays.asList(getRangeAttackScalingItems()));
		allScalingItems.addAll(Arrays.asList(getDefensiveScalingItems()));
		allScalingItems.addAll(Arrays.asList(getMagicDefenseScalingItems()));
		allScalingItems.addAll(Arrays.asList(getMagicAttackScalingItems()));
		return allScalingItems.stream().distinct().toArray();
	}

	public static List<Object> getItemsByName(String name) {
		List<Object> items = new ArrayList<>();
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			boolean contains = true;
			if (!ItemDefinitions.getDefs(i).getName().toLowerCase().contains(name.toLowerCase())
					|| ItemDefinitions.getDefs(i).isLended() || ItemDefinitions.getDefs(i).isNoted()) {
				continue;
			}
			if (contains)
				items.add(ItemDefinitions.getDefs(i).getName());
		}
		return items;
	}

	public static Object[] getMeleeAttackScalingItems() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByName("sword"));
		items.addAll(getItemsByName("abyssal whip"));
		items.addAll(getItemsByName("spear"));
		items.addAll(getItemsByName("hilt"));
		items.addAll(getItemsByName("dagger"));
		items.addAll(getItemsByName("warhammer"));
		items.addAll(getItemsByName("scimitar"));
		items.addAll(getItemsByName("hatchet"));
		items.addAll(getItemsByName("regen bracelet"));
		items.addAll(getItemsByName("Saradomin's whisper"));
		return items.stream().distinct().toArray();
	}

	public static Object[] getMeleeStrengthScalingItems() {
		return new Object[]{
				"Amulet of power", "Bandos chestplate", "Bandos tassets", "Bandos boots",
				"Bandos warshield", "Bandos helmet", "Bandos gloves", "Saradomin's whisper",
				"Regen bracelet"
		};
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
		return items.stream().distinct().toArray();
	}

	public static NPCDropHandler addMetas = new NPCDropHandler(null, ScalingItems.getAllScalingItems()) {
		@Override
		public void handle(NPCDropEvent e) {
			if(e.getNPC().getTempAttribs() == null)
				return;
			if(e.getNPC() instanceof DungeonNPC)
				return;
			if(!CustomScape.isPlayerCustomScape(e.getPlayer()))
				return;
			int playerScale = e.getPlayer().getI("CustomScapeScale", 0);
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
		}
	};

}
