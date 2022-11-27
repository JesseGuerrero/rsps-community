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
package com.rs.rsps.jessecustom.bosses.corp;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.object.GameObject;
import com.rs.game.region.RegionBuilder;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.rsps.jessecustom.CustomScape;
import com.rs.rsps.jessecustom.CustomScripts;
import com.rs.rsps.jessecustom.bosses.godwars.armadyl.ScaledKreeArra;
import com.rs.utils.music.Genre;
import com.rs.utils.music.Music;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class CorporealBeastScalingInstanceController extends Controller {
	private RegionBuilder.DynamicRegionReference instance;
	private double scale = 1;
	final WorldTile locationOnExit = new WorldTile(2970, 4384, 2);
	WorldTile spawn;

	public CorporealBeastScalingInstanceController(double scale) {
		super();
		this.scale = scale;
	}

	@Override
	public void start() {
		player.lock(5);
		instance = new RegionBuilder.DynamicRegionReference(20, 14);
		instance.copyMapAllPlanes(364, 545, () -> {
			spawn = instance.getLocalTile(58, 24);
			spawn.setLocation(spawn.getX(), spawn.getY(), 2);
			List<NPC> npcs = new ArrayList<>();
			npcs.add(new ScalingCorporealBeast(8133, new WorldTile(spawn.getX() + 10, spawn.getY(), 2), false, scale));
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
		return Music.getGenreByName("Wilderness");
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
	public boolean processObjectClick1(GameObject object) {
		if(object.getId() == 37929) {
			removeController();
			player.stopAll();
			player.setNextWorldTile(locationOnExit);
			return false;
		}
		if(isInsideLair())
			if (object.getId() == 38811) {
				player.setNextWorldTile(new WorldTile(spawn.getX(), player.getY(), 2));
				return false;
			}
		if(!isInsideLair())
			if (object.getId() == 38811) {
				player.setNextWorldTile(new WorldTile(spawn.getX() + 4, player.getY(), 2));
				return false;
			}
		return true;
	}

	private boolean isInsideLair() {
		return player.getX() > spawn.getX()+1;
	}

	private boolean teled = false;
	@Override
	public void magicTeleported(int type) {
		teled = true;
		forceClose();
	}

	@Override
	public boolean sendDeath() {
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					player.setNextAnimation(new Animation(836));
				else if (loop == 1)
					player.sendMessage("Oh dear, you have died.");
				else if (loop == 3) {
					if(!CustomScripts.deathCofferIsSuccessful(player)) {
						player.sendItemsOnDeath(null, false);
						player.reset();
						player.setNextWorldTile(new WorldTile(Settings.getConfig().getPlayerRespawnTile()));
						player.setNextAnimation(new Animation(-1));
					}
				} else if (loop == 4) {
					removeController();
					player.jingle(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
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
		instance.destroy(()->{if(!teled) player.setNextWorldTile(locationOnExit);});
	}

	public static ObjectClickHandler handleCororealEntrance = new ObjectClickHandler(new Object[] { 37929 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getPlayer().getX() <= 2918) {
				CustomScape.createCorpScalingDialogue(e.getPlayer());
				return;
			}
			e.getPlayer().setNextWorldTile(new WorldTile(2917, e.getPlayer().getY(), 2));
		}
	};


}
