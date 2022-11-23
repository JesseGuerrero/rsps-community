package com.rs.rsps.jessecustom.groupironman;

import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.lib.util.Utils;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.handlers.NPCDropHandler;
import com.rs.rsps.jessecustom.CustomScripts;
import com.rs.rsps.jessecustom.bosses.ScalingItems;
import com.rs.rsps.jessecustom.bosses.corp.ScalingCorporealBeast;
import com.rs.rsps.jessecustom.bosses.godwars.armadyl.ScaledKreeArra;
import com.rs.rsps.jessecustom.bosses.godwars.bandos.ScaledGeneralGraardor;
import com.rs.rsps.jessecustom.bosses.godwars.saradomin.ScaledCommanderZilyana;
import com.rs.rsps.jessecustom.bosses.godwars.zamorak.ScaledKrilTstsaroth;
import com.rs.rsps.jessecustom.bosses.kalphitequeen.KalphiteQueenScaling;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PrestigeGIMManager {
	private int prestige = 0;
	private transient GroupIronMan group;


	public void setGroup(GroupIronMan group) {
		this.group = group;
	}

	public void refreshPrestige() {
		switch(prestige) {
			case 0 -> {
				if(deservesNovice())
					promoteToNovice();
			}
			case 1 -> {
				if(deservesIntermediate())
					;
			}
		}
		group.updateDBGIM();
	}

	public int getPrestige() {
		return prestige;
	}

	public void setPrestige(int prestige) {
		this.prestige = prestige;
	}


	private void promoteToNovice() {
		group.broadcastMessage("<col=B3E0DC>Your team has ranked to \"Novice\" and each earned 5k loyalty points & 1 GIM bank!");
		for(String username : group.getPlayers()) {
			World.forceGetPlayerByDisplay(username, member -> {
				member.loyaltyPoints += 5000;
				WorldDB.getPlayers().save(member);
			});
		}
		setPrestige(1);
	}

	private boolean deservesNovice() {
		AtomicBoolean deservesNovice = new AtomicBoolean(true);
		for (String username : group.getPlayers()) {
			World.forceGetPlayerByDisplay(username, member -> {
				if (member.getSkills().getTotalLevel() <= 500)
					deservesNovice.set(false);
				if (member.getQuestManager().getQuestPoints() < 10)
					deservesNovice.set(false);
			});
		}
		return deservesNovice.get();
	}

	private void promoteToIntermediate() {
		group.broadcastMessage("<col=B3E0DC>Your team has ranked to \"Intermediate\" and each earned 15k loyalty points, 1 GIM bank and 25% for noted dragon bones!");
		for(String username : group.getPlayers()) {
			World.forceGetPlayerByDisplay(username, member -> {
				member.loyaltyPoints += 15000;
				member.save("DragonPerk", true);
				WorldDB.getPlayers().save(member);
			});
		}
		setPrestige(2);
	}

	private boolean deservesIntermediate() {
		AtomicBoolean deservesIntermediate = new AtomicBoolean(true);
		AtomicInteger dragonKills = new AtomicInteger(0);
		AtomicInteger slayerTasksDone = new AtomicInteger(0);
		for (String username : group.getPlayers()) {
			World.forceGetPlayerByDisplay(username, member -> {
				if (member.getSkills().getTotalLevel() <= 1500)
					deservesIntermediate.set(false);
				if (member.getQuestManager().getQuestPoints() < 20)
					deservesIntermediate.set(false);
				dragonKills.set(member.getNumberKilledContaining("Dragon") + dragonKills.get());
				slayerTasksDone.set(CustomScripts.getSlayerTasksCompleted(member) + slayerTasksDone.get());
			});
		}
		if(dragonKills.get() < group.getPlayers().size() *500)
			deservesIntermediate.set(false);
		if(slayerTasksDone.get() < group.getPlayers().size()*50)
			deservesIntermediate.set(false);
		return deservesIntermediate.get();
	}


	public static NPCDropHandler addPrestigePerkDrops = new NPCDropHandler(null, new Object[]{"Dragon bones", "Frost dragon bones"}) {
		@Override
		public void handle(NPCDropEvent e) {
			if(e.getPlayer().getBool("DragonPerk") == true)
				if(Utils.random(0, 4) == 0)
					e.getItem().setId(e.getItem().getId() + 1);
		}
	};
}
