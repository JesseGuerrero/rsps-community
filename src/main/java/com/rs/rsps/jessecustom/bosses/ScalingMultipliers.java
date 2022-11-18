package com.rs.rsps.jessecustom.bosses;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.handlers.NPCDropHandler;
import com.rs.rsps.jessecustom.bosses.godwars.ScaledGodWarMinion;

@PluginEventHandler
public class ScalingMultipliers {
	public static Object[] scalingAmountMultipliers() {
		return new Object[]{
				"Coins", "Fire rune", "Air rune", "Death rune", "Chaos rune", "Law rune",
				"Cosmic rune", "Blood rune", "Nature rune", "Adamant javelin", "Rune arrow",
				"Gold charm", "Green charm", "Crimson charm", "Blue charm", "Limpwurt root",
				"Steel dart", "Steel arrow"
		};
	}

	public static Object[] npcListForItemAmountMultipliers() {
		return new Object[] {
				1157, //Kalphite queen guardian
				6263, 6265, 6261 //Bandos minions
		};
	}

	public static NPCDropHandler increaseAmountByScale = new NPCDropHandler(ScalingMultipliers.npcListForItemAmountMultipliers(), ScalingMultipliers.scalingAmountMultipliers()) {
		@Override
		public void handle(NPCDropEvent e) {
			if(e.getNPC() instanceof NPCScaling npc)
				e.getItem().setAmount((int) (e.getItem().getAmount() * npc.scale));
			if(e.getNPC() instanceof ScaledGodWarMinion npc)
				e.getItem().setAmount((int) (e.getItem().getAmount() * npc.scale));
		}
	};
}
