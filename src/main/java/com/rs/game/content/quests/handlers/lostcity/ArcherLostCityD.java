package com.rs.game.content.quests.handlers.lostcity;

import static com.rs.game.content.quests.handlers.lostcity.LostCity.ARCHER;
import static com.rs.game.content.quests.handlers.lostcity.LostCity.CHOP_DRAMEN_TREE;
import static com.rs.game.content.quests.handlers.lostcity.LostCity.FIND_ZANARIS;
import static com.rs.game.content.quests.handlers.lostcity.LostCity.NOT_STARTED;
import static com.rs.game.content.quests.handlers.lostcity.LostCity.QUEST_COMPLETE;
import static com.rs.game.content.quests.handlers.lostcity.LostCity.TALK_TO_LEPRAUCAN;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ArcherLostCityD extends Conversation {
	public ArcherLostCityD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.LOST_CITY)) {
		case NOT_STARTED -> {
			addPlayer(HeadE.HAPPY_TALKING, "Why are you guys hanging around here?");
			addNPC(ARCHER, HeadE.SKEPTICAL, "(ahem)... 'Guys'?");
			addPlayer(HeadE.HAPPY_TALKING, "Um... yeah, sorry about that. Why are you all standing around out here?");
			addNPC(ARCHER, HeadE.FRUSTRATED, "Well, that's really none of your business.");
		}
		case TALK_TO_LEPRAUCAN -> {
			addPlayer(HeadE.HAPPY_TALKING, "So I hear theres a leprechaun around here who can show me the way to Zanaris?");
			addNPC(ARCHER, HeadE.AMAZED_MILD, "... W-what? How did you...? No. You're wrong. Now go away.");
		}
		case CHOP_DRAMEN_TREE, FIND_ZANARIS, QUEST_COMPLETE ->  {
			addPlayer(HeadE.HAPPY_TALKING, "So you didn't find the entrance to Zanaris yet, huh?");
			addNPC(ARCHER, HeadE.FRUSTRATED, "Don't tell me a novice like YOU has found it!");
			addPlayer(HeadE.AMAZED_MILD, "Yep. Found it REALLY easily too.");
			addNPC(ARCHER, HeadE.FRUSTRATED, "...I cannot believe that someone like you could find the portal when experienced adventurers such as " +
					"ourselves could not.");
			addPlayer(HeadE.FRUSTRATED, "Believe what you want. Enjoy your little camp fire.");
		}
		}
	}

	public static NPCClickHandler handleArcherDialogue = new NPCClickHandler(new Object[] { ARCHER }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new ArcherLostCityD(e.getPlayer()).getStart());
		}
	};
}
