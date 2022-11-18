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
package com.rs.rsps.jessecustom.bosses.godwars;

import com.rs.game.content.bosses.godwars.GodWarMinion;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.lib.game.WorldTile;

import java.util.HashMap;
import java.util.Map;

public class ScaledGodWarMinion extends GodWarMinion {
	public double scale = 1;

	public ScaledGodWarMinion(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		this.scale = 1 + (scale/10.0);
		this.setCombatLevel((int)Math.ceil(getCombatLevel()* scale));
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
}
