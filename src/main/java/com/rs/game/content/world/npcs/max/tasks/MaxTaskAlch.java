package com.rs.game.content.world.npcs.max.tasks;

import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.world.npcs.max.Max;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class MaxTaskAlch implements Task {
	
	private enum AlchLocation {
		EDGE(WorldTile.of(3087, 3494, 0), WorldTile.of(3093, 3497, 0)),
		SEERS(WorldTile.of(2757, 3479, 0), WorldTile.of(2725, 3491, 0)),
		VARROCK(WorldTile.of(3212, 3423, 0), WorldTile.of(3185, 3436, 0)),
		;
		
		private WorldTile tele, alchSpot;
		
		AlchLocation(WorldTile tele, WorldTile alchSpot) {
			this.tele = tele;
			this.alchSpot = alchSpot;
		}
	}
		
	private boolean started = false;
	private boolean atLocation = false;
	private int itemsAlched = Utils.random(85, 150);
	private AlchLocation loc = AlchLocation.values()[Utils.random(AlchLocation.values().length)];
	
	@Override
	public int tick(Max max) {
		if (!started) {
			max.wearItems(6563, 18747);
			started = true;
		}
		if (!max.withinDistance(loc.alchSpot, 64)) {
			Magic.npcNormalTeleport(max, loc.tele, true, null);
			return 10;
		}
		if (itemsAlched <= 0) {
			max.nextTask();
			return 10;
		}
		if (!atLocation && !max.getActionManager().hasSkillWorking() && !max.hasWalkSteps()) {
			max.setRouteEvent(new RouteEvent(WorldTile.of(loc.alchSpot, 2), () -> {
				atLocation = true;
				max.repeatAction(5, count -> {
					if (itemsAlched <= 0) {
						max.nextTask();
						return false;
					}
					max.anim(9633);
					max.spotAnim(1693);
					itemsAlched--;
					return true;
				});
			}));
		}
		return 5;
	}
}
