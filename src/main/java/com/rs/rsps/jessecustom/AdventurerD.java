package com.rs.rsps.jessecustom;

import com.rs.Settings;
import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.util.Utils;

public class AdventurerD extends Conversation {
	int NPC = 1512;
	public AdventurerD(Player player) {
		super(player);
		if(player.getBool("Group IronMan")) {
			if(player.getO("GIM Team") == null || player.getO("GIM Team").toString().equals("")) {
				addPlayer(HeadE.HAPPY_TALKING, "Hi.");
				addNPC(NPC, HeadE.CALM_TALK, "Hey.");
				addNPC(NPC, HeadE.CALM_TALK, "Would you like to join a group or start a group?");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Start a group", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I would like to start a group.")
								.addNPC(NPC, HeadE.CALM_TALK, "Great! Remember, starting a group is irreversible...")
								.addNext(()->{
									player.sendInputName("What group name would you like?", groupNameDisplay -> {
												if(groupNameDisplay.equals("")) {
													player.sendMessage("Invalid group");
													return;
												}
												groupNameDisplay = Utils.formatPlayerNameForProtocol(groupNameDisplay);
												final String groupName = groupNameDisplay;
												if (!WorldDB.getGIMS().groupExists(groupName)) {
													WorldDB.getGIMS().save(new GroupIronMan(groupName, player), () -> {
														player.sendMessage("Group created!");
														player.save("GIM Team", groupName);
													});
													return;
												}
												player.sendMessage("This group name already exists!");
											}
									);
								})
						);
						option("Join a group", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I would like to join a group.")
								.addNPC(NPC, HeadE.CALM_TALK, "Great! Remember, joining a group is reversible...")
								.addNext(()->{
									player.sendInputName("What group would you like to join?", groupNameDisplay -> {
												if(groupNameDisplay.equals("")) {
													player.sendMessage("Invalid group");
													return;
												}
												groupNameDisplay = Utils.formatPlayerNameForProtocol(groupNameDisplay);
												final String groupName = groupNameDisplay;
												if (WorldDB.getGIMS().groupExists(groupName)) {
													WorldDB.getGIMS().getByGroupName(groupName, group -> {
														if(group.getPlayers().size() > 5) {
															player.sendMessage("This group is full...");
															return;
														}
														Player founder = World.getPlayerByUsername(group.getPlayers().get(0));
														founder.getTempAttribs().setB("GIM Join Request_"+player.getUsername(), true);
														player.getTempAttribs().setB("GIM Group Request_"+groupName, true);
														group.broadcastMessage("<col=00FFFF>" + player.getDisplayName() + " wishes to join your group...</col>");
														player.startConversation(
																new Dialogue().addNPC(NPC, HeadE.CALM_TALK, "Have the founder talk to me and neither of you log out.")
														);
													});
													return;
												}
												player.sendMessage("This group doesn't exists!");
											}
									);
								})
						);
					}
				});
				return;
			}
			addPlayer(HeadE.HAPPY_TALKING, "Hi!");
			addNPC(NPC, HeadE.CALM_TALK, "Hey.");
			addNPC(NPC, HeadE.CALM_TALK, "What would you like to do?");
			addOptions("I would like to...", new Options() {
				@Override
				public void create() {
					option("Accept a new member", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I would like to accept a new member please...")
							.addNext(()->{
								player.sendInputName("What player would you like to accept?", displayname -> {
											Player member = World.getPlayerByDisplay(displayname);
											if (member != null && WorldDB.getGIMS().groupExists(player.getO("GIM Team"))
													&& player.getTempAttribs().getB("GIM Join Request_" +member.getUsername())
													&& member.getTempAttribs().getB("GIM Group Request_"+player.getO("GIM Team"))) {
												WorldDB.getGIMS().getByGroupName(player.getO("GIM Team"), group -> {
													group.addPlayer(member);
													player.sendMessage(member.getDisplayName() + " has been added to the group...");
													member.save("GIM Team", group.getGroupName());
													member.sendMessage("<col=00FFFF>You have joined " + group.getGroupName() + "!</col>");
													WorldDB.getGIMS().saveSync(group);
												});
												return;
											}
											player.sendMessage("Either the player is not logged in or there isn't a request!");
										}
								);
							})
					);
					option("What is my group name & members?", new Dialogue()
							.addNPC(NPC, HeadE.CALM_TALK, "Your group name is...")
							.addNPC(NPC, HeadE.CALM_TALK, "\"" + Utils.formatPlayerNameForDisplay(player.getO("GIM Team")) + "\"")
							.addNext(()->{
								WorldDB.getGIMS().getByGroupName(player.getO("GIM Team"), group -> {
									String membersListing = "";
									for(String member : group.getPlayers())
										membersListing = membersListing + "\"" + member + "\"<br>";
									player.startConversation(new Dialogue()
											.addNPC(NPC, HeadE.CALM_TALK, "You group members are...")
											.addNPC(NPC, HeadE.CALM_TALK, membersListing)
									);
								});

							})
					);


					option("What is the XP rate?", new Dialogue()
							.addNPC(NPC, HeadE.CALM_TALK, "The xp rate is " + (Settings.getConfig().getXpRate()))
					);
//								option("Rename my team", new Dialogue()
//										.addNPC(NPC, HeadE.CALM_TALK, "So you want to rename your team do you?")
//										.addNext(()->{
//											player.sendInputName("What group name would you like?", groupNameDisplay -> {
//												WorldDB.getGIMS().getByGroupName(player.getO("GIM Team"), group -> {
//													if(!group.isGroupLeader(player)) {
//														player.sendMessage("Only the group leader can do this...");
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
								player.sendInputName("What player would you like to kick?", displayname -> {
									if (!WorldDB.getGIMS().groupExists(player.getO("GIM Team"))) {
										player.sendMessage("Your group doesn't exist! This is not expected, contact an admin");
										return;
									}
									World.forceGetPlayerByDisplay(displayname, member -> {
										if(groupNamesMatch(member.getO("GIM Team"), player.getO("GIM Team"))) {
											WorldDB.getGIMS().getByGroupName(player.getO("GIM Team"), group -> {
												if(group.getPlayers().size() < 2) {
													player.sendMessage("You can't delete a group entirely!");
													return;
												}
												if(group.isGroupLeader(member)) {
													player.sendMessage("You can't kick the group leader!");
													return;
												}
												int i = group.getPlayers().indexOf(member.getUsername());
												player.getTempAttribs().setI("kick member", i);
												for(String groupMember : group.getPlayers()) {
													Player player = World.getPlayerByUsername(groupMember);
													player.sendMessage(player.getDisplayName() + " wants to kick " + member.getDisplayName());
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
										player.sendMessage(displayname + " is not in the same group as you!");
									});

								});
							})
					);
					option("Leave group", new Dialogue()
							.addOptions("Are you sure you want to leave the group?", new Options() {
								@Override
								public void create() {
									option("No", new Dialogue());
									option("Yes", new Dialogue()
											.addPlayer(HeadE.CALM_TALK, "Yes I want to leave my group...")
											.addNPC(NPC, HeadE.CALM_TALK, "You will have to be accepted again by the leader")
											.addOptions("Are you sure?", new Options() {
												@Override
												public void create() {
													option("Yes.", new Dialogue()
															.addNext(()->{
																WorldDB.getGIMS().getByGroupName(player.getO("GIM Team"), group -> {
																	if(group.getPlayers().size() < 2) {
																		player.sendMessage("You can't delete a group entirely!");
																		return;
																	}
																	if(group.isGroupLeader(player)) {
																		player.sendMessage("You can't leave your founding group!");
																		return;
																	}
																	int i = group.getPlayers().indexOf(player.getUsername());
																	player.delete("GIM Team");
																	group.getPlayers().remove(i);
																	group.broadcastMessage(player.getDisplayName() + " has left group \"" + group.getGroupName() + "\"");
																	player.sendMessage("You left group \"" + group.getGroupName() + "\"");
																	WorldDB.getGIMS().saveSync(group);
																});
															})
													);
													option("No.", new Dialogue());
												}
											})
									);
								}
							})
					);
				}
			});
			return;
		}
		if(player.isIronMan()) {
			addPlayer(HeadE.HAPPY_TALKING, "Hi!");
			addNPC(NPC, HeadE.CALM_TALK, "Hey.");
			addPlayer(HeadE.HAPPY_TALKING, "What is this chest about?");
			addNPC(NPC, HeadE.CALM_TALK, "Oh, it is for Group Iron Man & Woman.");
			addPlayer(HeadE.HAPPY_TALKING, "Oh I see.");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Will you let me join a Group IronMan group?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Will you let me join a Group IronMan group?")
							.addNPC(NPC, HeadE.CALM_TALK, "Sure, however, very specific things will happen if you become a Group Iron Player")
							.addNPC(NPC, HeadE.CALM_TALK, "First, you lose ALL your items!")
							.addNPC(NPC, HeadE.CALM_TALK, "Second, you lose ALL your stats!")
							.addNPC(NPC, HeadE.CALM_TALK, "Third, you lose ALL your quests!")
							.addNPC(NPC, HeadE.CALM_TALK, "Lastly, it's PERMANENT!")
							.addNPC(NPC, HeadE.CALM_TALK, "Essentially you are starting over and starting a team is irreversible.")
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
																resetPlayer(player);
																player.setIronMan(true);
																player.save("Group IronMan", true);
																player.getHintIconsManager().removeAll();
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
			});
			return;
		}
		addNPC(NPC, HeadE.CALM_TALK, "You must be on an Iron Man account to access Group IronMan.");
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
