package com.rs.rsps.jessecustom;

import com.rs.db.WorldDB;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class GIM {
	public static List<GroupIronMan> IRONMAN_GROUPS = new ArrayList<>();

	private static void resetPlayer(Player p) {
		for (int skill = 0; skill < 25; skill++)
			p.getSkills().setXp(skill, 0);
		p.getSkills().init();
		for (Quest quest : Quest.values())
			if (quest.isImplemented()) {
				p.getQuestManager().resetQuest(quest);
				p.sendMessage("Reset quest: " + quest.name());
			}
		p.getBank().clear();
		p.getEquipment().reset();
		p.getInventory().reset();
	}
	public static NPCClickHandler handleGIMDialogue = new NPCClickHandler(new Object[] { 1512 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if(!e.getPlayer().getBank().checkPin())
				return;
			int NPC = e.getNPCId();
			if(e.getPlayer().getBool("Group IronMan")) {
				if(e.getPlayer().getO("GIM Team") == null) {
					e.getPlayer().startConversation(new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Hi.")
							.addNPC(NPC, HeadE.CALM_TALK, "Hey.")
							.addNPC(NPC, HeadE.CALM_TALK, "Would you like to join a group or start a group?")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("Start a group", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "I would like to start a group.")
											.addNext(()->{
												e.getPlayer().sendInputName("What group name would you like?", groupName -> {
															if (!WorldDB.getGIMS().groupExists(groupName)) {
																WorldDB.getGIMS().save(new GroupIronMan(groupName, e.getPlayer()), () -> {
																	e.getPlayer().sendMessage("Group created!");
																	e.getPlayer().save("GIM Team", groupName);
																});
																return;
															}
															e.getPlayer().sendMessage("This group name already exists!");
														}
												);
											})
									);
									option("Join a group", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "I would like to join a group.")
											.addNPC(NPC, HeadE.CALM_TALK, "Okay have your group founder talk to me")
									);
								}
							}));
					return;
				}
				e.getPlayer().startConversation(new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Hi!")
						.addNPC(NPC, HeadE.CALM_TALK, "Hey.")
						.addNPC(NPC, HeadE.CALM_TALK, "What would you like to do?")
						.addOptions("I would like to...", new Options() {
							@Override
							public void create() {
								option("Accept a new member", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "")
								);
								option("Leave a group.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "")
								);
								option("Nothing.", new Dialogue());
							}
						})
				);
				return;
			}
			if(e.getPlayer().isIronMan()) {
				e.getPlayer().startConversation(new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Hi!")
						.addNPC(NPC, HeadE.CALM_TALK, "Hey.")
						.addPlayer(HeadE.HAPPY_TALKING, "What is this chest about?")
						.addNPC(NPC, HeadE.CALM_TALK, "Oh, it is for Group Iron Man & Woman.")
						.addPlayer(HeadE.HAPPY_TALKING, "Oh I see.")
						.addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("Will you let me join a Group IronMan group?", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "Will you let me join a Group IronMan group?")
										.addNPC(NPC, HeadE.CALM_TALK, "Sure, however, very specific things will happen if you become a Group Iron Player")
										.addNPC(NPC, HeadE.CALM_TALK, "First, you lose ALL your items!")
										.addNPC(NPC, HeadE.CALM_TALK, "Second, you lose ALL your stats!")
										.addNPC(NPC, HeadE.CALM_TALK, "Third, you lose ALL your quests!")
										.addNPC(NPC, HeadE.CALM_TALK, "Lastly, it's PERMANENT!")
										.addNPC(NPC, HeadE.CALM_TALK, "Essentially you are starting over and joining a team is irreversible.")
										.addPlayer(HeadE.AMAZED, "Wow!")
										.addNPC(NPC, HeadE.CALM_TALK, "Do you still want to become a Group IronMan?")
										.addOptions("Become a group IronMan?", new Options() {
											@Override
											public void create() {
												option("Yes, I KNOW the consequences", new Dialogue()
														.addPlayer(HeadE.HAPPY_TALKING, "Yes, I KNOW the consequences")
														.addNPC(NPC, HeadE.CALM_TALK, "Don't complain to me when you lose your stuff...")
														.addOptions("Are you sure?:", new Options() {
															@Override
															public void create() {
																option("No", new Dialogue());
																option("Yes", new Dialogue()
																		.addPlayer(HeadE.HAPPY_TALKING, "Yes I am sure.")
																		.addSimple("He tears a piece of his cape and rubs it on your face", () -> {
																			resetPlayer(e.getPlayer());
																			e.getPlayer().setIronMan(true);
																			e.getPlayer().save("Group IronMan", true);
																		})
																		.addNPC(NPC, HeadE.CALM_TALK, "Congratulations! You are a Group IronMan...")
																		.addPlayer(HeadE.HAPPY_TALKING, "Really?")
																		.addNPC(NPC, HeadE.CALM_TALK, "Yup! Talk to me again to select a team.")
																);
															}
														})
												);
												option("No thanks.", new Dialogue());
											}
										})
								);
								option("Farewell", new Dialogue());
							}
						})
				);
				return;
			}
			e.getPlayer().startConversation(new Dialogue().addNPC(e.getNPCId(), HeadE.CALM_TALK, "You must be on an Iron Man account to access Group IronMan."));
		}
	};

	public static ObjectClickHandler openChest = new ObjectClickHandler(new Object[] { 170 }, new WorldTile[]{new WorldTile(3091, 3493, 0)}) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getPlayer().getBool("Group IronMan")) {
				return;
			}
			e.getPlayer().sendMessage("Blocked");
		}
	};
}
