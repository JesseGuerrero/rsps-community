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
package com.rs.rsps.jessecustom.bosses.godwars.armadyl;

import com.rs.cores.CoresManager;
import com.rs.game.content.bosses.godwars.GodWarMinion;
import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.rsps.jessecustom.bosses.godwars.ScaledGodWarMinion;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class ScaledKreeArra extends NPC {
	public double combatScale = 1;
	private ScaledGodWarMinion[] minions = new ScaledGodWarMinion[3];

	public ScaledKreeArra(int id, WorldTile tile, boolean spawned, double scale) {
		super(id, tile, spawned);
		setIntelligentRouteFinder(true);
		setIgnoreDocile(true);
		setForceAggroDistance(64);
		minions[0] = new ScaledGodWarMinion(6223, tile.transform(7, 0), spawned);
		minions[1] = new ScaledGodWarMinion(6225, tile.transform(0, -3), spawned);
		minions[2] = new ScaledGodWarMinion(6227, tile.transform(3, -4), spawned);
		for(ScaledGodWarMinion minion : minions) {
			minion.setRespawnTask(75);
			minion.setForceMultiArea(true);
		}
		this.combatScale = 1 + (scale/10.0);
		this.setCombatLevel((int)Math.ceil(getCombatLevel()* combatScale));
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
		respawnMinions();
	}

	public void respawnMinions() {
		CoresManager.schedule(() -> {
			for (GodWarMinion minion : minions)
				if (minion.hasFinished() || minion.isDead())
					minion.respawn();
		}, 2);
	}

	@Override
	public boolean canBeAttackedBy(Player player) {
		if (!PlayerCombat.isRanging(player)) {
			player.sendMessage("Kree'arra is flying too high for you to attack using melee.");
			return false;
		}
		return true;
	}
}
