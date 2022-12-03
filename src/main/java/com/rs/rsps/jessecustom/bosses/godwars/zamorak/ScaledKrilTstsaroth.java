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
package com.rs.rsps.jessecustom.bosses.godwars.zamorak;

import com.rs.cores.CoresManager;
import com.rs.game.content.bosses.godwars.GodWarMinion;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.rsps.jessecustom.bosses.godwars.ScaledGodWarMinion;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class ScaledKrilTstsaroth extends NPC {
	public GodWarMinion[] minions = new GodWarMinion[3];

	public ScaledKrilTstsaroth(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setIntelligentRouteFinder(true);
		setIgnoreDocile(true);
		setForceAggroDistance(64);

		minions[0] = new GodWarMinion(6204, tile.transform(-7, 0), spawned);
		minions[1] = new GodWarMinion(6206, tile.transform(0, -3), spawned);
		minions[2] = new GodWarMinion(6208, tile.transform(-4, 3), spawned);
		for(GodWarMinion minion : minions) {
			minion.setRespawnTask(75);
			minion.setForceMultiArea(true);
		}
	}

	@Override
	public void spawn() {
		super.spawn();
		respawnMinions();
	}

	public void respawnMinions() {
		CoresManager.schedule(() -> {
			for (GodWarMinion minion : minions)
				if (minion.hasFinished() || minion.isDead())
					minion.respawn();
		}, 2);
	}

}
