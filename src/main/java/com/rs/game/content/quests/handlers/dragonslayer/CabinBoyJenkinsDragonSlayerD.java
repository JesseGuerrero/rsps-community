package com.rs.game.content.quests.handlers.dragonslayer;

import static com.rs.game.content.quests.handlers.dragonslayer.DragonSlayer.JENKINS;
import static com.rs.game.content.quests.handlers.dragonslayer.DragonSlayer.NED_IS_CAPTAIN_ATTR;
import static com.rs.game.content.quests.handlers.dragonslayer.DragonSlayer.NOT_STARTED;
import static com.rs.game.content.quests.handlers.dragonslayer.DragonSlayer.OWNS_BOAT_ATTR;
import static com.rs.game.content.quests.handlers.dragonslayer.DragonSlayer.PREPARE_FOR_CRANDOR;
import static com.rs.game.content.quests.handlers.dragonslayer.DragonSlayer.QUEST_COMPLETE;
import static com.rs.game.content.quests.handlers.dragonslayer.DragonSlayer.REPORT_TO_OZIACH;
import static com.rs.game.content.quests.handlers.dragonslayer.DragonSlayer.TALK_TO_GUILDMASTER;
import static com.rs.game.content.quests.handlers.dragonslayer.DragonSlayer.TALK_TO_OZIACH;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CabinBoyJenkinsDragonSlayerD extends Conversation {
	public CabinBoyJenkinsDragonSlayerD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.DRAGON_SLAYER)) {
		case NOT_STARTED, TALK_TO_OZIACH, TALK_TO_GUILDMASTER, PREPARE_FOR_CRANDOR -> {
			if(p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(OWNS_BOAT_ATTR)) {
				addNPC(JENKINS, HeadE.CALM_TALK, "Ahoy! What d'ye think of yer ship then?");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("I'd like to inspect her some more.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I'd like to inspect her some more.")
								.addNPC(JENKINS, HeadE.CALM_TALK, "Aye."));
						if(p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(NED_IS_CAPTAIN_ATTR))
							option("Can you sail this ship to Crandor?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Can you sail this ship to Crandor?")
									.addNPC(JENKINS, HeadE.CALM_TALK, "Not me, sir! I'm just an 'umble cabin boy. You'll need to talk to Captain Ned."));
						else
							option("Can you sail this ship to Crandor?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Can you sail this ship to Crandor?")
									.addNPC(JENKINS, HeadE.CALM_TALK, "Not me, sir! I'm just an 'umble cabin boy. You'll need a proper cap'n.")
									.addPlayer(HeadE.HAPPY_TALKING, "Where can I find a captain?")
									.addNPC(JENKINS, HeadE.CALM_TALK, "The cap'ns round 'ere seem to be a mite scared of Crandor. I ask 'em why and they " +
											"just say it was afore my time, but there is one cap'n I reckon might 'elp.")
									.addNPC(JENKINS, HeadE.CALM_TALK, "I 'eard there's a retured 'un who lives in Draynor Village who's so desperate to sail again " +
											"'e'd take any job. I can't remember 'is name, but 'e lives in Draynor Village an' makes rope.")
									);
					}
				});
			}
			else {
				addNPC(JENKINS, HeadE.CALM_TALK, "Ahoy! What d'ye think of the ship then?");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("I might buy her!", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I might buy her!")
								.addNPC(JENKINS, HeadE.CALM_TALK, "Great!"));
						option("I'd like to inspect her some more.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I'd like to inspect her some more.")
								.addNPC(JENKINS, HeadE.CALM_TALK, "Aye."));
						option("Can you sail this ship?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Can you sail this ship?")
								.addNPC(JENKINS, HeadE.CALM_TALK, "Not me, sir! I'm just an 'umble cabin boy. You'll need a proper cap'n.")
								);
					}
				});

			}
		}
		case REPORT_TO_OZIACH, QUEST_COMPLETE ->  {
			addNPC(JENKINS, HeadE.FRUSTRATED, "You are the worst captain ever! I could have died!");
			addPlayer(HeadE.CALM_TALK, "But you didn't...");
			addNPC(JENKINS, HeadE.FRUSTRATED, "Aye, I didn't.");
			addPlayer(HeadE.CALM_TALK, "Would you take me to Crandor again?");
			addNPC(JENKINS, HeadE.FRUSTRATED, "Now that the dragon is dead I guess I would.");
			addPlayer(HeadE.HAPPY_TALKING, "Great!");
		}
		}


	}

	public static NPCClickHandler handleJenkinsDialogue = new NPCClickHandler(new Object[] { 748 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new CabinBoyJenkinsDragonSlayerD(e.getPlayer()).getStart());
		}
	};
}
