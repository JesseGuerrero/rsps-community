package com.rs.game.content.quests.handlers.treegnomevillage.dialogues;

import static com.rs.game.content.quests.handlers.treegnomevillage.TreeGnomeVillage.FIRE_BALLISTA;
import static com.rs.game.content.quests.handlers.treegnomevillage.TreeGnomeVillage.GET_WOOD;
import static com.rs.game.content.quests.handlers.treegnomevillage.TreeGnomeVillage.KILL_WARLORD;
import static com.rs.game.content.quests.handlers.treegnomevillage.TreeGnomeVillage.NOT_STARTED;
import static com.rs.game.content.quests.handlers.treegnomevillage.TreeGnomeVillage.ORB1;
import static com.rs.game.content.quests.handlers.treegnomevillage.TreeGnomeVillage.QUEST_COMPLETE;
import static com.rs.game.content.quests.handlers.treegnomevillage.TreeGnomeVillage.TALK_TO_MONTAI_ABOUT_TRACKERS;
import static com.rs.game.content.quests.handlers.treegnomevillage.TreeGnomeVillage.TALK_TO_MONTAI_ABOUT_WOOD;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Tracker1TreeGnomeVillageD extends Conversation {
	private static final int NPC = 481;
	public Tracker1TreeGnomeVillageD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE)) {
			case NOT_STARTED, TALK_TO_MONTAI_ABOUT_WOOD, GET_WOOD, TALK_TO_MONTAI_ABOUT_TRACKERS -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello");
				addNPC(NPC, HeadE.CALM_TALK, "I can't talk now. Can't you see we're trying to win a battle here?");
			}
			case FIRE_BALLISTA -> {
				addPlayer(HeadE.HAPPY_TALKING, "Do you know the coordinates of the Khazard stronghold?");
				addNPC(NPC, HeadE.CALM_TALK, "I managed to get one, although it wasn't easy.");
				addPlayer(HeadE.HAPPY_TALKING, "Well done.", ()->{
					player.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).setB("tracker1found", true);
				});
				addNPC(NPC, HeadE.CALM_TALK, "The other two tracker gnomes should have the other coordinates if they're still alive.");
				addPlayer(HeadE.HAPPY_TALKING, "OK, take care.");
			}
			case ORB1, KILL_WARLORD -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello.");
				addNPC(NPC, HeadE.CALM_TALK, "Now we have the orb I'm much better. They won't stand a chance without it.");
			}
			case QUEST_COMPLETE ->  {
				addPlayer(HeadE.HAPPY_TALKING, "How are you tracker?");
				addNPC(NPC, HeadE.CALM_TALK, "Now we have the orb I'm much better. They won't stand a chance without it.");

			}
		}
	}

    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().startConversation(new Tracker1TreeGnomeVillageD(e.getPlayer()).getStart());
        }
    };
}
