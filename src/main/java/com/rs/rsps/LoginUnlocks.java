package com.rs.rsps;

import com.rs.game.model.entity.actions.LodestoneAction.Lodestone;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.handlers.LoginHandler;

@PluginEventHandler
public class LoginUnlocks {
	public static LoginHandler handler = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if (e.getPlayer().getStarter() <= 0) {
				for (Lodestone stone : Lodestone.values()) {
					e.getPlayer().unlockLodestone(stone, null);
				}
			}
		}
	};
}
