package com.rs.rsps.jessecustom;

import com.rs.game.content.ItemConstants;
import com.rs.game.content.skills.slayer.Master;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class CustomScripts {
	public static Master highestSlayerMaster(Player player, Master master) {
		if(player.withinDistance(new WorldTile(3088, 3484, 0))) {
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
		if(weapon == null)
			return;
		initializeMetas(weapon);
		double bonusValue = weapon.getMetaDataD("StrengthBonus");
		bonusValue += ((double)killedNPC.getCombatLevel()) / ((double)1_000_0000);
		weapon.addMetaData("StrengthBonus", bonusValue);
	}

	public static int increaseHitByWeaponBonus(Player p, int hit) {
		Item weapon = p.getEquipment().getItem(Equipment.WEAPON);
		if(weapon == null)
			return hit;
		initializeMetas(weapon);
		int bonus = (int)p.getEquipment().getItem(Equipment.WEAPON).getMetaDataD("StrengthBonus");
		return hit + bonus;
	}

	private static void initializeMetas(Item weapon) {
		if(!weapon.containsMetaData()) {
			weapon.addMetaData("StrengthBonus", (double)0.0);
		}
		if(weapon.getMetaData("StrengthBonus") == null) {
			weapon.addMetaData("StrengthBonus", (double)0.0);
		}
	}

	public static void sendExamine(Player p, Item item) {
		if (item.getMetaData("StrengthBonus") != null)
			p.sendMessage("<col=28A99E>This item has " + String.format("%.6f", item.getMetaDataD("StrengthBonus")) + " strength bonus...");
	}

	public static void restoreChaotic(Item item, ItemConstants.ItemDegrade deg) {
		item.setMetaDataO("combatCharges", deg.getDefaultCharges());
	}

	public static boolean checkTradeable(Item item) {
		if(item.getMetaData() == null)
			return true;
		return (item.getMetaData().size() > 1 && item.getMetaData("StrengthBonus") != null);
	}

	/**
	 * If we have a strength bonus you can't trade it on the GE, Shop, place in familiar or...
	 * @param item
	 * @return
	 */
	public static boolean hasStrengthBonusForExchange(Player player, Item item) {
		if(item.getMetaData("StrengthBonus") == null)
			return false;
		player.sendMessage("This cannot be done with strengthened items!");
		return true;
	}
}
