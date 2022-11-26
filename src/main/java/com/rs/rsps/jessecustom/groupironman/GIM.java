package com.rs.rsps.jessecustom.groupironman;

import com.rs.db.WorldDB;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.rsps.jessecustom.adventurer_dialogue.AdventurerD;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@PluginEventHandler
public class GIM {
		public static void setGIMMode(Player p, boolean isGIM) {
		p.setIronMan(true);
		p.save("Group IronMan", isGIM);
		if(!GIM.hasTeam(p)) {
//			p.sendMessage("<col=FF0000><shad=000000>You are XP locked until you are in a team...");
			p.setXpLocked(true);
		}
	}

	public static boolean isGIM(Player p) {
		return p.getBool("Group IronMan");
	}

	public static String getGIMTeamName(Player p) {
		if(p.getO("GIM Team") == null)
			return "None";
		return p.getO("GIM Team");
	}

	public static boolean hasTeam(Player p) {
		return p.getO("GIM Team") != null;
	}

	public static void openGIM(String group_name, Consumer<GroupIronMan> func) {
		WorldDB.getGIMS().getByGroupName(group_name, func);
	}

	public static ObjectClickHandler openChest = new ObjectClickHandler(new Object[] { 170 }, new WorldTile[]{
			new WorldTile(3173, 3485, 0),
			new WorldTile(3156, 3485, 0),
			new WorldTile(3173, 3497, 0),
			new WorldTile(3156, 3497, 0)}) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getBool("Group IronMan")) {
				if (e.getPlayer().getO("GIM Team") == null) {
					e.getPlayer().sendMessage("You need to be part of a group to access a shared bank...");
					return;
				}
				e.getPlayer().getBank().openGIMBank();
			} else
				e.getPlayer().startConversation(new AdventurerD(e.getPlayer()));
		}
	};
}
