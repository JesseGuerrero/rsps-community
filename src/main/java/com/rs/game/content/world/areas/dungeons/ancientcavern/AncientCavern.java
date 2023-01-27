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
package com.rs.game.content.world.areas.dungeons.ancientcavern;

import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.WorldUtil;

@PluginEventHandler
public class AncientCavern {

	public static int FIXED_RING_VARBIT = 6774;

	public static ObjectClickHandler handleFixRing = new ObjectClickHandler(new Object[] { "Enchanted land" }, e -> {
		if (!e.getOption().equals("Plant")) {
			e.getPlayer().sendMessage("YIKES");
			return;
		}
		if (!e.getPlayer().getInventory().containsItem(6004, 5)) {
			e.getPlayer().sendMessage("You need 5 bittercap mushrooms to repair the fairy ring.");
			return;
		}
		e.getPlayer().setNextAnimation(new Animation(2291));
		e.getPlayer().getInventory().deleteItem(6004, 5);
		e.getPlayer().getVars().saveVarBit(FIXED_RING_VARBIT, 1);
	});
	
	public static ObjectClickHandler handleRoughSteps = new ObjectClickHandler(new Object[] { 25337, 39468 }, e -> {
		switch(e.getObjectId()) {
		case 25337 -> e.getPlayer().setNextWorldTile(WorldTile.of(1744, 5321, 1));
		case 39468 -> e.getPlayer().setNextWorldTile(WorldTile.of(1745, 5325, 0));
		}
	});

	public static ObjectClickHandler handleMithrilDoor = new ObjectClickHandler(new Object[] { 25341, 40208 }, e -> {
		e.getPlayer().useStairs(e.getObjectId() == 25341 ? WorldTile.of(1823, 5273, 0) : WorldTile.of(1759, 5342, 1));
	});

	public static ObjectClickHandler handleDownStepsEntrance = new ObjectClickHandler(new Object[] { 25338 }, e -> {
		e.getPlayer().setNextWorldTile(WorldTile.of(1772, 5366, 0));
	});

	public static ObjectClickHandler handleUpStepsEntrance = new ObjectClickHandler(new Object[] { 25336 }, e -> {
		e.getPlayer().setNextWorldTile(WorldTile.of(1768, 5366, 1));
	});

	public static ObjectClickHandler handleDownStepsMithDrags = new ObjectClickHandler(new Object[] { 25340 }, e -> {
		e.getPlayer().setNextWorldTile(WorldTile.of(1778, 5346, 0));
	});

	public static ObjectClickHandler handleUpStepsMithDrags = new ObjectClickHandler(new Object[] { 25339 }, e -> {
		e.getPlayer().setNextWorldTile(WorldTile.of(1778, 5343, 1));
	});

//	public static ObjectClickHandler handleDownStepsKuradal = new ObjectClickHandler(new Object[] { 39468 }) {
//		@Override
//		public void handle(ObjectClickEvent e) {
//			e.getPlayer().setNextWorldTile(WorldTile.of(1744, 5325, 0));
//		}
//	};

//	public static ObjectClickHandler handleUpStepsKuradal = new ObjectClickHandler(new Object[] { 25337 }) {
//		@Override
//		public void handle(ObjectClickEvent e) {
//			e.getPlayer().setNextWorldTile(e.getPlayer().isQuestComplete(Quest.WHILE_GUTHIX_SLEEPS) ? WorldTile.of(1774, 5321, 1) : WorldTile.of(1694, 5296, 1));
//		}
//	};

	public static ObjectClickHandler handleWhirlpool = new ObjectClickHandler(new Object[] { 67966 }, e -> {
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;

			@Override
			public void run() {
				if (e.getPlayer().getX() != 2512 || e.getPlayer().getY() != 3516 && ticks == 0)
					e.getPlayer().addWalkStep(2512, 3516, e.getPlayer().getX(), e.getPlayer().getY(), true);
				else {
					if (ticks == 1) {
						e.getPlayer().setNextAnimation(new Animation(6723));
						e.getPlayer().setFaceAngle(WorldUtil.getAngleTo(Direction.SOUTH));
						e.getPlayer().setNextForceMovement(new ForceMovement(WorldTile.of(2512, 3508, 0), 7, Direction.SOUTH));
						ticks++;
					} else if (ticks >= 7) {
						e.getPlayer().setNextWorldTile(WorldTile.of(1764, 5365, 1));
						stop();
					}
					ticks++;
				}
			}
		}, 0, 1);
	});

}
