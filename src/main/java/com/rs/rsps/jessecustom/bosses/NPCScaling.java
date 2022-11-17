package com.rs.rsps.jessecustom.bosses;

import com.rs.game.content.skills.dungeoneering.DungeonRewards;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.handlers.NPCDropHandler;
import com.rs.rsps.jessecustom.bosses.kalphitequeen.KalphiteQueenScaling;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
@PluginEventHandler
public class NPCScaling extends NPC {
	public double scale = 1;
	public NPCScaling(int id, WorldTile tile, boolean spawned, int scale) {
		super(id, tile, spawned);
		this.scale = scale + (scale/10.0);
		this.setCombatLevel((int)Math.ceil(NPCCombatDefinitions.getDefs(super.getId()).getCombatLevel()*this.scale));
	}

	@Override
	public int getMaxHitpoints() {
		return (int)Math.ceil(getCombatDefinitions().getHitpoints()*scale);
	}

	@Override
	public void spawn() {
		super.spawn();
		Map<NPCCombatDefinitions.Skill, Integer> levels = NPCCombatDefinitions.getDefs(super.getId()).getLevels();
		Map<NPCCombatDefinitions.Skill, Integer> upgradedStats = new HashMap<>();
		for(NPCCombatDefinitions.Skill combatSkill : levels.keySet())
			upgradedStats.put(combatSkill, (int) Math.ceil(levels.get(combatSkill) * scale));
		this.setLevels(upgradedStats);
	}

	public static NPCDropHandler increaseAmountByScale = new NPCDropHandler(new Object[]{1157}, new Object[]{
			"Coins", "Fire rune", "Air rune", "Death rune", "Chaos rune", "Law rune", "Cosmic rune", "Blood rune",
			"Adamant javelin", "Rune arrow", "Gold charm", "Green charm", "Crimson charm", "Blue charm"
	}) {
		@Override
		public void handle(NPCDropEvent e) {
			if(e.getNPC() instanceof NPCScaling npc)
				e.getItem().setAmount((int) (e.getItem().getAmount() * npc.scale));
		}
	};
}
