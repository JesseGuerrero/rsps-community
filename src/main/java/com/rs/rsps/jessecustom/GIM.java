package com.rs.rsps.jessecustom;

import com.rs.Settings;
import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ServerPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class GIM {
	public static NPCClickHandler handleGIMDialogue = new NPCClickHandler(new Object[] { 1512 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if(!e.getPlayer().getBank().checkPin())
				return;
			int NPC = e.getNPCId();
			e.getPlayer().startConversation(new AdventurerD(e.getPlayer()));
		}
	};

//	public static LoginHandler updateHintGIM = new LoginHandler() {
//		@Override
//		public void handle(LoginEvent e) {
//			if(!e.getPlayer().getBool("Group IronMan")) {
//				if(e.getPlayer().getRegionId() == 12342) {
//					for(NPC npc : World.getNPCsInRegion(12342))
//						if(npc.getId() == 1512)
//							e.getPlayer().getHintIconsManager().addHintIcon(6, npc, 0, -1, true);
//				}
//			}
//		}
//	};

	public static ObjectClickHandler openChest = new ObjectClickHandler(new Object[] { 170 }, new WorldTile[]{new WorldTile(3171, 3467, 0)}) {
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
