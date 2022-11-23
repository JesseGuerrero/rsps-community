package com.rs.rsps.jessecustom.adventurer_dialogue;

import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.rsps.jessecustom.groupironman.GIM;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RanksInterfaces {
	int NPC = 1512;

	String[] ranks = new String[] {"Nubby", "Novice", "Intermediate", "Advanced", "Veteran", "Completionist"};

	public static void noviceInterface(Player player) {
		GIM.openGIM(GIM.getGIMTeamName(player), group -> {
			AtomicBoolean hasTotalLevel = new AtomicBoolean(true);
			AtomicBoolean hasQP = new AtomicBoolean(true);
			for(String username : group.getPlayers()) {
				World.forceGetPlayerByDisplay(username, member -> {
					if(member.getSkills().getTotalLevel() <= 500)
						hasTotalLevel.set(false);
					if(member.getQuestManager().getQuestPoints() < 10)
						hasQP.set(false);
				});
			}
			ArrayList<String> lines = new ArrayList<>();
			lines.add((hasTotalLevel.get() ? "<str>" : "") + "Achieve 500 total level for all players");
			lines.add((hasQP.get() ? "<str>" : "") + "Has 10 QP for all players");
			lines.add("");
			lines.add("");
			lines.add("~~Rewards~~");
			lines.add("5k loyalty points each");
			lines.add("+1 GIM bank");
			if(hasTotalLevel.get() && hasQP.get() || group.getPrestigeManager().getPrestige() >= 1) {
				lines.add("");
				lines.add("Congratulations, You pass this rank!");
			}
			player.getInterfaceManager().sendInterface(275);
			player.getPackets().sendRunScriptReverse(1207, lines.size());
			player.getPackets().setIFText(275, 1, "Novice Ranking");
			for (int i = 10; i < 289; i++)
				player.getPackets().setIFText(275, i, ((i - 10) >= lines.size() ? " " : lines.get(i - 10)));
		});

	}

}
