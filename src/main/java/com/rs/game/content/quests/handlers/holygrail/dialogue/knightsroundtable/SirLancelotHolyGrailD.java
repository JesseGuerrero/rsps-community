package com.rs.game.content.quests.handlers.holygrail.dialogue.knightsroundtable;

import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GIVE_AURTHUR_HOLY_GRAIL;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GO_TO_ENTRANA;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GO_TO_MCGRUBOR;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.QUEST_COMPLETE;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.SPEAK_TO_FISHER_KING;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.SPEAK_TO_PERCIVAL;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class SirLancelotHolyGrailD extends Conversation {
	private static final int NPC = 239;
	public SirLancelotHolyGrailD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case GO_TO_ENTRANA, GO_TO_MCGRUBOR, SPEAK_TO_FISHER_KING, SPEAK_TO_PERCIVAL, GIVE_AURTHUR_HOLY_GRAIL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Greetings! I am Sir Lancelot, the greatest Knight in the land! What do you want?");
				addPlayer(HeadE.HAPPY_TALKING, "I am questing for the Holy Grail.");
				addNPC(NPC, HeadE.LAUGH, "The Grail? Ha! Frankly, little man, you're not in that league.");
				addPlayer(HeadE.FRUSTRATED, "Why do you say that?");
				addNPC(NPC, HeadE.TALKING_ALOT, "You got lucky with freeing Merlin but there's no way a puny wannabe like you is going to find the Holy Grail " +
						"where so many others have failed.");
				addPlayer(HeadE.CALM_TALK, "We'll see about that.");
			}
			case QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.TALKING_ALOT, "There is no way a wannabe like you got the Holy Grail without help. You lucky knight.");
				addPlayer(HeadE.FRUSTRATED, "I am sure you would have gotten it if you tried...");
				addNPC(NPC, HeadE.CALM_TALK, "Yup...");
			}
		}
	}
}
