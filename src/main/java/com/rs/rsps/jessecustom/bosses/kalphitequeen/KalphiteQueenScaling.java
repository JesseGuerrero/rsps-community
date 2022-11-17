// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.rsps.jessecustom.bosses.kalphitequeen;

import com.rs.game.content.bosses.kalphitequeen.KalphiteQueen;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.handlers.NPCDropHandler;
import com.rs.rsps.jessecustom.CustomScripts;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class KalphiteQueenScaling extends KalphiteQueen {
	double combatScale = 1;

	public KalphiteQueenScaling(int id, WorldTile tile, boolean spawned, int scale) {
		super(id, tile, spawned);
		this.combatScale = scale + (scale/10.0);
	}

	@Override
	public int getMaxHitpoints() {
		return (int)Math.ceil(NPCCombatDefinitions.getDefs(super.getId()).getHitpoints()* combatScale);
	}

	@Override
	public void spawn() {
		super.spawn();
		Map<NPCCombatDefinitions.Skill, Integer> levels = NPCCombatDefinitions.getDefs(super.getId()).getLevels();
		Map<NPCCombatDefinitions.Skill, Integer> upgradedStats = new HashMap<>();
		for(NPCCombatDefinitions.Skill combatSkill : levels.keySet())
			upgradedStats.put(combatSkill, (int) Math.ceil(levels.get(combatSkill) * combatScale));
		this.setLevels(upgradedStats);
		this.setCombatLevel((int)Math.ceil(NPCCombatDefinitions.getDefs(super.getId()).getCombatLevel()* combatScale));
	}

	@Override
	public void reset() {
		super.reset();
		Map<NPCCombatDefinitions.Skill, Integer> levels = NPCCombatDefinitions.getDefs(super.getId()).getLevels();
		Map<NPCCombatDefinitions.Skill, Integer> upgradedStats = new HashMap<>();
		for(NPCCombatDefinitions.Skill combatSkill : levels.keySet()) {
			upgradedStats.put(combatSkill, (int) Math.ceil(levels.get(combatSkill) * combatScale));
		}
		this.setLevels(upgradedStats);
	}

	public static NPCDropHandler addMetas = new NPCDropHandler(new Object[]{1158, 1160}, new Object[]{
			"Dragon 2h sword", "Dragon chainbody", "Adamant longsword", "Rune 2h sword", "Rune warhammer",
			"Rune hatchet", "Rune battleaxe", "Lava battlestaff"
	}) {
		@Override
		public void handle(NPCDropEvent e) {
			if(e.getNPC() instanceof KalphiteQueenScaling npc) {
				if(e.getItem().getName().equalsIgnoreCase("Dragon chainbody")) {
					CustomScripts.scaleEquipmentBonus(e.getItem(), "defense", npc.combatScale);
				}
			}
		}
	};
}
