package com.rs.rsps.jessecustom.bosses;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.handlers.NPCDropHandler;
import com.rs.rsps.jessecustom.CustomScripts;
import com.rs.rsps.jessecustom.bosses.kalphitequeen.KalphiteQueenScaling;

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

	public static Object[] getMeleeAttackScalingItems() {
		return new Object[]{
				"Dragon 2h sword", "Adamant longsword", "Rune 2h sword", "Rune warhammer",
				"Rune hatchet", "Rune battleaxe", "Amulet of power", "Adamant spear"
		};
	}

	public static Object[] getMeleeStrengthScalingItems() {
		return new Object[]{
				"Amulet of power"
		};
	}

	public static Object[] getRangeAttackScalingItems() {
		return new Object[]{
				"Amulet of power"
		};
	}

	public static Object[] getRangeStrengthScalingItems() {
		return new Object[]{
				"Amulet of power"
		};
	}

	public static Object[] getDefensiveScalingItems() {
		return new Object[]{
				"Dragon chainbody", "Amulet of power"
		};
	}
	public static Object[] getMagicDefenseScalingItems() {
		return new Object[]{
				"Amulet of power"
		};
	}
	public static Object[] getMagicAttackScalingItems() {
		return new Object[]{
				"Lava battlestaff", "Amulet of power"
		};
	}

	public static Object[] npcListForScalingItems() {
		return new Object[] {
				1158, 1160
		};
	}

	public static NPCDropHandler addMetas = new NPCDropHandler(ScalingItems.npcListForScalingItems(), ScalingItems.getAllScalingItems()) {
		@Override
		public void handle(NPCDropEvent e) {
			if(e.getNPC() instanceof KalphiteQueenScaling npc) {
				CustomScripts.scaleEquipmentBonus(e.getItem(), npc.combatScale);
			}
		}
	};

}
