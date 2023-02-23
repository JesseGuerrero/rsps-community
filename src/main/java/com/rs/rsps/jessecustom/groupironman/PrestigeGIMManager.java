package com.rs.rsps.jessecustom.groupironman;

import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.content.achievements.AchievementDef;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.handlers.LoginHandler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
@PluginEventHandler
public class PrestigeGIMManager {
	private int prestige = 0;

	public static final int
		NUBBY = 0,
		NOVICE = 1,
		INTERMEDIATE = 2,
		ADVANCED = 3,
		VETERAN = 4,
		COMPLETIONIST = 5;
	private transient GroupIronMan group;

	public static LoginHandler onLoginUpdatePrestige = new LoginHandler(e -> {
		if(GIM.hasTeam(e.getPlayer()))
			if(GIM.getGroupPrestige(e.getPlayer()) > GIM.getIndividualPrestige(e.getPlayer()))
				GIM.setIndividualPrestige(e.getPlayer(), GIM.getGroupPrestige(e.getPlayer()));
	});



	public void setGroup(GroupIronMan group) {
		this.group = group;
	}

	public void refreshPrestige(int prestigeInQuestion) {
		if(group.getSize() < 4) {
			return;
		}
		switch(prestigeInQuestion) {
			case NUBBY -> {
				if(prestige == prestigeInQuestion && deservesNovice())
					promoteToNovice();
			}
			case NOVICE -> {
				if(prestige == prestigeInQuestion && deservesIntermediate())
					promoteToIntermediate();
			}
			case INTERMEDIATE -> {
				if(prestige == prestigeInQuestion && deservesAdvanced())
					promoteToAdvanced();
			}
			case ADVANCED -> {
				if(prestige == prestigeInQuestion && deservesVeteran())
					promoteToVeteran();
			}
			case VETERAN -> {
				if(prestige == prestigeInQuestion && deservesCompletionist())
					promoteToCompletionist();
			}
			case COMPLETIONIST -> {
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
		group.updateDBGIM();
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

	private boolean deservesIntermediate() {
		AtomicBoolean deservesIntermediate = new AtomicBoolean(true);
//		AtomicInteger dragonKills = new AtomicInteger(0);
//		AtomicInteger slayerTasksDone = new AtomicInteger(0);
		for (String username : group.getPlayers()) {
			World.forceGetPlayerByDisplay(username, member -> {
				if (member.getSkills().getTotalLevel() <= 1500)
					deservesIntermediate.set(false);
				if (member.getQuestManager().getQuestPoints() < 20)
					deservesIntermediate.set(false);
//				dragonKills.set(member.getNumberKilledContaining("Dragon") + dragonKills.get());
//				slayerTasksDone.set(CustomScripts.getSlayerTasksCompleted(member) + slayerTasksDone.get());
			});
		}
//		if(dragonKills.get() < group.getPlayers().size() *500)
//			deservesIntermediate.set(false);
//		if(slayerTasksDone.get() < group.getPlayers().size()*50)
//			deservesIntermediate.set(false);
		return deservesIntermediate.get();
	}

	private boolean deservesAdvanced() {
		AtomicBoolean deservesAdvanced = new AtomicBoolean(true);
		AtomicBoolean allQuestsComplete = new AtomicBoolean(true);
		AtomicInteger numOfReaperKills = new AtomicInteger(0);
		AtomicInteger numOfPCGames = new AtomicInteger(0);
		AtomicBoolean completedFightCaves = new AtomicBoolean(false);
		World.forceGetPlayerGroupByDisplay(group.getPlayers(), players -> {
			for(Player player : players) {
				if(!player.getQuestManager().completedAllQuests())
					allQuestsComplete.set(false);
				numOfReaperKills.set(numOfReaperKills.get() + player.getCounterValue("Reaper assignments completed"));
				numOfPCGames.set(numOfPCGames.get() + player.getCounterValue("Pest control games completed"));
				if(player.getCounterValue("Fight Caves clears") >= 1)
					completedFightCaves.set(true);
			}
		});

		if(!allQuestsComplete.get())
			deservesAdvanced.set(false);
//		if(numOfReaperKills.get() < group.getSize()*10)
//			deservesAdvanced.set(false);
//		if(numOfPCGames.get() < group.getSize()*30)
//			deservesAdvanced.set(false);
//		if(!completedFightCaves.get())
			deservesAdvanced.set(false);
		return deservesAdvanced.get();
	}

	private boolean deservesVeteran() {
		AtomicBoolean deservesVeteran = new AtomicBoolean(true);
		AtomicBoolean allPlayersHaveTotalLevels = new AtomicBoolean(true);
		AtomicInteger numOfReaperKills = new AtomicInteger(0);
		AtomicInteger numOfPCGames = new AtomicInteger(0);
		AtomicInteger numOfFightKilnCleared = new AtomicInteger(0);
		World.forceGetPlayerGroupByDisplay(group.getPlayers(), players -> {
			for(Player player : players) {
				if(player.getSkills().getTotalLevel() < 2496)
					allPlayersHaveTotalLevels.set(false);
				numOfReaperKills.set(numOfReaperKills.get() + player.getCounterValue("Reaper assignments completed"));
				numOfPCGames.set(numOfPCGames.get() + player.getCounterValue("Pest control games completed"));
				numOfFightKilnCleared.set(numOfFightKilnCleared.get() + player.getCounterValue("Fight Kiln clears"));
			}
		});
		if(!allPlayersHaveTotalLevels.get())
			deservesVeteran.set(false);
//		if(numOfReaperKills.get() < group.getSize()*25)
//			deservesVeteran.set(false);
//		if(numOfPCGames.get() < group.getSize()*100)
//			deservesVeteran.set(false);
//		if(numOfFightKilnCleared.get() < 1)
//			deservesVeteran.set(false);
		return deservesVeteran.get();
	}

	private boolean deservesCompletionist() {
		AtomicBoolean deservesCompletionist = new AtomicBoolean(true);
		AtomicBoolean allPlayersHaveTotalLevels = new AtomicBoolean(true);
		AtomicInteger numOfReaperKills = new AtomicInteger(0);
		World.forceGetPlayerGroupByDisplay(group.getPlayers(), players -> {
			for(Player player : players) {
				for(int skillId = 0; skillId < 25; skillId++)
					if(!player.getSkills().is120(skillId))
						allPlayersHaveTotalLevels.set(false);
				numOfReaperKills.set(numOfReaperKills.get() + player.getCounterValue("Reaper assignments completed"));
			}
		});
		if(!allPlayersHaveTotalLevels.get())
			deservesCompletionist.set(false);
///		if(numOfReaperKills.get() < group.getSize()*100)
//			deservesCompletionist.set(false);
		return deservesCompletionist.get();
	}

	private void promoteToNovice() {
		group.broadcastMessage("<col=B3E0DC>Your team has ranked to \"Novice\" and each earned 5k loyalty points & 1 GIM bank!");
		for(String username : group.getPlayers()) {
			World.forceGetPlayerByDisplay(username, member -> {
				member.loyaltyPoints += 5000;
				WorldDB.getPlayers().save(member);
			});
		}
		setPrestige(NOVICE);
	}

	private void promoteToIntermediate() {
		group.broadcastMessage("<col=B3E0DC>Your team has ranked to \"Intermediate\" and each earned 15k loyalty points, 1 GIM bank!");
		for(String username : group.getPlayers()) {
			World.forceGetPlayerByDisplay(username, member -> {
				member.loyaltyPoints += 15_000;
				WorldDB.getPlayers().save(member);
			});
		}
		setPrestige(INTERMEDIATE);
	}

	private void promoteToAdvanced() {
		group.broadcastMessage("<col=B3E0DC>Your team has ranked to \"Advanced\" and each earned 30k loyalty points, 1 GIM bank!");
		for(String username : group.getPlayers()) {
			World.forceGetPlayerByDisplay(username, member -> {
				member.loyaltyPoints += 30_000;
				WorldDB.getPlayers().save(member);
			});
		}
		setPrestige(ADVANCED);
		World.sendWorldMessage(group.getGroupDisplayName() + " has completed Advanced prestige!", false);
	}

	private void promoteToVeteran() {
		group.broadcastMessage("<col=B3E0DC>Your team has ranked to \"Veteran\" and each earned 50k loyalty points, 1 GIM bank!");
		for(String username : group.getPlayers()) {
			World.forceGetPlayerByDisplay(username, member -> {
				member.loyaltyPoints += 50_000;
				WorldDB.getPlayers().save(member);
			});
		}
		setPrestige(VETERAN);
		World.sendWorldMessage(group.getGroupDisplayName() + " has completed Veteran prestige!", false);
	}

	private void promoteToCompletionist() {
		group.broadcastMessage("<col=B3E0DC>Your team has ranked to \"Completionist\" and each earned 120k loyalty points!");
		for(String username : group.getPlayers()) {
			World.forceGetPlayerByDisplay(username, member -> {
				member.loyaltyPoints += 120_000;
				WorldDB.getPlayers().save(member);
			});
		}
		setPrestige(COMPLETIONIST);
		World.sendWorldMessage(group.getGroupDisplayName() + " has completed Completionist prestige!", false);
	}




//	public static NPCDropHandler addPrestigePerkDrops = new NPCDropHandler(null, new Object[]{"Dragon bones", "Frost dragon bones"}) {
//		@Override
//		public void handle(NPCDropEvent e) {
//			if(e.getPlayer().getBool("DragonPerk") == true)
//				if(Utils.random(0, 4) == 0)
//					e.getItem().setId(e.getItem().getId() + 1);
//		}
//	};
}
