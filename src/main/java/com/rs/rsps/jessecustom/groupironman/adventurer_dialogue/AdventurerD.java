package com.rs.rsps.jessecustom.groupironman.adventurer_dialogue;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.rsps.jessecustom.groupironman.GIM;
import com.rs.rsps.jessecustom.groupironman.PrestigeGIMManager;

import static com.rs.rsps.jessecustom.groupironman.adventurer_dialogue.AdventurerDIntroduction.*;

@PluginEventHandler
public class AdventurerD extends Conversation {
	int NPC = 1512;

	String[] ranks = new String[] {"Nubby", "Novice", "Intermediate", "Advanced", "Veteran", "Completionist"};

	public static NPCClickHandler handleGIMDialogue = new NPCClickHandler(new Object[] { 1512 }, e -> {
		if(!e.getPlayer().getBank().checkPin())
			return;
		if(GIM.isGIM(e.getPlayer())) {
			if(e.getPlayer().getI("GroupPrestige") >= PrestigeGIMManager.NOVICE) {
				e.getPlayer().startConversation(new AdventurerD(e.getPlayer()));
				return;
			}
			e.getPlayer().startConversation(new AdventurerDIntroduction(e.getPlayer()));
			return;
		}
		e.getPlayer().sendMessage("Only GIM can talk to the adventurer...");
	});




	public AdventurerD(Player player) {
		super(player);
		addPlayer(HeadE.HAPPY_TALKING, "Hello there.");
		addNPC(NPC, HeadE.CALM_TALK, "Welcome back " + player.getDisplayName() + ". How can I help you?");
		addOptions("What would you like to do?", option -> {
			if(GIM.hasTeam(player)) {
				option.add("What is my group name & members?", groupInformationDialogue(player));
				option.add("Tell me about prestige...", prestigeDialogue(player));
				if(!GIM.isGroupFounder(player))
					option.add("I would like to leave my group.", leaveGroupDialogue(player));
			}
			if(!GIM.hasTeam(player)) {
				option.add("I’d like to start a new group.", startGroupDialogue(player));
				option.add(" I’d like to join a group.", joinGroupDialogue(player));
			}
			if(GIM.isGroupFounder(player)) {
				option.add("I'd like to accept someone into my group.", acceptPlayerDialogue(player));
				option.add("Kick a member.", kickMemberDialogue(player));
				option.add("I'd like to disband my group.", disbandGroupDialogue(player));
				option.add("Rename team", renameTeamDialogue(player));
			}
		});
		create();
	}
	private static boolean groupNamesMatch(String group1, String group2) {
		return Utils.formatPlayerNameForProtocol(group1).equals(Utils.formatPlayerNameForProtocol(group2));
	}

	private static void resetPlayer(Player p) {
		for (int skill = 0; skill < 25; skill++)
			p.getSkills().setXp(skill, 0);
		p.getSkills().init();
		p.getBank().clear();
		p.getEquipment().reset();
		p.getInventory().reset();
	}
}
