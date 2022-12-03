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

import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.object.GameObject;
import com.rs.game.region.RegionBuilder;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.rsps.jessecustom.bosses.godwars.zamorak.ScaledKrilTstsaroth;
import com.rs.utils.music.Genre;
import com.rs.utils.music.Music;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class KreeArraScalingInstanceController extends Controller {
	private long lastPrayerRecharge;


	private RegionBuilder.DynamicRegionReference instance;
	final WorldTile locationOnExit = new WorldTile(2915, 3747, 0);
	WorldTile spawn;
	List<NPC> npcs = new ArrayList<>();

	public KreeArraScalingInstanceController() {
		super();
	}

	@Override
	public void start() {
		player.lock(5);
		instance = new RegionBuilder.DynamicRegionReference(4, 4);
		instance.copyMapAllPlanes(352, 661, () -> {
			spawn = instance.getLocalTile(8, 15);
			spawn.setLocation(spawn.getX(), spawn.getY(), 2);
			ScaledKreeArra boss = new ScaledKreeArra(6222, new WorldTile(spawn.getX() + 4, spawn.getY(), 2), false);
			npcs.add(boss);
			npcs.add(boss.minions[0]);
			npcs.add(boss.minions[1]);
			npcs.add(boss.minions[2]);
			for(NPC npc : npcs) {
				npc.setRespawnTask(75);
				npc.setForceMultiArea(true);
			}
			player.fadeScreen(() -> {
				player.resetReceivedHits();
				player.setNextWorldTile(spawn);
				player.setForceMultiArea(true);
			});
		});
	}

	@Override
	public Genre getGenre() {
		return Music.getGenreByName("God Wars Dungeon");
	}

	@Override
	public boolean playAmbientOnControllerRegionEnter() {
		return false;
	}

	@Override
	public boolean playAmbientMusic() {
		return true;
	}

	@Override
	public boolean login() {
		forceClose();
		return true;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean processObjectClick1(final GameObject object) {
		if (object.getId() == 26288) {
			if (lastPrayerRecharge >= System.currentTimeMillis()) {
				player.sendMessage("You must wait a total of 10 minutes before being able to recharge your prayer points.");
				return false;
			} else if (player.inCombat()) {
				player.sendMessage("You cannot recharge your prayer while engaged in combat.");
				return false;
			}
			player.getPrayer().restorePrayer(player.getSkills().getLevelForXp(Constants.PRAYER) * 10);
			player.setNextAnimation(new Animation(645));
			player.sendMessage("Your prayer points feel rejuvinated.");
			lastPrayerRecharge = 600000 + System.currentTimeMillis();
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(GameObject object) {
		if (object.getId() == 26288) {
			Magic.sendNormalTeleportNoType(player, locationOnExit);
			teled = true;
			forceClose();
			return false;
		}
		return true;
	}

	@Override
	public boolean sendDeath() {
		teled = true;
		forceClose();
		return true;
	}

	private boolean teled = false;
	@Override
	public void magicTeleported(int type) {
		teled = true;
		forceClose();
	}

	/**
	 * Close everything without teleporting
	 */
	@Override
	public void forceClose() {
		removeInstance();
		removeController();
	}

	private void removeInstance() {
		player.setForceMultiArea(false);
		instance.destroy(()->{
			if(!teled)
				player.setNextWorldTile(locationOnExit);
			for(NPC npc : npcs) {
				npc.finishAfterTicks(90);
				player.sendMessage("finished " + npc.getName());
			}
		});
	}

}
