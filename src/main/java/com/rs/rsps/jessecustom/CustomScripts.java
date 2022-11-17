package com.rs.rsps.jessecustom;

import com.rs.Settings;
import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.ItemConstants;
import com.rs.game.content.Toolbelt;
import com.rs.game.content.commands.Commands;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.handlers.shieldofarrav.ShieldOfArrav;
import com.rs.game.content.skills.slayer.Master;
import com.rs.game.model.entity.actions.LodestoneAction;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
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
import com.rs.rsps.jessecustom.bosses.kalphitequeen.KalphiteQueenScalingInstanceController;

import static com.rs.game.content.skills.slayer.ReaperAssignments.talkAboutAssignment;

@PluginEventHandler
public class CustomScripts {
	public static Master highestSlayerMaster(Player player, Master master) {
		if (player.withinDistance(new WorldTile(3161, 3461, 0))) {
			for (int i = Master.values().length - 1; i >= 0; i--) {
				if (player.getSkills().getCombatLevelWithSummoning() >= Master.values()[i].requiredCombatLevel
						&& player.getSkills().getLevelForXp(Constants.SLAYER) >= Master.values()[i].reqSlayerLevel) {
					return Master.values()[i];
				}
			}
		}
		return master;
	}

	public static void increaseWeaponStats(Player p, NPC killedNPC) {
		Item weapon = p.getEquipment().getItem(Equipment.WEAPON);
		if (weapon == null)
			return;
		initializeMetas(weapon);
		double bonusValue = weapon.getMetaDataD("StrengthBonus");
		bonusValue += ((double) killedNPC.getCombatLevel()) / ((double) (500_000/10)); //1 classical hit every 500k levelled monsters killed
		weapon.addMetaData("StrengthBonus", bonusValue);
	}

	public static int increaseHitByWeaponBonus(Player p, int hit) {
		Item weapon = p.getEquipment().getItem(Equipment.WEAPON);
		if (weapon == null)
			return hit;
		initializeMetas(weapon);
		int bonus = (int) p.getEquipment().getItem(Equipment.WEAPON).getMetaDataD("StrengthBonus");
		return hit + bonus;
	}

	private static void initializeMetas(Item weapon) {
		if (!weapon.containsMetaData()) {
			weapon.addMetaData("StrengthBonus", (double) 0.0);
		}
		if (weapon.getMetaData("StrengthBonus") == null) {
			weapon.addMetaData("StrengthBonus", (double) 0.0);
		}
	}

	public static void sendExamine(Player p, Item item) {
		boolean hasStats = false;
		String nameColor = "<col=F01E2C>";
		String bonusesColor = "<col=28A99E>";
		String weaponName = bonusesColor + "This item";
		String strengthBonus = "";
		String attackBonus = "";
		String defenseBonus = "";
		String magicAttackBonus = "";
		String magicDefenseBonus = "";

		if(item.getMetaData("WeaponName") != null)
			weaponName = nameColor + (String) item.getMetaDataO("WeaponName");
		if (item.getMetaData("StrengthBonus") != null) {
			strengthBonus = " " + String.format("%.3f", item.getMetaDataD("StrengthBonus")) + " strength ";
			hasStats = true;
		}
		if (item.getMetaData("AttackBonus") != null) {
			attackBonus = " " + String.format("%.3f", item.getMetaDataD("AttackBonus")) + " attack ";
			hasStats = true;
		}
		if (item.getMetaData("DefenseBonus") != null) {
			defenseBonus = " " + String.format("%.3f", item.getMetaDataD("DefenseBonus")) + " defense ";
			hasStats = true;
		}
		if (item.getMetaData("MagicAttackBonus") != null) {
			magicAttackBonus = " " + String.format("%.3f", item.getMetaDataD("MagicAttackBonus")) + " mage attack ";
			hasStats = true;
		}
		if (item.getMetaData("MagicDefenseBonus") != null) {
			magicDefenseBonus = " " + String.format("%.3f", item.getMetaDataD("MagicDefenseBonus")) + " mage defense ";
			hasStats = true;
		}

		if(hasStats)
			p.sendMessage(weaponName + bonusesColor + " has" + strengthBonus + attackBonus + defenseBonus + magicAttackBonus + magicDefenseBonus);

	}

	public static void restoreChargesWithoutLosingMeta(Item item, ItemConstants.ItemDegrade deg) {
		item.setMetaDataO("combatCharges", deg.getDefaultCharges());
	}

	public static void restorePolyporeStaff(Item stick, int newCharges) {
		stick.setMetaDataO("polyporeCasts", newCharges);
	}

	public static boolean polyporeDontLoseMeta() {
		return !true;
	}

	public static void customDebugCommands() {
		Commands.add(Rights.PLAYER, "rights", "Completes all quests.", (p, args) -> {
			p.sendMessage("Rights: " + p.getRights());
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

		Commands.add(Rights.PLAYER, "kq [scale]", "Start qbd", (p, args) -> {
			p.getControllerManager().startController(new KalphiteQueenScalingInstanceController(Integer.parseInt(args[0])));
		});

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

	/*
	* Scales the equipment by 10% of the scale parameter to the bonus on the item.
	* defense
	* attack
	* strength
	* magicdefense
	* magicattack
	* */
	public static void scaleEquipmentBonus(Item item, String bonus, double scale) {
		String metaName = "";
		switch(bonus) {
			case "defense" -> {metaName = "DefenseBonus";}
			case "attack" -> {metaName = "AttackBonus";}
			case "strength" -> {metaName = "StrengthBonus";}
			case "magicdefense" -> {metaName = "MagicDefenseBonus";}
			case "magicattack" -> {metaName = "MagicAttackBonus";}
			default -> {
				System.out.println("Defaulted on scaleEquipmentBonus");
				return;
			}
		}
		item.setMetaDataO(metaName, CustomScripts.getAverage(item, bonus) * (scale - 1));
	}

	private static double getAverage(Item item, String type) {
		int[] bonuses = item.getDefinitions().bonuses;
		int defenseAvg = (bonuses[Bonus.STAB_DEF.ordinal()] + bonuses[Bonus.SLASH_DEF.ordinal()] + bonuses[Bonus.CRUSH_DEF.ordinal()]
				+ bonuses[Bonus.RANGE_DEF.ordinal()]) / 4;
		int attackAvg = (bonuses[Bonus.STAB_ATT.ordinal()] + bonuses[Bonus.SLASH_ATT.ordinal()] + bonuses[Bonus.CRUSH_ATT.ordinal()]
				+ bonuses[Bonus.RANGE_ATT.ordinal()]) / 4;
		int strengthAvg = bonuses[Bonus.MELEE_STR.ordinal()] + bonuses[Bonus.RANGE_STR.ordinal()] / 2;
		int magicDefenseAvg = bonuses[Bonus.MAGIC_DEF.ordinal()];
		int magicAttackAvg = bonuses[Bonus.MAGIC_ATT.ordinal()];

		switch(type.toLowerCase()) {
			case "defense" -> {
				if(defenseAvg < 0)
					return 0;
				return defenseAvg;
			}
			case "attack" -> {
				if(attackAvg < 0)
					return 0;
				return attackAvg;
			}
			case "strength" -> {
				if(strengthAvg < 0)
					return 0;
				return strengthAvg;
			}
			case "magicdefense" -> {
				if(magicDefenseAvg < 0)
					return 0;
				return magicDefenseAvg;
			}
			case "magicattack" -> {
				if(magicAttackAvg < 0)
					return 0;
				return magicAttackAvg;
			}
			default -> {
				System.out.println("Defaulted on getAverage");
				return 0;
			}
		}
	}

	public static boolean isTradeable(Item item) {
		if (item.getMetaData() == null)
			return true;
		if(item.getMetaData().size() == 1)
			if(item.getMetaData("StrengthBonus") != null)
				return true;
		return false;
	}

	public static ItemOnItemHandler handleMakeVineWhip = new ItemOnItemHandler(new int[]{21369}, new int[]{4151}) {//blamish oil, fly fishing rod
		@Override
		public void handle(ItemOnItemEvent e) {
			if(e.getItem1().getId() == 4151)
				e.getItem1().setId(21371);
			if(e.getItem2().getId() == 4151)
				e.getItem2().setId(21371);
			e.getPlayer().getInventory().deleteItem(21369, 1); // WHIP VINE
			e.getPlayer().getInventory().refresh();
		}
	};

	public static void handleWhipSplit(Player p, Item item) {
		if (p.getInventory().getFreeSlots() >= 1) {
			item.setId(4151);
			p.getInventory().addItem(21369);
			p.sendMessage("You split the vine from the whip.");
			p.getInventory().refresh();
		} else
			p.sendMessage("Not enough space in your inventory.");
	}

	public static boolean dontDeleteWhip() {
		return false;
	}

	/**
	 * If we have a strength bonus you can't trade it on the GE, Shop, place in familiar or...
	 *
	 * @param item
	 * @return
	 */
	public static boolean hasStrengthBonusForExchange(Player player, Item item) {
		if (item.getMetaData("StrengthBonus") == null)
			return false;
		player.sendMessage("This cannot be done with strengthened items!");
		return true;
	}

	public static int getAverageCombatLevelDung(int level, int teamSize) {
		return (int) ((level / teamSize) * 0.9);
	}

	public static int getTokenReward(double totalXp) {
		return (int) ((totalXp / 10.0) * 3);
	}

	public static double increaseByXPRateForInterfaceDungeoneering(double xp) {
		xp *= Settings.getConfig().getXpRate();
		return xp;
	}

	public static LoginHandler onLoginUpdates = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if(!ShieldOfArrav.hasGang(e.getPlayer()))
				questsEnabled(e.getPlayer(), false);
			if((boolean)e.getPlayer().get("resetQuestsNovember") == false) {
				e.getPlayer().save("resetQuestsNovember", true);
				e.getPlayer().getQuestManager().resetQuest(Quest.KNIGHTS_SWORD);
				e.getPlayer().getQuestManager().resetQuest(Quest.WATERFALL_QUEST);
			}
		}
	};

	public static void getNPCCombatExamine(NPC npc, Player player) {
		player.sendMessage(npc.getName() + " has HP: "+ npc.getMaxHitpoints() +  ", Attack: " + npc.getAttackLevel()
				+ ", Strength: " + npc.getStrengthLevel() + ", Defense: " + npc.getDefenseLevel() + ", Magic: " + npc.getMagicLevel());
	}

	public static void questsEnabled(Player p, boolean enabled) {
		if (!enabled) {
			ShieldOfArrav.setGang(p, "Phoenix");
			if (!p.getQuestManager().completedAllQuests())
				for (Quest quest : Quest.values())
					if (quest.isImplemented()) {
						p.getQuestManager().setStage(quest, quest.getHandler().getCompletedStage());
						p.getQuestManager().sendQuestStage(quest, true);
						p.getQuestManager().sendQuestPoints();
					}
		}
	}

	public static boolean completeTreasureTrail(Player player, int level, Item item) {
		if(level == -1)
			return false;
		player.getInventory().deleteItem(item.getId(), 1);
		player.getTreasureTrailsManager().openReward(level);
		return true;
	}

	public static void updateMetasOnWeapons(Player player) {
		ItemsContainer<Item> boundItems = player.getDungManager().getBindedItems().asItemContainer();
		Item weapon = player.getEquipment().get(Equipment.WEAPON);
		if(weapon == null) {
			return;
		}
		for(Item boundItem : boundItems.asList()) {
			if (weapon.getId() == boundItem.getId()) {
				if (weapon.containsMetaData() && weapon.getMetaData("StrengthBonus") != null) {
					int slot = player.getDungManager().getBindedItems().getThisItemSlot(boundItem);
					player.getDungManager().getBindedItems().remove(boundItem);
					player.getDungManager().bind(weapon, slot);
				}
			}
		}
	}

	public static int addBonusToEquip(Item item, Bonus bonus) {
		int value = item.getDefinitions().getBonuses()[bonus.ordinal()];
		switch(bonus) {
			case STAB_DEF, SLASH_DEF, CRUSH_DEF, RANGE_DEF-> {
				return (value + (item.getMetaData("DefenseBonus") == null ? 0 : (int)item.getMetaDataD("DefenseBonus", 0)));
			}
			case MELEE_STR, RANGE_STR -> {
				return (value + (item.getMetaData("StrengthBonus") == null ? 0 : (int)item.getMetaDataD("StrengthBonus", 0)));
			}
			case CRUSH_ATT, RANGE_ATT, SLASH_ATT, STAB_ATT -> {
				return (value + (item.getMetaData("AttackBonus") == null ? 0 : (int)item.getMetaDataD("AttackBonus", 0)));
			}
			case MAGIC_ATT -> {
				return (value + (item.getMetaData("MagicAttackBonus") == null ? 0 : (int)item.getMetaDataD("MagicAttackBonus", 0)));
			}
			case MAGIC_DEF -> {
				return (value + (item.getMetaData("MagicDefenseBonus") == null ? 0 : (int)item.getMetaDataD("MagicDefenseBonus", 0)));
			}
			default -> {
				return value;
			}
		}
	}

	public static boolean silenceBoundNotice(boolean isDisabled) {
		return !isDisabled;
	}

	public static boolean isBindedItem(Item item) {
		String name = item.getName();
		for (int i = 15775; i <= 16272; i++)
			if (item.getId() == i)
				return true;
		for (int i = 19865; i <= 19866; i++)
			if (item.getId() == i)
				return true;
		return false;

	}

	public static int slayerPointsMultiplier(int amount) {
		return amount*3;
	}

	public static void bindItemDirectly(ItemsContainer<Item> bindedItems, Item item) {
		bindedItems.add(item);
	}

	public static boolean chargesLostOnDeath() {
		return false;
	}

	public static World.DropMethod untradeablesDropNormal() {
		return World.DropMethod.NORMAL;
	}

	public static boolean deathCofferIsSuccessful(Player player) {
		if(player.isIronMan()) {
			player.setNextWorldTile(Settings.getConfig().getPlayerRespawnTile());
			player.lock(3);
			player.startConversation(new Dialogue().addNPC(15661, HeadE.CALM, "Given you have earned everything yourself(iron) you get a free pass...").addPlayer(HeadE.HAPPY_TALKING, "Thanks!"));
			return true;
		}
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
					if(p.getDungManager().getTokens() < 5)
						options.add("I don't have enough tokens!", new Dialogue());
					if(p.getDungManager().getTokens() >= 5) {
						if(!p.getInventory().hasFreeSlots())
							options.add("I don't have enough space!", new Dialogue());
						if(p.getInventory().hasFreeSlots())
							options.add("Okay I will take it", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Okay I will take it!")
								.addNPC(NPC, HeadE.CALM_TALK, "Sounds good...")
								.addSimple("He gives you the charming imp", ()->{
									p.getInventory().addItem(25350, 1);
									p.getDungManager().removeTokens(5);
								}));
					}
				});
				create();
			}
		};
	}

	public static boolean updateRightsOnRun(Player p) {
		p.setRights(Rights.PLAYER);
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
						.addNPC(15661, HeadE.CALM, "Not yet, mortal. I am still thinking about possible reward options. But I will still keep tally of your points regardless."));
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

	public static ItemOnNPCHandler handleLightCreatures = new ItemOnNPCHandler(true, new Object[] { 15661 }) {
		@Override
		public void handle(ItemOnNPCEvent e) {
			int NPC = e.getNPC().getId();
			Player player = e.getPlayer();
			Item weapon = e.getItem();

			if (weapon.getMetaData("StrengthBonus") == null) {
				player.startConversation(new Dialogue().addNPC(NPC, HeadE.CALM_TALK, "Your weapon must first taste death before taking on a name..."));
				return;
			}

			if(weapon.getMetaData("WeaponName") != null) {
				player.startConversation(new Dialogue()
						.addNPC(NPC, HeadE.CALM_TALK, "Mortal, you have already named this weapon..."));
				return;
			}

			if (weapon.getMetaData("StrengthBonus") != null && weapon.getMetaData("WeaponName") == null)
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

		}
	};
}
