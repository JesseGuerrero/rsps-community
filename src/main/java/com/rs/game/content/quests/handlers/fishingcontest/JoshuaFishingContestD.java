package com.rs.game.content.quests.handlers.fishingcontest;

import static com.rs.game.content.quests.handlers.fishingcontest.FishingContest.DO_ROUNDS;
import static com.rs.game.content.quests.handlers.fishingcontest.FishingContest.ENTER_COMPETITION;
import static com.rs.game.content.quests.handlers.fishingcontest.FishingContest.GIVE_TROPHY;
import static com.rs.game.content.quests.handlers.fishingcontest.FishingContest.NOT_STARTED;
import static com.rs.game.content.quests.handlers.fishingcontest.FishingContest.QUEST_COMPLETE;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class JoshuaFishingContestD extends Conversation {
	private static final int NPC = 229;
	public JoshuaFishingContestD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.FISHING_CONTEST)) {
		case NOT_STARTED, ENTER_COMPETITION -> {
			addPlayer(HeadE.HAPPY_TALKING, "Hi, what are you doing here?");
			addNPC(NPC, HeadE.CALM_TALK, "I am waiting for the fishing contest to start.");
			addPlayer(HeadE.HAPPY_TALKING, "Oh.");
		}
		case DO_ROUNDS -> {
			addNPC(NPC, HeadE.CALM_TALK, "Yeah? What you want?");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Um... nothing really..", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Um... nothing really..")
							.addNPC(NPC, HeadE.CALM_TALK, "Quit bugging me then, "+ p.getPronoun("dude", "doll") + "! I got me some fish to " +
									"catch!")
							);
					option("Can I fish here instead of you?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Can I fish here instead of you?")
							.addNPC(NPC, HeadE.CALM_TALK, "Nuh uh dude. Less talk, more fishing!")
							);
					option("Do you have any tips for me?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Do you have any tips for me?")
							.addNPC(NPC, HeadE.CALM_TALK, "Who's Grampa Jack " + p.getPronoun("he", "she") + " says! He won this " +
									"competition four years in a row! He lives " +
									"in the house just outside the gate.")
							);
				}
			});


		}
		case GIVE_TROPHY, QUEST_COMPLETE ->  {
			addNPC(NPC, HeadE.CALM_TALK, "Congratulations on the win " + p.getPronoun("dude", "lady") + "!");
			addPlayer(HeadE.HAPPY_TALKING, "Thanks!");
		}
		}
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new JoshuaFishingContestD(e.getPlayer()).getStart());
		}
	};

	public static NPCClickHandler handleJoshuaSpot = new NPCClickHandler(true, new Object[] { 236 }) {
		@Override
		public void handle(NPCClickEvent e) {
			Player p = e.getPlayer();
			NPC npc = e.getNPC();
			if(npc.getRegionId() == 10549) {
				e.getNPC().resetDirection();
				if (p.getQuestManager().getStage(Quest.FISHING_CONTEST) >= GIVE_TROPHY) {
					p.sendMessage("Nothing interesting happens...");
					return;
				}
				p.startConversation(new Conversation(p) {
					{
						addNPC(NPC, HeadE.CALM_TALK, "Hey dude! This is my spot!");
						addPlayer(HeadE.HAPPY_TALKING, "Um... can I fish here then please?");
						addNPC(NPC, HeadE.CALM_TALK, "No way "+ p.getPronoun("man", "lady") + ", I got a good feeling about this spot!");
						create();
					}
				});
			}

		}
	};
}
