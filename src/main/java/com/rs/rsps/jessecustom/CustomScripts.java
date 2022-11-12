package com.rs.rsps.jessecustom;

import com.rs.Settings;
import com.rs.game.content.ItemConstants;
import com.rs.game.content.Toolbelt;
import com.rs.game.content.commands.Commands;
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
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.LoginHandler;

import static com.rs.game.content.skills.slayer.ReaperAssignments.talkAboutAssignment;

@PluginEventHandler
public class CustomScripts {
	public static Master highestSlayerMaster(Player player, Master master) {
		if (player.withinDistance(new WorldTile(3088, 3484, 0))) {
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
		if (item.getMetaData("StrengthBonus") != null)
			p.sendMessage("<col=28A99E>This item has " + String.format("%.3f", item.getMetaDataD("StrengthBonus")) + " strength bonus...");
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
			questsEnabled(e.getPlayer(), false);
		}
	};

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
		player.getInventory().deleteItem(item.getId(), 1);
		player.getTreasureTrailsManager().openReward(level);
		return true;
	}

	public static void updateMetasOnWeapons(Player player) {
		ItemsContainer<Item> boundItems = player.getDungManager().getBindedItems().asItemContainer();
		Item weapon = player.getEquipment().get(Equipment.WEAPON);
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

	public static void bindItemDirectly(ItemsContainer<Item> bindedItems, Item item) {
		bindedItems.add(item);
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
}
