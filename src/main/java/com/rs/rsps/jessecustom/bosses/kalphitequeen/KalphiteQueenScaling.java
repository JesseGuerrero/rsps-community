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
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class KalphiteQueenScaling extends KalphiteQueen {
	public double combatScale = 1;

	public KalphiteQueenScaling(int id, WorldTile tile, boolean spawned, double scale) {
		super(id, tile, spawned);
		this.combatScale = 1 + (scale/10.0);
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

	public static ObjectClickHandler handleKalphiteQueenLairEntrance = new ObjectClickHandler(true, new Object[] { 48803 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player player = e.getPlayer();
			if(e.getPlayer().isKalphiteLairSetted()) {
				if(e.getPlayer().getInventory().getAmountOf(995) >= 5_000) {
					e.getPlayer().startConversation(new Dialogue()
							.addOptions("Do you want to create a boss instance?", option -> {
								option.add("Yes", ()->{
									e.getPlayer().sendInputName("What instanced combat scale would you like? (1-10000)", scaleString -> {
										try {
											int scale = Integer.parseInt(scaleString);
											if(scale < 0)
												throw new NumberFormatException();
											player.getInventory().removeItems(new Item(995, 5000));
											player.getControllerManager().startController(new KalphiteQueenScalingInstanceController(scale));
										} catch(NumberFormatException n) {
											player.sendMessage("Improper scale formatting, try again.");
											return;
										}
									});
								});
								option.add("No", () -> {e.getPlayer().setNextWorldTile(new WorldTile(3508, 9494, 0));});
							})
					);
					return;
				}
				e.getPlayer().sendMessage("You need 5k coins for an instance");
				e.getPlayer().setNextWorldTile(new WorldTile(3508, 9494, 0));
			}
		}
	};

/*else if (id == 48803 && player.isKalphiteLairSetted())
				player.setNextWorldTile(new WorldTile(3508, 9494, 0));*/
}
