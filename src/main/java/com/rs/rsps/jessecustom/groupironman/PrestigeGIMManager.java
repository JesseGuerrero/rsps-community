package com.rs.rsps.jessecustom.groupironman;

import com.rs.db.WorldDB;
import com.rs.game.World;

import java.util.concurrent.atomic.AtomicBoolean;

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


}
