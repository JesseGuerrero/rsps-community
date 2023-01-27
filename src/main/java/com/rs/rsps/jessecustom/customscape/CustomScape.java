package com.rs.rsps.jessecustom.customscape;

import com.rs.Settings;
import com.rs.cache.loaders.Bonus;
import com.rs.game.World;
import com.rs.game.content.ItemConstants;
import com.rs.game.content.quests.shieldofarrav.ShieldOfArrav;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.game.engine.dialogue.Dialogue;
import com.rs.game.engine.quest.Quest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.rsps.jessecustom.bosses.corp.ScalingCorporealBeast;
import com.rs.rsps.jessecustom.groupironman.GIM;

import java.util.*;

@PluginEventHandler
public class CustomScape {
	public static String[] metasString = new String[] {
			"DefenseBonus", "StrengthBonus", "RangeStrengthBonus", "AttackBonus", "RangeAttackBonus", "MagicAttackBonus", "MagicDefenseBonus", "HitBonus", "WeaponName"
	};

	public static void setPlayerCustomScape(Player player) {
		player.save("isCustomScape", true);
	}

	public static boolean isPlayerCustomScape(Player player) {
		if(player.getO("isCustomScape") == null)
			return false;
		return player.getBool("isCustomScape");
	}

		public static LoginHandler onLoginUpdates = new LoginHandler(e-> {

//			if(GIM.isGIM(e.getPlayer()))
//				return;
			if(!GIM.isGIM(e.getPlayer()) && !isPlayerCustomScape(e.getPlayer()))
				setPlayerCustomScape(e.getPlayer());
			if(isPlayerCustomScape(e.getPlayer()))
				if(!ShieldOfArrav.hasGang(e.getPlayer()))
					questsEnabled(e.getPlayer(), false);
				if((boolean)e.getPlayer().get("resetQuestsNovember") == false) {
					e.getPlayer().save("resetQuestsNovember", true);
					e.getPlayer().getQuestManager().resetQuest(Quest.KNIGHTS_SWORD);
					e.getPlayer().getQuestManager().resetQuest(Quest.WATERFALL_QUEST);
				}
	});


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

		public static void getNPCCombatExamine(NPC npc, Player player) {
		if(Settings.getConfig().isDebug())
			player.sendMessage(npc.getName() + " has HP: "+ npc.getMaxHitpoints() +  ", Attack: " + npc.getAttackLevel()
					+ ", Strength: " + npc.getStrengthLevel() + ", Defense: " + npc.getDefenseLevel() + ", Magic: " + npc.getMagicLevel());
	}


	public static void updateMetasOnWeaponsDung(Player player) {
		ItemsContainer<Item> boundItems = player.getDungManager().getBindedItems().asItemContainer();
		Item weapon = player.getEquipment().get(Equipment.WEAPON);
		if(weapon == null) {
			return;
		}
		for(Item boundItem : boundItems.asList()) {
			if (weapon.getId() == boundItem.getId()) {
				if (weapon.containsMetaData() && weapon.getMetaData("HitBonus") != null) {
					int slot = player.getDungManager().getBindedItems().getThisItemSlot(boundItem);
					player.getDungManager().getBindedItems().remove(boundItem);
					player.getDungManager().bind(weapon, slot);
				}
			}
		}
	}

	public static int addBonusToEquip(Player player, Item item, Bonus bonus) {
		int value = item.getDefinitions().getBonuses()[bonus.ordinal()];
		if(!isPlayerCustomScape(player))
			return value;
		switch(bonus) {
			case STAB_DEF, SLASH_DEF, CRUSH_DEF, RANGE_DEF-> {
				return (value + (item.getMetaData("DefenseBonus") == null ? 0 : (int)Math.ceil(item.getMetaDataD("DefenseBonus", 0))));
			}
			case MELEE_STR -> {
				return (value + (item.getMetaData("StrengthBonus") == null ? 0 : (int)Math.ceil(item.getMetaDataD("StrengthBonus", 0))));
			}
			case RANGE_STR -> {
				return (value + (item.getMetaData("RangeStrengthBonus") == null ? 0 : (int)Math.ceil(item.getMetaDataD("RangeStrengthBonus", 0))));
			}
			case CRUSH_ATT, SLASH_ATT, STAB_ATT -> {
				return (value + (item.getMetaData("AttackBonus") == null ? 0 : (int)Math.ceil(item.getMetaDataD("AttackBonus", 0))));
			}
			case RANGE_ATT -> {
				return (value + (item.getMetaData("RangeAttackBonus") == null ? 0 : (int)Math.ceil(item.getMetaDataD("RangeAttackBonus", 0))));
			}
			case MAGIC_ATT -> {
				return (value + (item.getMetaData("MagicAttackBonus") == null ? 0 : (int)Math.ceil(item.getMetaDataD("MagicAttackBonus", 0))));
			}
			case MAGIC_DEF -> {
				return (value + (item.getMetaData("MagicDefenseBonus") == null ? 0 : (int)Math.ceil(item.getMetaDataD("MagicDefenseBonus", 0))));
			}
			default -> {
				return value;
			}
		}
	}

	public static void bindItemDirectly(ItemsContainer<Item> bindedItems, Item item) {
		bindedItems.add(item);
	}

	public static boolean silenceBoundNotice(boolean isDisabled) {
		return !isDisabled;
	}


	public static ObjectClickHandler handleKalphiteQueenLairEntrance = new ObjectClickHandler(true, new Object[] { 48803 }, e -> {
		Player player = e.getPlayer();
		if(e.getPlayer().isKalphiteLairSetted()) {
//				if(!CustomScape.isPlayerCustomScape(e.getPlayer())) {
//					e.getPlayer().setNextWorldTile(WorldTile.of(3508, 9494, 0));
//					return;
//				}
//				if(e.getPlayer().getInventory().getAmountOf(995) >= 5_000) {
//					e.getPlayer().startConversation(new Dialogue()
//							.addOptions("Do you want to create a boss instance?", option -> {
//								option.add("Yes", ()->{
//									player.getControllerManager().startController(new KalphiteQueenScalingInstanceController());
//								});
//								option.add("No", () -> {e.getPlayer().setNextWorldTile(WorldTile.of(3508, 9494, 0));});
//							})
//					);
//					return;
//				}
//				e.getPlayer().sendMessage("You need 5k coins for an instance");
			e.getPlayer().setNextWorldTile(WorldTile.of(3508, 9494, 0));
		}
	});



/*else if (id == 48803 && player.isKalphiteLairSetted())
				player.setNextWorldTile(WorldTile.of(3508, 9494, 0));*/

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



	public static void increaseWeaponStats(Player p, NPC killedNPC) {
		if(!isPlayerCustomScape(p))
			return;
		Item weapon = p.getEquipment().getItem(Equipment.WEAPON);
		if (weapon == null)
			return;
		initializeMetas(weapon);
		double bonusValue = weapon.getMetaDataD("HitBonus");
		bonusValue += ((double) killedNPC.getCombatLevel()) / ((double) (100_000/10)); //1 classical hit every 500k levelled monsters killed
		weapon.addMetaData("HitBonus", bonusValue);
	}

	private static void initializeMetas(Item weapon) {
		if (!weapon.containsMetaData()) {
			weapon.addMetaData("HitBonus", (double) 0.0);
		}
		if (weapon.getMetaData("HitBonus") == null) {
			weapon.addMetaData("HitBonus", (double) 0.0);
		}
	}

	public static void sendExamine(Player p, Item item) {
		if(!isPlayerCustomScape(p))
			return;
		boolean hasStats = false;
		String nameColor = "<col=F01E2C>";
		String bonusesColor = "<col=28A99E>";
		String weaponName = bonusesColor + "This item";
		String hitBonus = "";
		String strengthBonus = "";
		String attackBonus = "";
		String defenseBonus = "";
		String magicAttackBonus = "";
		String magicDefenseBonus = "";

		if(item.getMetaData("WeaponName") != null)
			weaponName = nameColor + (String) item.getMetaDataO("WeaponName");
		if (item.getMetaData("HitBonus") != null && item.getMetaDataD("HitBonus") > 0) {
			hitBonus = " " + String.format("%.3f", item.getMetaDataD("HitBonus")) + " hit-bonus ";
			hasStats = true;
		}
		if (item.getMetaData("StrengthBonus") != null && item.getMetaDataD("StrengthBonus") > 0) {
			strengthBonus = " " + String.format("%.1f", item.getMetaDataD("StrengthBonus")) + " strength ";
			hasStats = true;
		}
		if (item.getMetaData("AttackBonus") != null && item.getMetaDataD("AttackBonus") > 0) {
			attackBonus = " " + String.format("%.1f", item.getMetaDataD("AttackBonus")) + " attack ";
			hasStats = true;
		}
		if (item.getMetaData("RangeStrengthBonus") != null && item.getMetaDataD("RangeStrengthBonus") > 0) {
			strengthBonus = " " + String.format("%.1f", item.getMetaDataD("RangeStrengthBonus")) + " strength ";
			hasStats = true;
		}
		if (item.getMetaData("RangeAttackBonus") != null && item.getMetaDataD("RangeAttackBonus") > 0) {
			attackBonus = " " + String.format("%.1f", item.getMetaDataD("RangeAttackBonus")) + " attack ";
			hasStats = true;
		}
		if (item.getMetaData("DefenseBonus") != null && item.getMetaDataD("DefenseBonus") > 0) {
			defenseBonus = " " + String.format("%.1f", item.getMetaDataD("DefenseBonus")) + " defense ";
			hasStats = true;
		}
		if (item.getMetaData("MagicAttackBonus") != null && item.getMetaDataD("MagicAttackBonus") > 0) {
			magicAttackBonus = " " + String.format("%.1f", item.getMetaDataD("MagicAttackBonus")) + " mage attack ";
			hasStats = true;
		}
		if (item.getMetaData("MagicDefenseBonus") != null && item.getMetaDataD("MagicDefenseBonus") > 0) {
			magicDefenseBonus = " " + String.format("%.1f", item.getMetaDataD("MagicDefenseBonus")) + " mage defense ";
			hasStats = true;
		}

		if(hasStats)
			p.sendMessage(weaponName + bonusesColor + " has"
					+ hitBonus + strengthBonus + attackBonus + defenseBonus + magicAttackBonus + magicDefenseBonus);

	}


	public static boolean polyporeLoseMeta(Player p) {
		if(isPlayerCustomScape(p))
			return false;
		return true;
	}

	public static void createGodSwordOrLeftShieldOrSigils(Player player, int godSwordID, int hilt, int blade) {
			Map<String, Object> metas1 = player.getInventory().getItemById(hilt).getMetaData();
			Map<String, Object> metas2 = player.getInventory().getItemById(blade).getMetaData();
			player.getInventory().deleteItem(hilt, 1);
			player.getInventory().deleteItem(blade, 1);
			Item godSword = new Item(godSwordID, 1);
			if(metas1 != null)
				for(String meta : metas1.keySet())
					if(!meta.equalsIgnoreCase("WeaponName"))
						godSword.setMetaDataO(meta, metas1.get(meta));
			if(metas2 != null)
				for(String meta : metas2.keySet())
					if(!meta.equalsIgnoreCase("WeaponName"))
						if(metas1 != null && metas1.containsKey(meta)) {
							godSword.setMetaDataO(meta, ((double)metas1.get(meta) + (double)metas2.get(meta)));
						} else
							godSword.setMetaDataO(meta, metas2.get(meta));
			player.getInventory().addItem(godSword);
	}

	public static void createGWDScalingDialogueFromAltar(Player player, int altarID) {
		if(!isPlayerCustomScape(player))
			return;
//		if (player.getInventory().getAmountOf(995) >= 5_000) {
//			player.startConversation(new Dialogue()
//					.addOptions("Start a boss instance?", option -> {
//						option.add("Yes", () -> {
//							player.getInventory().removeItems(new Item(995, 5000));
//							if (altarID == 26289)
//								player.getControllerManager().startController(new GeneralGraardorScalingInstanceController());
//							if(altarID == 26286)
//								player.getControllerManager().startController(new KrilTstsarothScalingInstanceController());
//							if(altarID == 26288)
//								player.getControllerManager().startController(new KreeArraScalingInstanceController());
//							if(altarID == 26287)
//								player.getControllerManager().startController(new CommanderZilyanaScalingInstanceController());
//						});
//						option.add("No", new Dialogue());
//					}));
//		} else
//			player.sendMessage("You need 5k coins for an instance");
	}

	public static void createCorpScalingDialogue(Player player) {
		if(!isPlayerCustomScape(player)) {
			player.setNextWorldTile(WorldTile.of(2921, player.getY(), 2));
			return;
		}
		if (player.getInventory().getAmountOf(995) >= 5_000) {
			player.startConversation(new Dialogue()
					.addOptions("Start a boss instance?", option -> {
						option.add("Yes", () -> {
							player.getInventory().removeItems(new Item(995, 5000));
//							player.getControllerManager().startController(new CorporealBeastScalingInstanceController());
						});
						option.add("No", ()->{player.setNextWorldTile(WorldTile.of(2921, player.getY(), 2));});
					}));
		} else {
			player.sendMessage("You need 5k coins for an instance");
			player.setNextWorldTile(WorldTile.of(2921, player.getY(), 2));
		}

	}


	public static int KalphiteQueenRespawnTime() {
		return 30;
	}

	public static int increaseHitByWeaponBonus(Player p, int hit) {
		if(!isPlayerCustomScape(p))
			return hit;

		Item weapon = p.getEquipment().getItem(Equipment.WEAPON);
		if (weapon == null)
			return hit;
		initializeMetas(weapon);
		int bonus = (int) weapon.getMetaDataD("HitBonus");
		return hit + bonus;
	}



	public static void restoreChargesWithoutLosingMeta(Item item, ItemConstants.ItemDegrade deg) {
		item.setMetaDataO("combatCharges", deg.getDefaultCharges());
	}

	public static void restorePolyporeStaff(Item stick, int newCharges) {
		stick.setMetaDataO("polyporeCasts", newCharges);
	}





	public static boolean isScalingCorp(NPC npc) {
		if(npc instanceof ScalingCorporealBeast beast) {
			beast.spawnDarkEnergyCore();
			return true;
		}
		return false;
	}

	/*
	 * Scales the equipment by 10% of the scale parameter to the bonus on the item.
	 * defense
	 * attack
	 * strength
	 * magicdefense
	 * magicattack
	 * */
	public static void scaleEquipmentBonus(Item item, double scale) {
		for(Object itemName : ScalingItems.getDefensiveScalingItems())
			if(item.getName().equalsIgnoreCase((String)itemName))
				if(item.getMetaData("DefenseBonus") == null) {
					double average = getAverage(item, "defense") * (scale - 1);
					if(average > 0)
						item.setMetaDataO("DefenseBonus", average);
				}
		for(Object itemName : ScalingItems.getMeleeAttackScalingItems())
			if(item.getName().equalsIgnoreCase((String)itemName))
				if(item.getMetaData("AttackBonus") == null) {
					double average = getAverage(item, "attack") * (scale - 1);
					if(average > 0)
						item.setMetaDataO("AttackBonus", average);
				}
		for(Object itemName : ScalingItems.getMeleeStrengthScalingItems())
			if(item.getName().equalsIgnoreCase((String)itemName))
				if(item.getMetaData("StrengthBonus") == null) {
					double average = getAverage(item, "strength") * (scale - 1);
					if(average > 0)
						item.setMetaDataO("StrengthBonus", average);
				}
		for(Object itemName : ScalingItems.getRangeAttackScalingItems())
			if(item.getName().equalsIgnoreCase((String)itemName))
				if(item.getMetaData("RangeAttackBonus") == null) {
					double average = getAverage(item, "rangeattack") * (scale - 1);
					if(average > 0)
						item.setMetaDataO("RangeAttackBonus", average);
				}
		for(Object itemName : ScalingItems.getRangeStrengthScalingItems())
			if(item.getName().equalsIgnoreCase((String)itemName))
				if(item.getMetaData("RangeStrengthBonus") == null) {
					double average = getAverage(item, "rangestrength") * (scale - 1);
					if(average > 0)
						item.setMetaDataO("RangeStrengthBonus", average);
				}
		for(Object itemName : ScalingItems.getMagicDefenseScalingItems())
			if(item.getName().equalsIgnoreCase((String)itemName))
				if(item.getMetaData("MagicDefenseBonus") == null) {
					double average = getAverage(item, "magicdefense") * (scale - 1);
					if(average > 0)
						item.setMetaDataO("MagicDefenseBonus", average);
				}
		for(Object itemName : ScalingItems.getMagicAttackScalingItems())
			if(item.getName().equalsIgnoreCase((String)itemName))
				if(item.getMetaData("MagicAttackBonus") == null) {
					double average = getAverage(item, "magicattack") * (scale - 1);
					if(average > 0)
						item.setMetaDataO("MagicAttackBonus", average);
				}
	}


	public static void customScale(Entity attacker, NPC npc) {
		if(npc.getTempAttribs().getO("AttackingCustomScapePlayer") != null)
			return;
		if(npc instanceof DungeonNPC)
			return;
		if(attacker instanceof Player player && isPlayerCustomScape(player)) {
			npc.getTempAttribs().setO("AttackingCustomScapePlayer", player);
			int scale = player.getI("CustomScapeScale", 0);
			double combatScale = 1 + (scale/10.0);
			npc.setCombatLevel((int)Math.ceil(npc.getCombatLevel()*combatScale));
			if(Settings.getConfig().isDebug())
				player.sendMessage("Set npc scale " + combatScale);
			npc.getTempAttribs().setI("CustomScapeScale", scale);
			Map<NPCCombatDefinitions.Skill, Integer> levels = NPCCombatDefinitions.getDefs(npc.getId()).getLevels();
			Map<NPCCombatDefinitions.Skill, Integer> upgradedStats = new HashMap<>();
			for(NPCCombatDefinitions.Skill combatSkill : levels.keySet())
				upgradedStats.put(combatSkill, (int) Math.ceil(levels.get(combatSkill) * combatScale));
			npc.setLevels(upgradedStats);
			npc.setHitpoints(npc.getMaxHitpoints());
		}
		if(npc.getTempAttribs().getO("AttackingCustomScapePlayer") == null) {
			if(attacker instanceof Player player)
				if(Settings.getConfig().isDebug())
					player.sendMessage("reset2");
			resetNPCScale(npc);
		}
	}

	public static void resetNPCTickRestore(NPC npc, long lastAttackedByTarget) {
		if(npc.getTempAttribs() != null && npc.getTempAttribs().getI("CustomScapeScale") >= 1)
			if(System.currentTimeMillis() - lastAttackedByTarget > 1000*60*1) { //1 minute
				if(Settings.getConfig().isDebug()) {
					Player attacker = World.getPlayerByDisplay("Abc20");
					attacker.sendMessage("reset3 " + npc.getName());
				}
				CustomScape.resetNPCScale(npc);
			}
	}

	public static void resetNPCScale(NPC npc) {
		npc.getTempAttribs().removeI("CustomScapeScale");
		npc.setCombatLevel(npc.getDefinitions().combatLevel);
		npc.setLevels(NPCCombatDefinitions.getDefs(npc.getId()).getLevels());
		if(npc.getHitpoints() > npc.getMaxHitpoints())
			npc.setHitpoints(npc.getMaxHitpoints());
	}

	public static int scaleHP(NPC npc) {
		if(npc.getTempAttribs() == null)
			return npc.getCombatDefinitions().getHitpoints();
		int scale = npc.getTempAttribs().getI("CustomScapeScale", 1);
		double combatScale = 1 + (scale/10.0);
		if(combatScale > 1)
			return (int)Math.ceil(NPCCombatDefinitions.getDefs(npc.getId()).getHitpoints()* combatScale);
		return npc.getCombatDefinitions().getHitpoints();
	}

	private static double getAverage(Item item, String type) {
		if(item.getName().equalsIgnoreCase("Bandos hilt"))
			item = new Item(11696, 1);
		if(item.getName().equalsIgnoreCase("Zamorak hilt"))
			item = new Item(11700, 1);
		if(item.getName().equalsIgnoreCase("Armadyl hilt"))
			item = new Item(11694, 1);
		if(item.getName().equalsIgnoreCase("Saradomin hilt"))
			item = new Item(11698, 1);
		if(item.getName().equalsIgnoreCase("Arcane sigil"))
			item = new Item(13738, 1);
		if(item.getName().equalsIgnoreCase("Divine sigil"))
			item = new Item(13740, 1);
		if(item.getName().equalsIgnoreCase("Elysian sigil"))
			item = new Item(13742, 1);
		if(item.getName().equalsIgnoreCase("Spectral sigil"))
			item = new Item(13744, 1);

		int[] bonuses = item.getDefinitions().bonuses;
		int defenseAvg = (bonuses[Bonus.STAB_DEF.ordinal()] + bonuses[Bonus.SLASH_DEF.ordinal()] + bonuses[Bonus.CRUSH_DEF.ordinal()]
				+ bonuses[Bonus.RANGE_DEF.ordinal()]) / 4;
		int attackAvg = (bonuses[Bonus.STAB_ATT.ordinal()] + bonuses[Bonus.SLASH_ATT.ordinal()] + bonuses[Bonus.CRUSH_ATT.ordinal()]) / 3;
		int rangeAttackAvg = bonuses[Bonus.RANGE_ATT.ordinal()];
		int rangeStrengthAvg = bonuses[Bonus.RANGE_STR.ordinal()];
		int strengthAvg = bonuses[Bonus.MELEE_STR.ordinal()];
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
			case "rangeattack" -> {
				if(rangeAttackAvg < 0)
					return 0;
				return rangeAttackAvg;
			}
			case "rangestrength" -> {
				if(rangeStrengthAvg < 0)
					return 0;
				return rangeStrengthAvg;
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
	//StrengthBonus
	public static boolean isTradeable(Item item) {
		if (item.getMetaData() == null) {
			return true;
		}
		if(item.getMetaData().size() >= 1) {
			Map<String, Object> metas = item.getMetaData();//Any that is not these is false
			List<String> keys = new ArrayList<>();
			for(String key : metas.keySet())
				keys.add(key);
			for(String metaString : metasString)
				if(keys.contains(metaString)) {
					keys.remove(metaString);
				}
			if(keys.size() == 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean dontDeleteWhip() {
		return false;
	}

	/**
	 * If we have a strength bonus you can't trade it on the GE, Shop, place in familiar or...
	 *
	 * @param
	 * @return
	 */
	public static boolean hasMetaBonusWhichPreventsExchange(Player player, Item item) {
		boolean hasMeta = false;
		for(String metaString : metasString)
			if (item.getMetaData(metaString) != null) {
				hasMeta = true;
				break;
			}
		if(hasMeta)
			player.sendMessage("This cannot be done with strengthened items!");
		return hasMeta;
	}

	public static int getAverageCombatLevelDung(int level, int teamSize) {
		return (int) ((level / teamSize) * 0.9);
	}

}
