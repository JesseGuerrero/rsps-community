package com.rs.rsps;

import com.rs.game.World;
import com.rs.game.content.Toolbelt;
import com.rs.game.content.Toolbelt.Tools;
import com.rs.game.content.skills.magic.LodestoneAction;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class LoginUnlocks {
	public static LoginHandler handler = new LoginHandler(e -> {
		e.getPlayer().getNSV().setL("lastRandom", World.getServerTicks() + Ticks.fromHours(300));
		if (e.getPlayer().getStarter() <= 0) {
			for (LodestoneAction.Lodestone stone : LodestoneAction.Lodestone.values()) {
				if (stone == LodestoneAction.Lodestone.BANDIT_CAMP || stone == LodestoneAction.Lodestone.LUNAR_ISLE)
					continue;
				e.getPlayer().unlockLodestone(stone, null);
			}
			e.getPlayer().addToolbelt(1265);
			for (Tools tool : Tools.values()) {
				if (tool == null)
					continue;
				if (e.getPlayer().getToolbelt().get(tool) != null)
					continue;
				e.getPlayer().getToolbelt().put(tool, 1);
			}
			Toolbelt.refreshToolbelt(e.getPlayer());
		}
	});
}
