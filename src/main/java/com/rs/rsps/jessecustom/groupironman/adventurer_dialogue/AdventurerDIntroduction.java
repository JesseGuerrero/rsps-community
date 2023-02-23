package com.rs.rsps.jessecustom.groupironman.adventurer_dialogue;

import com.rs.db.WorldDB;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.rsps.jessecustom.customscape.CustomScape;
import com.rs.rsps.jessecustom.groupironman.GIM;
import com.rs.rsps.jessecustom.groupironman.GroupIronMan;

@PluginEventHandler
public class AdventurerDIntroduction extends Conversation {
	public static int NPC = 1512;
	static String[] ranks = new String[] {"Nubby", "Novice", "Intermediate", "Advanced", "Veteran", "Completionist"};

	public AdventurerDIntroduction(Player player) {
		super(player);
		if(GIM.hasTeam(player) || player.getTempAttribs().getB("HasReadIntroGIM")) {
			startCreatingGIM();
			create();
			return;
		}
		introduceToGIM();
		create();
	}

	private void startCreatingGIM() {
		addPlayer(HeadE.HAPPY_TALKING, "Hello there.");
		addNPC(NPC, HeadE.CALM_TALK, "Greetings! How may I help you?");
		addOptions("What would you like to do?", option -> {
			if(GIM.hasTeam(player)) {
				option.add("What is my group name & members?", groupInformationDialogue(player));
				option.add("Tell me about prestige...", prestigeDialogue(player));
				if(!GIM.isGroupFounder(player))
					option.add("I would like to leave my group.", leaveGroupDialogue(player));
			}
			if(!GIM.hasTeam(player)) {
				option.add("I'd like to start a new group.", startGroupDialogue(player));
				option.add(" I'd like to join a group.", joinGroupDialogue(player));
			}
			if(GIM.isGroupFounder(player)) {
				if(GIM.getGIMUnsyncReadOnly(player).getSize() <= 4)
					option.add("I'd like to accept someone into my group.", acceptPlayerDialogue(player));
				option.add("Kick a member.", kickMemberDialogue(player));
				option.add("I'd like to disband my group.", disbandGroupDialogue(player));
				option.add("Rename team", renameTeamDialogue(player));
			}
		});
	}

	private void introduceToGIM() {
		addPlayer(HeadE.CALM_TALK, "Hello there");
		addNPC(NPC, HeadE.CALM_TALK, "Greetings traveler! I am searching for a quest, nay an adventure!");
		addPlayer(HeadE.HAPPY_TALKING, "Isn't that a little on the nose?");
		addNPC(NPC, HeadE.CALM_TALK, "Huzzah!");
		addNPC(NPC, HeadE.CALM_TALK, " I am seeking an adventure, but only with the bravest souls of Darkan.");
		addPlayer(HeadE.SKEPTICAL_THINKING, "Hmm. Tell me more");
		addNPC(NPC, HeadE.CALM_TALK, "Of course.");
		addNPC(NPC, HeadE.CALM_TALK, "There was an idea, called Group Ironman.");
		addNPC(NPC, HeadE.CALM_TALK, "The idea was to bring together a group of remarkable people, to see if they could become something more.");
		addNPC(NPC, HeadE.CALM_TALK, "To see if they could work together when Darkan needed them to fight the battles that we never could.");
		addNPC(NPC, HeadE.CALM_TALK, "My old comrades died still believing in that idea.");
		addPlayer(HeadE.SKEPTICAL_THINKING, "How did they die?");
		addNPC(NPC, HeadE.CALM_TALK, "Nex, the mother of shadows slayed them all in one blow, and I unfortunately escaped my brotherly death.");
		addPlayer(HeadE.AMAZED, "NEX!? She's impossible to kill!");
		addNPC(NPC, HeadE.CALM_TALK, "Exactly.");
		addNPC(NPC, HeadE.CALM_TALK, "That's why I need the strongest of Darkan to join my adventure and take vengeance to Nex!");
		addNPC(NPC, HeadE.CALM_TALK, "Do you know anyone who'd be worthy of such an adventure?");
		addPlayer(HeadE.HAPPY_TALKING, "I am.");
		addNPC(NPC, HeadE.CALM_TALK, "HAHA! You alone are not worthy.");
		addNPC(NPC, HeadE.CALM_TALK, "True strength comes in numbers, hence the Group Ironman initiative.");
		addNPC(NPC, HeadE.CALM_TALK, "Gather a group of 4 to 5 people and return to me when you're ready to prove yourself.");
		addNPC(NPC, HeadE.CALM_TALK, "Huzzah!");
		addPlayer(HeadE.SECRETIVE, "Okay.", ()->{player.getTempAttribs().setB("HasReadIntroGIM", true);});
	}

	public static Dialogue prestigeDialogue(Player player) {
		if(GIM.getGIMUnsyncReadOnly(player).getSize() < 4)
			return new Dialogue().addNPC(NPC, HeadE.CALM_TALK, "You must gather a team of 4 players to prestige...").addNext(()-> {
				prestigeFunction(player);
			});;
		return new Dialogue().addNext(()-> {
			prestigeFunction(player);
		});
	}

	private static void prestigeFunction(Player player) {
		GIM.openGIM(GIM.getGIMTeamName(player), group -> {
			player.startConversation(new Dialogue()
					.addNPC(NPC, HeadE.CALM_TALK, "Your current rank is " + ranks[group.getPrestigeManager().getPrestige()] + ".")
					.addOptions("Which rank would you like to inquire?", rankOption -> {
						rankOption.add("Novice", new Dialogue().addNext(() -> {
							RanksInterfaces.noviceInterface(player);
							group.getPrestigeManager().refreshPrestige(0);
						}));
						rankOption.add("Intermediate", new Dialogue().addNext(() -> {
							RanksInterfaces.intermediateInterface(player);
							group.getPrestigeManager().refreshPrestige(1);
						}));
						rankOption.add("Advanced", new Dialogue().addNext(() -> {
							RanksInterfaces.advancedInterface(player);
							group.getPrestigeManager().refreshPrestige(2);
						}));
						rankOption.add("Veteran", new Dialogue().addNext(() -> {
							RanksInterfaces.veteranInterface(player);
							group.getPrestigeManager().refreshPrestige(3);
						}));
						rankOption.add("Completionist", new Dialogue().addNext(() -> {
							RanksInterfaces.completionistInterface(player);
							group.getPrestigeManager().refreshPrestige(4);
						}));
					}));
		});
	}

	public static Dialogue groupInformationDialogue(Player player) {
		return new Dialogue()
				.addNPC(NPC, HeadE.CALM_TALK, "Your group name is...")
				.addNPC(NPC, HeadE.CALM_TALK, "\"" + Utils.formatPlayerNameForDisplay(player.getO("GIM Team")) + "\"")
				.addNext(()->{
					WorldDB.getGIMS().getByGroupName(GIM.getGIMTeamName(player), group -> {
						String membersListing = "";
						for(String member : group.getPlayers())
							membersListing = membersListing + "\"" + member + "\"<br>";
						player.startConversation(new Dialogue()
								.addNPC(NPC, HeadE.CALM_TALK, "You group members are...")
								.addNPC(NPC, HeadE.CALM_TALK, membersListing)
						);
					});

			});
	}

	public static Dialogue startGroupDialogue(Player player) {
		return new Dialogue()
				.addNPC(NPC, HeadE.CALM_TALK, "Perfect! What would you like to call your group?")
				.addNext(()->{
							player.sendInputName("What group name would you like?", groupNameDisplay -> {
										if(groupNameDisplay.equals("")) {
											player.sendMessage("Invalid group");
											return;
										}
										groupNameDisplay = Utils.formatPlayerNameForProtocol(groupNameDisplay);
										final String groupName = groupNameDisplay;
										if (!WorldDB.getGIMS().groupExists(groupName)) {
											GroupIronMan group = new GroupIronMan(groupName, player);
											group.getPrestigeManager().setPrestige(GIM.getIndividualPrestige(player));
											WorldDB.getGIMS().save(group, () -> {
												player.sendMessage("<col=00FF00>You are no longer xp locked...");
												player.save("GIM Team", groupName);
												player.setXpLocked(false);
												player.startConversation(new Dialogue()
														.addNPC(NPC, HeadE.CALM_TALK, "Excellent! I couldn't think of a better one.")
														.addNPC(NPC, HeadE.CALM_TALK, "Scout at least 4 players to join you, then speak to me again to begin the adventure.")
												);
											});
											return;
										}
										player.sendMessage("This group name already exists!");
									}
							);
						}
				);
	}

	public static Dialogue acceptPlayerDialogue(Player player) {
		if(GIM.getGIMUnsyncReadOnly(player).getSize() >= 5)
			return new Dialogue().addNPC(NPC, HeadE.CALM_TALK, "Your party is full...");
		return new Dialogue()
				.addPlayer(HeadE.HAPPY_TALKING, "I would like to accept a new member please...")
				.addNext(()->{
					player.sendInputName("What player would you like to accept?", displayname -> {
								Player member = World.getPlayerByDisplay(displayname);
								if (member != null && WorldDB.getGIMS().groupExists(player.getO("GIM Team"))
										&& player.getTempAttribs().getB("GIM Join Request_" +member.getUsername())
										&& member.getTempAttribs().getB("GIM Group Request_"+player.getO("GIM Team"))) {
									WorldDB.getGIMS().getByGroupName(player.getO("GIM Team"), group -> {
										if(group.isCustomScape() != CustomScape.isPlayerCustomScape(member)) {
											member.sendMessage("The prospective member must" + (group.isCustomScape() ? " " : " not ") + "be a custom scaper...");
											player.sendMessage("The prospective member must" + (group.isCustomScape() ? " " : " not ") + "be a custom scaper...");
											return;
										}
										group.addPlayer(member);
										player.sendMessage(member.getDisplayName() + " has been added to the group...");
										member.save("GIM Team", group.getGroupName());
										member.sendMessage("<col=00FFFF>You have joined " + group.getGroupName() + "!</col>");
										member.setXpLocked(false);
										member.sendMessage("<col=00FF00>You are no longer xp locked...");
										WorldDB.getGIMS().saveSync(group);
									});
									return;
								}
								player.sendMessage("Either the player is not logged in or there isn't a request!");
							}
					);
				});
	}

	public static Dialogue disbandGroupDialogue(Player player) {
		return new Dialogue()
				.addNPC(NPC, HeadE.CALM_TALK, "Are you certain?")
				.addOptions("Disband group?", confirm -> {
					confirm.add("No");
					confirm.add("Yes", () -> {
						GIM.removeGIM(GIM.getGIMTeamName(player));
						player.sendMessage("Group disbanded...");
						player.setXpLocked(true);
						player.sendMessage("<col=FF0000><shad=000000>You are XP locked until you are in a team...");
					});
				});
	}

	public static Dialogue joinGroupDialogue(Player player) {
		return new Dialogue()
				.addPlayer(HeadE.HAPPY_TALKING, "I would like to join a group.")
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
										int groupPrestige = group.getPrestigeManager().getPrestige();
										if(groupPrestige != GIM.getIndividualPrestige(player)) {
											player.sendMessage("You must be " + ranks[groupPrestige] + " to join this group...");
											return;
										}
										Player founder = World.getPlayerByUsername(group.getFounderUsername());
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
				});
	}

	public static Dialogue leaveGroupDialogue(Player player) {
		return new Dialogue()
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
													WorldDB.getGIMS().getByGroupName(GIM.getGIMTeamName(player), group -> {
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
														player.setXpLocked(true);
														player.sendMessage("<col=FF0000><shad=000000>You are XP locked until you are in a team...");
													});
												})
										);
										option("No.", new Dialogue());
									}
								})
						);
					}
				});
	}

	public static Dialogue kickMemberDialogue(Player player) {
		return new Dialogue()
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
										Player p = World.getPlayerByUsername(groupMember);
										p.sendMessage(p.getDisplayName() + " wants to kick " + member.getDisplayName());
										if(p.getUsername() != member.getUsername() && p.getTempAttribs().getI("kick member") != i)
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
				});
	}

	public static Dialogue renameTeamDialogue(Player player) {
		return new Dialogue()
				.addPlayer(HeadE.HAPPY_TALKING, "I would like to rename the team...")
				.addNPC(NPC, HeadE.CALM_TALK, "Sure, what would you like?")
				.addNext(()->{
					player.sendInputName("What group name would you like?", groupNameDisplay -> {
						GIM.openGIM(GIM.getGIMTeamName(player), group -> {
							String groupName = Utils.formatPlayerNameForProtocol(groupNameDisplay);
							group.setGroupName(groupName);
							player.sendMessage("The group has been renamed to " + Utils.formatPlayerNameForDisplay(groupName));
						});
					});
				});
	}

	private static boolean groupNamesMatch(String group1, String group2) {
		return Utils.formatPlayerNameForProtocol(group1).equals(Utils.formatPlayerNameForProtocol(group2));
	}
}
