package com.rs.game.content.quests.handlers.tribaltotem;

import static com.rs.game.content.quests.handlers.tribaltotem.TribalTotem.CHANGED_CRATE_ATTR;
import static com.rs.game.content.quests.handlers.tribaltotem.TribalTotem.GET_TOTEM;
import static com.rs.game.content.quests.handlers.tribaltotem.TribalTotem.REDIRECT_TELE_STONE;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class RPDTEmployeeTribalTotemD extends Conversation {
	private static final int NPC = 843;
	public RPDTEmployeeTribalTotemD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.TRIBAL_TOTEM)) {
		case REDIRECT_TELE_STONE -> {
			addNPC(NPC, HeadE.CALM_TALK, "Welcome to R.P.D.T.!");
			if(p.getQuestManager().getAttribs(Quest.TRIBAL_TOTEM).getB(CHANGED_CRATE_ATTR)) {
				addPlayer(HeadE.HAPPY_TALKING, "So, when are you going to deliver this crate?");
				addNPC(NPC, HeadE.CALM_TALK, "Well...I guess we could do it now...", ()->{
					p.getQuestManager().setStage(Quest.TRIBAL_TOTEM, GET_TOTEM);
					p.sendMessage("The crate is delivered to the mansion.");
				});
			}
			else
				addPlayer(HeadE.HAPPY_TALKING, "Thank you.");
		}
		default -> {
			addNPC(NPC, HeadE.CALM_TALK, "Welcome to R.P.D.T.!");
			addPlayer(HeadE.HAPPY_TALKING, "Thank you.");
		}
		}
	}



	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new RPDTEmployeeTribalTotemD(e.getPlayer()).getStart());
		}
	};
}
