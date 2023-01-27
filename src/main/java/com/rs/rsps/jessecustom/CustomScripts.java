package com.rs.rsps.jessecustom;

import com.rs.Settings;
import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.Toolbelt;
import com.rs.game.content.skills.magic.LodestoneAction;
import com.rs.game.content.skills.slayer.Master;
import com.rs.game.engine.command.Commands;
import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.Dialogue;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.engine.dialogue.Options;
import com.rs.game.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.rsps.jessecustom.customscape.CustomScape;
import com.rs.rsps.jessecustom.groupironman.GIM;

import static com.rs.game.content.skills.slayer.ReaperAssignments.talkAboutAssignment;

@PluginEventHandler
public class CustomScripts {


	public static Master highestSlayerMaster(Player player, Master master) {
		if (player.withinDistance(WorldTile.of(3161, 3461, 0))) {
			for (int i = Master.values().length - 1; i >= 0; i--) {
				if (player.getSkills().getCombatLevelWithSummoning() >= Master.values()[i].requiredCombatLevel
						&& player.getSkills().getLevelForXp(Constants.SLAYER) >= Master.values()[i].reqSlayerLevel) {
					return Master.values()[i];
				}
			}
		}
		return master;
	}







	public static void reaperRewardsConversation(Player player) {
		player.startConversation(new Dialogue()
				.addOptions("Choose your rewards", reward -> {
					reward.add("Coin accumulator(250 points)", new Dialogue()
							.addNPC(15661, HeadE.CALM, "I have a coin accumulator which picks up coins?")
							.addOptions("Would you like to have the coin accumulator?", confirm -> {
								confirm.add("Nevermind", new Dialogue());
								if(player.reaperPoints >= 250)
									confirm.add("Yes, I would like that", new Dialogue().addNext(()->{
										if(player.getInventory().hasFreeSlots()) {
											player.reaperPoints -= 250;
											player.getInventory().addItem(25351);
											return;
										}
										player.sendMessage("Your inventory is full...");
									}));
								else
									confirm.add("Yes, but I don't have enough reaper points", new Dialogue());
							})

					);
				})
		);
	}

	public static void customDebugCommands() {
		Commands.add(Rights.PLAYER, "rights", "Completes all quests.", (p, args) -> {
			p.sendMessage("Rights: " + p.getRights());
		});

		Commands.add(Rights.PLAYER, "getprestige", "Completes all quests.", (p, args) -> {
			GIM.openGIM(GIM.getGIMTeamName(p), group -> {
				p.sendMessage(group.getPrestigeManager().getPrestige() + " prestige.");
			});
		});

		Commands.add(Rights.PLAYER, "addadvancedcounts", "Completes all quests.", (p, args) -> {
			p.incrementCount("Reaper assignments completed", 101);
			p.incrementCount("Pest control games completed", 301);
			p.incrementCount("Fight Caves clears", 2);
			p.incrementCount("Fight Kiln clears", 2);
			p.sendMessage("added counts");
		});

		Commands.add(Rights.PLAYER, "resetadvancedcounts", "Completes all quests.", (p, args) -> {
			p.save("Reaper assignments completed", 0);
			p.save("Pest control games completed", 0);
			p.save("Fight Caves clears", 0);
		});

		Commands.add(Rights.PLAYER, "setprestige [prestige]", "Completes all quests.", (p, args) -> {
			GIM.openGIM(GIM.getGIMTeamName(p), group -> {
				group.getPrestigeManager().setPrestige(Integer.valueOf(args[0]));
				p.sendMessage("Set prestige to " + args[0]);
			});

		});

		Commands.add(Rights.PLAYER, "iscaled [itemId scale]", "Spawns an item with specified id and scaling.", (p, args) -> {
			if (ItemDefinitions.getDefs(Integer.valueOf(args[0])).getName().equals("null")) {
				p.sendMessage("That item is unused.");
				return;
			}
			Item item = new Item(Integer.valueOf(args[0]), 1);
			item.addMetaData("AttackBonus", Double.valueOf(args[1]));
			item.addMetaData("StrengthBonus", Double.valueOf(args[1]));
			p.getInventory().addItem(item);
			p.stopAll();
		});

//		Commands.add(Rights.PLAYER, "kq [scale]", "Start qbd", (p, args) -> {
//			p.getControllerManager().startController(new KalphiteQueenScalingInstanceController());
//		});
//
//		Commands.add(Rights.PLAYER, "bando [scale]", "Start qbd", (p, args) -> {
//			p.getControllerManager().startController(new GeneralGraardorScalingInstanceController());
//		});
//
//		Commands.add(Rights.PLAYER, "corp [scale]", "Start qbd", (p, args) -> {
//			p.getControllerManager().startController(new CorporealBeastScalingInstanceController());
//		});
//
//		Commands.add(Rights.PLAYER, "zammy [scale]", "Start qbd", (p, args) -> {
//			p.getControllerManager().startController(new KrilTstsarothScalingInstanceController());
//		});
//
//		Commands.add(Rights.PLAYER, "arma [scale]", "Start qbd", (p, args) -> {
//			p.getControllerManager().startController(new KreeArraScalingInstanceController());
//		});
//
//		Commands.add(Rights.PLAYER, "sara [scale]", "Start qbd", (p, args) -> {
//			p.getControllerManager().startController(new CommanderZilyanaScalingInstanceController());
//		});

		Commands.add(Rights.PLAYER, "ipeek [itemId]", "Spawns an item with specified id and scaling.", (p, args) -> {
			if (ItemDefinitions.getDefs(Integer.valueOf(args[0])).getName().equals("null")) {
				p.sendMessage("That item is unused.");
				return;
			}
			Item item = new Item(Integer.valueOf(args[0]), 1);
			int[] bonuses = item.getDefinitions().bonuses;
			int defenseAvg = (bonuses[Bonus.STAB_DEF.ordinal()] + bonuses[Bonus.SLASH_DEF.ordinal()] + bonuses[Bonus.CRUSH_DEF.ordinal()]
					+ bonuses[Bonus.RANGE_DEF.ordinal()]) / 4;
			int attackAvg = (bonuses[Bonus.STAB_ATT.ordinal()] + bonuses[Bonus.SLASH_ATT.ordinal()] + bonuses[Bonus.CRUSH_ATT.ordinal()]
					 + bonuses[Bonus.RANGE_ATT.ordinal()]) / 4;
			int strengthAvg = bonuses[Bonus.MELEE_STR.ordinal()] + bonuses[Bonus.RANGE_STR.ordinal()] / 2;
			int magicDefenseAvg = bonuses[Bonus.MAGIC_DEF.ordinal()];
			int magicAttackAvg = bonuses[Bonus.MAGIC_ATT.ordinal()];
			p.sendMessage("Def avg: " + defenseAvg + "Str avg: " + strengthAvg + " Att avg: " + attackAvg
					+ " Mag def avg: " + magicDefenseAvg + " Mag att avg: " + magicAttackAvg);
		});
	}



	public static ItemOnItemHandler handleMakeVineWhip = new ItemOnItemHandler(new int[]{21369}, new int[]{4151}, e-> {
		if(e.getItem1().getId() == 4151)
			e.getItem1().setId(21371);
		if(e.getItem2().getId() == 4151)
			e.getItem2().setId(21371);
		e.getPlayer().getInventory().deleteItem(21369, 1); // WHIP VINE
		e.getPlayer().getInventory().refresh();
	});




	public static void handleWhipSplit(Player p, Item item) {
		if (p.getInventory().getFreeSlots() >= 1) {
			item.setId(4151);
			p.getInventory().addItem(21369);
			p.sendMessage("You split the vine from the whip.");
			p.getInventory().refresh();
		} else
			p.sendMessage("Not enough space in your inventory.");
	}



	public static int getTokenReward(double totalXp) {
		return (int) ((totalXp / 10.0) * 10);
	}

	public static double increaseByXPRateForInterfaceDungeoneering(double xp) {
		xp *= Settings.getConfig().getXpRate();
		return xp;
	}

	public static int getLargeDungPartSizeRequirement() {
		return 1;
	}
//
	public static int bossHPMultiplier() {
		return 13;
	}

	public static int DungNPCMultiplier() {
		return 6;
	}



	public static LoginHandler onLoginUpdateGIMQuests = new LoginHandler(e -> {
		if(GIM.isGIM(e.getPlayer()) && (boolean)e.getPlayer().get("resetQuestsGIM") == false) {
			e.getPlayer().save("resetQuestsGIM", true);
			for (Quest quest : Quest.values())
				if (quest.isImplemented())
					e.getPlayer().getQuestManager().resetQuest(quest);
		}
	});





	public static boolean completeTreasureTrail(Player player, int level, Item item) {
		if(level == -1)
			return false;
		player.getInventory().deleteItem(item.getId(), 1);
		player.getTreasureTrailsManager().openReward(level);
		return true;
	}


	public static int slayerPointsMultiplier(int amount) {
		return amount*3;
	}



	public static boolean chargesLostOnDeath() {
		return false;
	}

	public static World.DropMethod untradeablesDropNormal() {
		return World.DropMethod.NORMAL;
	}

	public static boolean deathCofferIsSuccessful(Player player) {
//		if(player.isIronMan()) {
//			player.setNextWorldTile(Settings.getConfig().getPlayerRespawnTile());
//			player.lock(3);
//			player.startConversation(new Dialogue().addNPC(15661, HeadE.CALM, "Given you have earned everything yourself(iron) you get a free pass...").addPlayer(HeadE.HAPPY_TALKING, "Thanks!"));
//			return true;
//		}
		if(player.getI("death coffer") < 10_000) {
			player.sendMessage("You didn't have enough in your coffer");
			return false;
		}
		if(player.getI("death coffer") >= 10_000) {
			player.save("death coffer", player.getI("death coffer") - 10_000);
			player.setNextWorldTile(Settings.getConfig().getPlayerRespawnTile());
			player.lock(3);
			player.startConversation(new Dialogue().addNPC(15661, HeadE.CALM, "This has cost you 10K coins").addPlayer(HeadE.HAPPY_TALKING, "Thanks!"));
			return true;
		}
		return false;
	}

	public static Conversation customRewardTraderConversations(Player p, int NPC) {
		return new Conversation(p) {
			{
				addNPC(NPC, HeadE.CALM_TALK, "Oh, hello, I didn't see...");
				addPlayer(HeadE.HAPPY_TALKING, "Hey. I was wondering if you could help me?");
				addNPC(NPC, HeadE.CALM_TALK, "Help? Uh... I'm not sure that I can... uh...");
				addPlayer(HeadE.HAPPY_TALKING, "What is that in your pocket?");
				addNPC(NPC, HeadE.CALM_TALK, "I'm not sure, its some kind of imp...");
				addItem(25350, "He pulls out the imp and shows it to you...");
				addPlayer(HeadE.AMAZED_MILD, "I have seen these!");
				addNPC(NPC, HeadE.CALM_TALK, "You want it so bad, you know what I will give it for 50k Dungeoneering Token...");
				addOptions("Pay 50k tokens for charming imp?", options -> {
					options.add("No thank you...", new Dialogue());
					if(p.getDungManager().getTokens() < 50_000)
						options.add("I don't have enough tokens!", new Dialogue());
					if(p.getDungManager().getTokens() >= 50_000) {
						if(!p.getInventory().hasFreeSlots())
							options.add("I don't have enough space!", new Dialogue());
						if(p.getInventory().hasFreeSlots())
							options.add("Okay I will take it", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Okay I will take it!")
								.addNPC(NPC, HeadE.CALM_TALK, "Sounds good...")
								.addSimple("He gives you the charming imp", ()->{
									p.getInventory().addItem(25350, 1);
									p.getDungManager().removeTokens(50_000);
								}));
					}
				});
				create();
			}
		};
	}

	public static double getXPRate(Player player, int skill, double exp) {
		if (Settings.getConfig().getXpRate() > 1 ) {
			if(CustomScape.isPlayerCustomScape(player)) //Don't multiply xp rate past 99
				if(player.getSkills().getLevelForXp(skill) < 99) {
					exp *= Settings.getConfig().getXpRate();
					return exp;
				}
			if(GIM.isGIM(player)) {
				exp *= Settings.getConfig().getXpRate();
				return exp;
			}
		}
		return exp;
	}

	public static int dungBossCombatMultiplier() {
		return 1;
	}

	public static void sendWeakenGeomancer(Player player, int skill) {
		player.getSkills().set(skill, (int) (player.getSkills().getLevelForXp(skill) * .95));
	}

	public static boolean updateRightsOnRun(Player p) {
		if(p.getUsername().equals(Settings.getConfig().getOwnerName()))
			p.setRights(Rights.OWNER);
		return false;
	}

	public static boolean isInfiniteLogout() {
		return true;
	}

	public static Options getDeathsOptions(Player player) {
		return new Options() {
			@Override
			public void create() {
				option("I need an assignment.", () -> {
					talkAboutAssignment(player);
				});

				option("I'd like another grim gem.", new Dialogue()
						.addItem(24806, "You receive a grim gem.", () -> {
							player.getInventory().addItem(24806, 1);
						}));

				option("Are there any rewards for this?", new Dialogue()
						.addNext(()-> {reaperRewardsConversation(player);}));
				if (player.getInventory().getAmountOf(995) >= 10_000)
					option("I would like to add to my death coffer...", new Dialogue().addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							option("10k", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I would like to add 10k.")
									.addNext(() -> {
										player.getInventory().deleteItem(995, 10_000);
										player.save("death coffer", player.getI("death coffer") + 10_000);
									})
							);
							if (player.getInventory().getAmountOf(995) >= 100_000)
								option("100k", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I would like to add 100k.")
										.addNext(() -> {
											player.getInventory().deleteItem(995, 100_000);
											player.save("death coffer", player.getI("death coffer") + 100_000);
										})
								);
							if (player.getInventory().getAmountOf(995) >= 1_000_000)
								option("1M", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I would like to add 1M.")
										.addNext(() -> {
											player.getInventory().deleteItem(995, 1_000_000);
											player.save("death coffer", player.getI("death coffer") + 1_000_000);
										})
								);
							if (player.getInventory().getAmountOf(995) >= 10_000_000)
								option("10M", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I would like to add 10M.")
										.addNext(() -> {
											player.getInventory().deleteItem(995, 10_000_000);
											player.save("death coffer", player.getI("death coffer") + 10_000_000);
										})
								);
						}
					}));
				option("How much is in my death coffer?", new Dialogue()
						.addNPC(15661, HeadE.CALM, "Speak to me about my death coffers once you have at least 10k coins, however in your coffer " +
								"you currently have " + player.getI("death coffer") + " coins...")
				);
			}
		};
	}

	public static boolean dontConvertItemsOnDeathTileDrop() {
		return true;
	}

	/**
	 * in givestarter
	 * @param p
	 */
	public static void giveToolsLoadestones(Player p) {
		for (LodestoneAction.Lodestone stone : LodestoneAction.Lodestone.values()) {
			p.unlockLodestone(stone, null);
		}
		p.addToolbelt(1265);
		for (Toolbelt.Tools tool : Toolbelt.Tools.values()) {
			if (tool == null)
				continue;
			if (p.getToolbelt().get(tool) != null)
				continue;
			p.getToolbelt().put(tool, 1);
		}
		Toolbelt.refreshToolbelt(p);
	}

	public static ItemOnNPCHandler handleDeathOptions = new ItemOnNPCHandler(true, new Object[] { 15661 }, e -> {
		int NPC = e.getNPC().getId();
		Player player = e.getPlayer();
		Item weapon = e.getItem();

		if (weapon.getMetaData("HitBonus") == null) {
			player.startConversation(new Dialogue().addNPC(NPC, HeadE.CALM_TALK, "Your weapon must first taste death before taking on a name..."));
			return;
		}

		if(weapon.getMetaData("WeaponName") != null) {
			player.startConversation(new Dialogue()
					.addNPC(NPC, HeadE.CALM_TALK, "Mortal, you have already named this weapon..."));
			return;
		}

		if (weapon.getMetaData("HitBonus") != null && weapon.getMetaData("WeaponName") == null)
			player.startConversation(new Dialogue()
					.addNPC(NPC, HeadE.CALM_TALK, "I see you have a weapon of death...")
					.addPlayer(HeadE.EVIL_LAUGH, "Yes I do, thank you very much.")
					.addPlayer(HeadE.CALM_TALK, "What of it?")
					.addNPC(NPC, HeadE.CALM_TALK, "I can name your weapon once and forever that will be its brand...")
					.addOptions("Name your weapon?", options -> {
						options.add("No");
						options.add("Yes", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Yes, ill name it")
								.addNext(()->{
									player.sendInputName("What name for your weapon?", name -> {
										player.startConversation(new Dialogue()
												.addNPC(NPC, HeadE.CALM_TALK, "Are you sure you want \"" + name + "\"?")
												.addOptions("Set name to \"" + name + "\"?", option -> {
													option.add("No");
													option.add("Yes", () -> {
														weapon.setMetaDataO("WeaponName", name);
														player.sendMessage("Your weapon is now named \"" + name + "\"...");
													});
												})
										);

									});
								})
						);
					})
			);
	});





	public static boolean removeCorpStatReset() {
		return false;
	}
}
