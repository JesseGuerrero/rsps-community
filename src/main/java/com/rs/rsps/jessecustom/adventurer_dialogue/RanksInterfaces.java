package com.rs.rsps.jessecustom.adventurer_dialogue;

import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.rsps.jessecustom.CustomScripts;
import com.rs.rsps.jessecustom.groupironman.GIM;
import com.rs.rsps.jessecustom.groupironman.PrestigeGIMManager;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

	public static void intermediateInterface(Player player) {
		GIM.openGIM(GIM.getGIMTeamName(player), group -> {
			AtomicBoolean totalLevels = new AtomicBoolean(true);
			AtomicBoolean questPoints = new AtomicBoolean(true);
//			AtomicInteger dragonKills = new AtomicInteger(0);
//			AtomicInteger slayerTasksDone = new AtomicInteger(0);
			for (String username : group.getPlayers()) {
				World.forceGetPlayerByDisplay(username, member -> {
					if (member.getSkills().getTotalLevel() <= 1500)
						totalLevels.set(false);
					if (member.getQuestManager().getQuestPoints() < 20)
						questPoints.set(false);
//					dragonKills.set(member.getNumberKilledContaining("Dragon") + dragonKills.get());
//					slayerTasksDone.set(CustomScripts.getSlayerTasksCompleted(member) + slayerTasksDone.get());
				});
			}
//			boolean killedEnoughDragons = dragonKills.get() > group.getPlayers().size()*500;
//			boolean doneEnoughTasks = slayerTasksDone.get() > group.getPlayers().size()*50;
			ArrayList<String> lines = new ArrayList<>();
			lines.add((totalLevels.get() ? "<str>" : "") + "Achieve 1500 total level for all players");
			lines.add((questPoints.get() ? "<str>" : "") + "Has 20 QP for all players");
//			lines.add((killedEnoughDragons ? "<str>" : "") + "Killed a total of " + group.getPlayers().size()*500 + " dragons");
//			lines.add((doneEnoughTasks ? "<str>" : "") + "Has done " + group.getPlayers().size()*50 + " slayer tasks");
			lines.add("");
			lines.add("");
			lines.add("~~Rewards~~");
			lines.add("15k loyalty points each");
			lines.add("+1 GIM bank");
//			lines.add("25 % chance for dragon bones to drop as noted");
			if(totalLevels.get() && questPoints.get() || group.getPrestigeManager().getPrestige() >= 2) {
				lines.add("");
				lines.add("Congratulations, You pass this rank!");
			}
			player.getInterfaceManager().sendInterface(275);
			player.getPackets().sendRunScriptReverse(1207, lines.size());
			player.getPackets().setIFText(275, 1, "Intermediate Ranking");
			for (int i = 10; i < 289; i++)
				player.getPackets().setIFText(275, i, ((i - 10) >= lines.size() ? " " : lines.get(i - 10)));
		});

	}

	public static void advancedInterface(Player player) {
		GIM.openGIM(GIM.getGIMTeamName(player), group -> {
			AtomicBoolean allQuestsComplete = new AtomicBoolean(true);
			AtomicInteger numOfReaperKills = new AtomicInteger(0);
			AtomicInteger numOfPCGames = new AtomicInteger(0);
			AtomicBoolean completedFightCaves = new AtomicBoolean(false);
			World.forceGetPlayerGroupByDisplay(group.getPlayers(), players -> {
				for(Player p : players) {
					if(!p.getQuestManager().completedAllQuests())
						allQuestsComplete.set(false);
					System.out.println(p.getI("Reaper assignments completed"));
					numOfReaperKills.set(numOfReaperKills.get() + p.getCounterValue("Reaper assignments completed"));
					numOfPCGames.set(numOfPCGames.get() + p.getCounterValue("Pest control games completed"));
					if(p.getCounterValue("Fight Caves clears") >= 1)
						completedFightCaves.set(true);
				}
			});
			;
			ArrayList<String> lines = new ArrayList<>();
			lines.add((allQuestsComplete.get() ? "<str>" : "") + "All quests completed");
			lines.add((numOfReaperKills.get() >= group.getSize()*10 ? "<str>" : "") + "Has done " + group.getPlayers().size()*10 + " reaper tasks");
			lines.add((numOfPCGames.get() >= group.getSize()*300 ? "<str>" : "") + "Finished a total of " + group.getSize()*300 + " pest control games");
			lines.add((completedFightCaves.get() ? "<str>" : "") + "1 player completed fight caves");
			lines.add("");
			lines.add("");
			lines.add("~~Rewards~~");
			lines.add("30k loyalty points each");
			lines.add("+1 GIM bank");
			if(allQuestsComplete.get() && numOfReaperKills.get() < group.getSize()*10 &&  numOfPCGames.get() < group.getSize()*300 && completedFightCaves.get()
					|| group.getPrestigeManager().getPrestige() >= PrestigeGIMManager.ADVANCED) {
				lines.add("");
				lines.add("Congratulations, You pass this rank!");
			}
			player.getInterfaceManager().sendInterface(275);
			player.getPackets().sendRunScriptReverse(1207, lines.size());
			player.getPackets().setIFText(275, 1, "Advanced Ranking");
			for (int i = 10; i < 289; i++)
				player.getPackets().setIFText(275, i, ((i - 10) >= lines.size() ? " " : lines.get(i - 10)));
		});

	}

}
