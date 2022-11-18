package com.rs.rsps.jessecustom.bosses;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.handlers.NPCDropHandler;

@PluginEventHandler
public class ScalingMultipliers {
	public static Object[] scalingAmountMultipliers() {
		return new Object[]{
				"Coins", "Fire rune", "Air rune", "Death rune", "Chaos rune", "Law rune", "Cosmic rune", "Blood rune",
				"Adamant javelin", "Rune arrow", "Gold charm", "Green charm", "Crimson charm", "Blue charm"
		};
	}

	public static Object[] npcListForItemAmountMultipliers() {
		return new Object[] {
			1157
		};
	}

	public static NPCDropHandler increaseAmountByScale = new NPCDropHandler(ScalingMultipliers.npcListForItemAmountMultipliers(), ScalingMultipliers.scalingAmountMultipliers()) {
		@Override
		public void handle(NPCDropEvent e) {
			if(e.getNPC() instanceof NPCScaling npc)
				e.getItem().setAmount((int) (e.getItem().getAmount() * npc.scale));
		}
	};
}
