package com.rs.rsps.jessecustom.groupironman;

import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.rsps.jessecustom.groupironman.adventurer_dialogue.AdventurerD;

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

	public static void openGIM(String groupName, Consumer<GroupIronMan> func) {
		WorldDB.getGIMS().getByGroupName(groupName, func);
	}

	public static void removeGIM(String groupName) {
			openGIM(groupName, group -> {
				for(String username : group.getPlayers())
					World.forceGetPlayerByDisplay(username, player -> {
						player.delete("GIM Team");
						player.setXpLocked(true);
					});
				WorldDB.getGIMS().removeSync(group);
			});

	}

	public static GroupIronMan getGIMUnsyncReadOnly(String groupName) {
			return WorldDB.getGIMS().getGroupSyncName(groupName);
	}

	public static GroupIronMan getGIMUnsyncReadOnly(Player player) {
			return getGIMUnsyncReadOnly(getGIMTeamName(player));
	}

	public static boolean isGroupFounder(Player player) {
			if(!hasTeam(player))
				return false;
			return getGIMUnsyncReadOnly(player).isGroupLeader(player);
	}

	public static int getGroupPrestige(Player player) {
			return getGIMUnsyncReadOnly(player).getPrestigeManager().getPrestige();
	}

	public static int getIndividualPrestige(Player player) {
			if(player.getO("GroupPrestige") == null) {
				player.save("GroupPrestige", 0);
			}
			return player.getI("GroupPrestige");
	}

	public static void setIndividualPrestige(Player player, int prestige) {
		player.save("GroupPrestige", prestige);
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
				e.getPlayer().sendMessage("Only GIM have access to this chest.");
		}
	};
}
