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
package com.rs.cores;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.OwnedObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;
import com.rs.web.Telemetry;

@PluginEventHandler
public final class WorldThread extends Thread {

	public static volatile long WORLD_CYCLE;

	protected WorldThread() {
		setPriority(Thread.MAX_PRIORITY);
		setName("World Thread");
		setUncaughtExceptionHandler((th, ex) -> Logger.handle(WorldThread.class, "uncaughtExceptionHandler", ex));
	}

	@ServerStartupEvent(Priority.SYSTEM)
	public static void init() {
		WORLD_CYCLE = System.currentTimeMillis() / 600L;
		CoresManager.getWorldExecutor().scheduleAtFixedRate(new WorldThread(), 0, Settings.WORLD_CYCLE_MS, TimeUnit.MILLISECONDS);
	}

	public static Set<String> NAMES = new HashSet<>();

	@Override
	public final void run() {
		WORLD_CYCLE++;
		try {
			long startTime = System.currentTimeMillis();
			WorldTasks.processTasks();
			OwnedObject.process();
			World.processRegions();
			NAMES.clear();
			for (Player player : World.getPlayers()) {
				try {
					if (player != null && player.getTempAttribs().getB("realFinished"))
						player.realFinish();
					if (player == null || !player.hasStarted() || player.hasFinished())
						continue;
					if (NAMES.contains(player.getUsername()))
						player.logout(false);
					else
						NAMES.add(player.getUsername());
					player.processEntity();
				} catch(Throwable e) {
					Logger.handle(WorldThread.class, "run:playerProcessEntity", "Error processing player: " + (player == null ? "NULL PLAYER" : player.getUsername()), e);
				}
			}
			for (NPC npc : World.getNPCs()) {
				try {
					if (npc == null || npc.hasFinished())
						continue;
					npc.processEntity();
				} catch(Throwable e) {
					Logger.handle(WorldThread.class, "run:npcProcessEntity", "Error processing NPC: " + (npc == null ? "NULL NPC" : npc.getId()), e);
				}
			}
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				try {
					player.processMovement();
				} catch(Throwable e) {
					Logger.handle(WorldThread.class, "processPlayerMovement", e);
				}
			}
			for (NPC npc : World.getNPCs()) {
				if (npc == null || npc.hasFinished())
					continue;
				try {
					npc.processMovement();
				} catch(Throwable e) {
					Logger.handle(WorldThread.class, "processNPCMovement", e);
				}
			}
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				try {
					player.getPackets().sendLocalPlayersUpdate();
					player.getPackets().sendLocalNPCsUpdate();
					player.postSync();
					player.processProjectiles();
					player.getSession().flush();
				} catch(Throwable e) {
					Logger.handle(WorldThread.class, "processPlayersPostSync", e);
				}
			}
			World.removeProjectiles();
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				player.resetMasks();
			}
			for (NPC npc : World.getNPCs()) {
				if (npc == null || npc.hasFinished())
					continue;
				npc.resetMasks();
			}
			World.processEntityLists();
			Telemetry.queueTelemetryTick((System.currentTimeMillis() - startTime));
		} catch (Throwable e) {
			Logger.handle(WorldThread.class, "tick", e);
		}
	}
}
