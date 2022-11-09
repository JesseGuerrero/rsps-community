package com.rs.rsps.jessecustom;

import com.rs.game.content.skills.slayer.Master;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.WorldTile;

public class TaskFromSlayerMaster {
	public static Master highestSlayerMaster(Player player, Master master) {
		if(player.withinDistance(new WorldTile(3088, 3484, 0))) {
			for (int i = Master.values().length - 1; i >= 0; i--) {
				if (player.getSkills().getCombatLevelWithSummoning() >= Master.values()[i].requiredCombatLevel
						&& player.getSkills().getLevelForXp(Constants.SLAYER) >= Master.values()[i].reqSlayerLevel) {
					return Master.values()[i];
				}
			}
		}
		return master;
	}
}
