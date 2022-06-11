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
	private static boolean isMaxedSilent(Player player) {
		boolean maxed = true;
		for (int i = 0; i < 25; i++)
			if (player.getSkills().getLevelForXp(i) < 99)
				maxed = false;
		if (player.getSkills().getLevelForXp(Constants.DUNGEONEERING) < 120)
			maxed = false;
		return maxed;
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
											.addNPC(NPC, HeadE.CALM_TALK, "Great! Remember, starting a group is irreversible...")
											.addNext(()->{
												e.getPlayer().sendInputName("What group name would you like?", groupNameDisplay -> {
															groupNameDisplay = Utils.formatPlayerNameForProtocol(groupNameDisplay);
															final String groupName = groupNameDisplay;
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
											.addNPC(NPC, HeadE.CALM_TALK, "Great! Remember, joining a group is reversible...")
											.addNext(()->{
												e.getPlayer().sendInputName("What group would you like to join?", groupNameDisplay -> {
															groupNameDisplay = Utils.formatPlayerNameForProtocol(groupNameDisplay);
															final String groupName = groupNameDisplay;
															if (WorldDB.getGIMS().groupExists(groupName)) {
																WorldDB.getGIMS().getByGroupName(groupName, group -> {
																	if(group.getPlayers().size() > 5) {
																		e.getPlayer().sendMessage("This group is full...");
																		return;
																	}
																	Player founder = World.getPlayerByUsername(group.getPlayers().get(0));
																	founder.getTempAttribs().setB("GIM Join Request_"+e.getPlayer().getUsername(), true);
																	e.getPlayer().getTempAttribs().setB("GIM Group Request_"+groupName, true);
																	group.broadcastMessage("<col=00FFFF>" + e.getPlayer().getDisplayName() + " wishes to join your group...</col>");
																	e.getPlayer().startConversation(
																			new Dialogue().addNPC(NPC, HeadE.CALM_TALK, "Have the founder talk to me and neither of you log out.")
																	);
																});
																return;
															}
															e.getPlayer().sendMessage("This group doesn't exists!");
														}
												);
											})
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
										.addPlayer(HeadE.HAPPY_TALKING, "I would like to accept a new member please...")
										.addNext(()->{
											e.getPlayer().sendInputName("What player would you like to accept?", displayname -> {
												Player member = World.getPlayerByDisplay(displayname);
														if (member != null && WorldDB.getGIMS().groupExists(e.getPlayer().getO("GIM Team"))
																&& e.getPlayer().getTempAttribs().getB("GIM Join Request_" +member.getUsername())
																&& member.getTempAttribs().getB("GIM Group Request_"+e.getPlayer().getO("GIM Team"))) {
															WorldDB.getGIMS().getByGroupName(e.getPlayer().getO("GIM Team"), group -> {
																if(group.getPrestige() < 0)
																	group.setPrestige(0);
																if(group.getPrestige() != member.getI("prestige", 0)) {
																	group.broadcastMessage(member.getDisplayName() + " has a prestige of "
																			+ member.getI("prestige", 0) + " while your group has a prestige of "
																			+ group.getPrestige() + " and cannot join.");
																	return;
																}
																group.addPlayer(member);
																e.getPlayer().sendMessage(member.getDisplayName() + " has been added to the group...");
																member.save("GIM Team", group.getGroupName());
																member.sendMessage("<col=00FFFF>You have joined " + group.getGroupName() + "!</col>");
																WorldDB.getGIMS().saveSync(group);
															});
															return;
														}
														e.getPlayer().sendMessage("Either the player is not logged in or there isn't a request!");
													}
											);
										})
								);
								option("What is my group name & members?", new Dialogue()
										.addNPC(NPC, HeadE.CALM_TALK, "Your group name is...")
										.addNPC(NPC, HeadE.CALM_TALK, "\"" + Utils.formatPlayerNameForDisplay(e.getPlayer().getO("GIM Team")) + "\"")
										.addNext(()->{
											WorldDB.getGIMS().getByGroupName(e.getPlayer().getO("GIM Team"), group -> {
												String membersListing = "";
												for(String member : group.getPlayers())
													membersListing = membersListing + "\"" + member + "\"<br>";
												e.getPlayer().startConversation(new Dialogue()
														.addNPC(NPC, HeadE.CALM_TALK, "You group members are...")
														.addNPC(NPC, HeadE.CALM_TALK, membersListing)
												);
											});

										})
								);

								if(isMaxedSilent(e.getPlayer())) {
									option("I'd like to prestige", new Dialogue()
											.addNPC(NPC, HeadE.CALM_TALK, "Wow!")
											.addPlayer(HeadE.HAPPY_TALKING, "What??")
											.addNPC(NPC, HeadE.CALM_TALK, "You are epic!")
											.addPlayer(HeadE.HAPPY_TALKING, "Thanks.")
											.addNPC(NPC, HeadE.CALM_TALK, "Great, if all of you come to me and say you want to prestige I will do it.")
											.addPlayer(HeadE.HAPPY_TALKING, "Then, I want to prestige please.")
											.addNPC(NPC, HeadE.CALM_TALK, "Okay, I will note you down to prestige. Don't logout.")
											.addNext(()->{
												e.getPlayer().getTempAttribs().setB("wants to prestige", true);
												if(WorldDB.getGIMS().groupExists(e.getPlayer().getO("GIM Team"))) {
													WorldDB.getGIMS().getByGroupName(e.getPlayer().getO("GIM Team"), group -> {
														boolean canPrestige = true;
														for(String username : group.getPlayers()) {
															Player p = World.getPlayerByUsername(username);
															p.sendMessage(e.getPlayer().getDisplayName() + " wants to prestige...");
															if(p == null || !p.getTempAttribs().getB("wants to prestige")) {
																canPrestige = false;
															}
														}
														if(canPrestige) {
															for(String username : group.getPlayers()) {
																Player p = World.getPlayerByUsername(username);
																p.save("prestige", p.getI("prestige", 0) + 1);
																for (int skill = 0; skill < 25; skill++)
																	p.getSkills().setXp(skill, 0);
																p.getSkills().init();
																p.sendMessage("<col=00FFFF>You have prestiged!</col>");
															}
														}

													});
												} else
													e.getPlayer().sendMessage("Your team doesn't exist, contact an admin...");
											})
									);

								}

								option("What is my prestige and XP rate?", new Dialogue()
										.addNPC(NPC, HeadE.CALM_TALK, "Your prestige is " + String.valueOf(e.getPlayer().getI("prestige", 0))
												+ " and your xp rate is " + String.valueOf((Settings.getConfig().getXpRate()/(e.getPlayer().getI("prestige") > 0 ? (e.getPlayer().getI("prestige") + 1) : (1)))))
								);

								option("Tell me about prestige.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I heard a rumor there is a group prestige?")
										.addNPC(NPC, HeadE.CALM_TALK, "Yes, you can prestige your group to get rewards.")
										.addNPC(NPC, HeadE.CALM_TALK, "You will be able to do it here after your entire team says they want to do it.", () -> {
											e.getPlayer().getSkills().isMaxed(true);
										})
								);
//								option("Rename my team", new Dialogue()
//										.addNPC(NPC, HeadE.CALM_TALK, "So you want to rename your team do you?")
//										.addNext(()->{
//											e.getPlayer().sendInputName("What group name would you like?", groupNameDisplay -> {
//												WorldDB.getGIMS().getByGroupName(e.getPlayer().getO("GIM Team"), group -> {
//													if(!group.isGroupLeader(e.getPlayer())) {
//														e.getPlayer().sendMessage("Only the group leader can do this...");
//														return;
//													}
//													String groupName = Utils.formatPlayerNameForProtocol(groupNameDisplay);
//													group.setGroupName(groupName);
//													WorldDB.getGIMS().saveSync(group);
//												});
//											});
//										})
//								);
								option("Kick a member.", new Dialogue()
										.addNext(()->{
											e.getPlayer().sendInputName("What player would you like to kick?", displayname -> {
												if (!WorldDB.getGIMS().groupExists(e.getPlayer().getO("GIM Team"))) {
													e.getPlayer().sendMessage("Your group doesn't exist! This is not expected, contact an admin");
													return;
												}
												World.forceGetPlayerByDisplay(displayname, member -> {
													if(groupNamesMatch(member.getO("GIM Team"), e.getPlayer().getO("GIM Team"))) {
														WorldDB.getGIMS().getByGroupName(e.getPlayer().getO("GIM Team"), group -> {
															if(group.getPlayers().size() < 2) {
																e.getPlayer().sendMessage("You can't delete a group entirely!");
																return;
															}
															if(group.isGroupLeader(member)) {
																e.getPlayer().sendMessage("You can't kick the group leader!");
																return;
															}
															int i = group.getPlayers().indexOf(member.getUsername());
															e.getPlayer().getTempAttribs().setI("kick member", i);
															for(String groupMember : group.getPlayers()) {
																Player player = World.getPlayerByUsername(groupMember);
																player.sendMessage(e.getPlayer().getDisplayName() + " wants to kick " + member.getDisplayName());
																if(player.getUsername() != member.getUsername() && player.getTempAttribs().getI("kick member") != i)
																	return;
															}


															member.delete("GIM Team");
															group.getPlayers().remove(member.getUsername());
															group.broadcastMessage(member.getDisplayName() + " has left group \"" + group.getGroupName() + "\"");
															member.sendMessage("You left group \"" + group.getGroupName() + "\"");
															WorldDB.getGIMS().saveSync(group);
														});
														return;
													}
													e.getPlayer().sendMessage(displayname + " is not in the same group as you!");
												});

											});
										})
										.addNext(()->{
											e.getPlayer().getTempAttribs().setB("wants to prestige", true);
											if(WorldDB.getGIMS().groupExists(e.getPlayer().getO("GIM Team"))) {
												WorldDB.getGIMS().getByGroupName(e.getPlayer().getO("GIM Team"), group -> {
													boolean canPrestige = true;
													for(String username : group.getPlayers()) {
														Player p = World.getPlayerByUsername(username);
														p.sendMessage(e.getPlayer().getDisplayName() + " wants to prestige...");
														if(p == null || !p.getTempAttribs().getB("wants to prestige")) {
															canPrestige = false;
														}
													}
													if(canPrestige) {
														for(String username : group.getPlayers()) {
															Player p = World.getPlayerByUsername(username);
															p.save("prestige", p.getI("prestige", 0) + 1);

															for (int skill = 0; skill < 25; skill++)
																p.getSkills().setXp(skill, 0);
															p.getSkills().init();
															p.sendMessage("<col=00FFFF>You have prestiged!</col>");
														}
														if(group.getPrestige() < 0 )
															group.setPrestige(0);
														group.setPrestige(group.getPrestige() + 1);
													}

												});
											} else
												e.getPlayer().sendMessage("Your team doesn't exist, contact an admin...");
										})
								);
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
																			e.getPlayer().getHintIconsManager().removeAll();
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

	public static LoginHandler updateHintGIM = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if(!e.getPlayer().getBool("Group IronMan")) {
				if(e.getPlayer().getRegionId() == 12342) {
					for(NPC npc : World.getNPCsInRegion(12342))
						if(npc.getId() == 1512)
							e.getPlayer().getHintIconsManager().addHintIcon(6, npc, 0, -1, true);
				}
			}
		}
	};

	public static ObjectClickHandler openChest = new ObjectClickHandler(new Object[] { 170 }, new WorldTile[]{new WorldTile(3091, 3493, 0)}) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getBool("Group IronMan")) {
				if (e.getPlayer().getO("GIM Team") == null) {
					e.getPlayer().sendMessage("You need to be part of a group to access a shared bank...");
					return;
				}
				e.getPlayer().getBank().open();
			}
		}
	};
}
