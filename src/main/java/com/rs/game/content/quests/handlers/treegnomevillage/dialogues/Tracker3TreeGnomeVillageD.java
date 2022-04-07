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
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Tracker3TreeGnomeVillageD extends Conversation {
	private static final int NPC = 483;
	public Tracker3TreeGnomeVillageD(Player p) {
		super(p);
		int coordinate = p.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).getI("tracker3coordinate");
		if(coordinate == 0) {
			coordinate = Utils.randomInclusive(1, 4);
			p.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).setI("tracker3coordinate", coordinate);
		}
		switch(p.getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE)) {
			case NOT_STARTED, TALK_TO_MONTAI_ABOUT_WOOD, GET_WOOD, TALK_TO_MONTAI_ABOUT_TRACKERS -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello");
				addNPC(NPC, HeadE.CALM_TALK, "I can't talk now. Can't you see we're trying to win a battle here?");
			}
			case FIRE_BALLISTA -> {
				addPlayer(HeadE.HAPPY_TALKING, "Are you OK?");
				addNPC(NPC, HeadE.CALM_TALK, "OK? Who's OK? Not me! Hee hee!");
				addPlayer(HeadE.HAPPY_TALKING, "What's wrong?");
				addNPC(NPC, HeadE.CALM_TALK, "You can't see me, no one can. Monsters, demons, they're all around me!");
				addPlayer(HeadE.HAPPY_TALKING, "What do you mean?");
				addNPC(NPC, HeadE.CALM_TALK, "They're dancing, all of them, hee hee.");
				addPlayer(HeadE.HAPPY_TALKING, "Do you have the coordinate for the Khazard stronghold?");
				addNPC(NPC, HeadE.CALM_TALK, "Who holds the stronghold?");
				addPlayer(HeadE.HAPPY_TALKING, "What?");
				if(coordinate == 1)
					addNPC(NPC, HeadE.CALM_TALK, "Less than my hands", ()->{
						p.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).setB("tracker3found", true);
					});
				if(coordinate == 2)
					addNPC(NPC, HeadE.CALM_TALK, "More than my head less than my fingers", ()->{
						p.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).setB("tracker3found", true);
					});
				if(coordinate == 3)
					addNPC(NPC, HeadE.CALM_TALK, "More than we but less than our feet", ()->{
						p.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).setB("tracker3found", true);
					});
				if(coordinate == 4)
					addNPC(NPC, HeadE.CALM_TALK, "My legs and your legs", ()->{
						p.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).setB("tracker3found", true);
					});
				addPlayer(HeadE.HAPPY_TALKING, "You're mad.");
				addNPC(NPC, HeadE.CALM_TALK, "Dance with me, and Khazard's men are beat.");
				addPlayer(HeadE.HAPPY_TALKING, "I'll pray for you little man.");
				addNPC(NPC, HeadE.CALM_TALK, "All day we pray in the hay, hee hee.");

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
            e.getPlayer().startConversation(new Tracker3TreeGnomeVillageD(e.getPlayer()).getStart());
        }
    };
}
