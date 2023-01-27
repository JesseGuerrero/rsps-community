//// This program is free software: you can redistribute it and/or modify
//// it under the terms of the GNU General Public License as published by
//// the Free Software Foundation, either version 3 of the License, or
//// (at your option) any later version.
////
//// This program is distributed in the hope that it will be useful,
//// but WITHOUT ANY WARRANTY; without even the implied warranty of
//// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//// GNU General Public License for more details.
////
//// You should have received a copy of the GNU General Public License
//// along with this program.  If not, see <http://www.gnu.org/licenses/>.
////
////  Copyright (C) 2021 Trenton Kress
////  This file is part of project: Darkan
////
//package com.rs.rsps.jessecustom.bosses.kalphitequeen;
//
//import com.rs.Settings;
//import com.rs.game.content.bosses.kalphitequeen.KalphiteQueen;
//import com.rs.game.content.death.DeathOfficeController;
//import com.rs.game.model.entity.npc.NPC;
//import com.rs.game.model.entity.player.Controller;
//import com.rs.game.model.object.GameObject;
//import com.rs.game.region.RegionBuilder.DynamicRegionReference;
//import com.rs.game.tasks.WorldTask;
//import com.rs.game.tasks.WorldTasks;
//import com.rs.lib.game.Animation;
//import com.rs.lib.game.WorldTile;
//import com.rs.plugin.annotations.PluginEventHandler;
//import com.rs.rsps.jessecustom.CustomScripts;
//import com.rs.rsps.jessecustom.bosses.NPCScaling;
//import com.rs.utils.music.Genre;
//import com.rs.utils.music.Music;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@PluginEventHandler
//public final class KalphiteQueenScalingInstanceController extends Controller {
//	private DynamicRegionReference instance;
//	final WorldTile locationOnExit = new WorldTile(3509, 9499, 2);
//	WorldTile spawn;
//	boolean teled=false;
//	List<NPC> npcs = new ArrayList<>();
//
//	//Multicombat
//	public KalphiteQueenScalingInstanceController() {
//		super();
//	}
//
//	@Override
//	public void start() {
//		player.lock(5);
//		instance = new DynamicRegionReference(8, 8);
//		instance.copyMapAllPlanes(433, 1183, () -> {
//			spawn = instance.getLocalTile(44, 29);
//			npcs.add(new NPC(1157, instance.getLocalTile(34, 35), false));
//			npcs.add(new NPC(1157, instance.getLocalTile(19, 46), false));
//			npcs.add(new NPC(1157, instance.getLocalTile(17, 31), false));
//			npcs.add(new KalphiteQueen(1158, instance.getLocalTile(16, 25), false));
//			for(NPC npc : npcs) {
//				npc.setRespawnTask(75);
//				npc.setForceMultiArea(true);
//			}
//			player.fadeScreen(() -> {
//				player.resetReceivedHits();
//				player.setNextWorldTile(spawn);
//				player.setForceMultiArea(true);
//			});
//		});
//	}
//
//	@Override
//	public Genre getGenre() {
//		return Music.getGenreByName("Other Dungeons");
//	}
//
//	@Override
//	public boolean playAmbientOnControllerRegionEnter() {
//		return false;
//	}
//
//	@Override
//	public boolean playAmbientMusic() {
//		return true;
//	}
//
//	@Override
//	public void magicTeleported(int type) {
//		teled = true;
//		forceClose();
//	}
//
//	@Override
//	public boolean processObjectClick1(GameObject object) {
//		if (object.getId() == 3832) {
//			player.setNextWorldTile(locationOnExit);
//			forceClose();
//			return false;
//		}
//		return true;
//	}
//
//	@Override
//	public boolean sendDeath() {
//		player.lock(7);
//		teled = true;
//		player.stopAll();
//		WorldTasks.schedule(new WorldTask() {
//			int loop;
//
//			@Override
//			public void run() {
//				if (loop == 0)
//					player.setNextAnimation(new Animation(836));
//				else if (loop == 1)
//					player.sendMessage("Oh dear, you have died.");
//				else if (loop == 3) {
//					forceClose();
//					if(!CustomScripts.deathCofferIsSuccessful(player))
//						player.getControllerManager().startController(new DeathOfficeController(Settings.getConfig().getPlayerRespawnTile(), player.hasSkull()));
//				} else if (loop == 4) {
//					player.jingle(90);
//					stop();
//				}
//				loop++;
//			}
//		}, 0, 1);
//		return false;
//	}
//
//	@Override
//	public boolean login() {
//		return true;
//	}
//
//	@Override
//	public boolean logout() {
//		removeInstance();
//		return true;
//	}
//
//	/**
//	 * Close everything without teleporting
//	 */
//	@Override
//	public void forceClose() {
//		removeInstance();
//		removeController();
//	}
//
//	private void removeInstance() {
//		player.setForceMultiArea(false);
//		instance.destroy(()->{
//			if(!teled)
//				player.setNextWorldTile(locationOnExit);
//			for(NPC npc : npcs) {
//				npc.finishAfterTicks(90);
//				player.sendMessage("finished " + npc.getName());
//			}
//		});
//	}
//
//
//}
//
