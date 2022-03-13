package com.rs.game.content.quests.handlers.merlinscrystal;

import static com.rs.game.content.quests.handlers.merlinscrystal.MerlinsCrystal.CONFRONT_KEEP_LA_FAYE;
import static com.rs.game.content.quests.handlers.merlinscrystal.MerlinsCrystal.THE_BLACK_CANDLE;

import com.rs.game.World;
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
public class MorganMerlinsCrystalD extends Conversation {
	private final static int NPC = 248;
	private final static String KNOWS_EXCALIBUR_ATTR = "KNOWS_E";
	private final static String KNOWS_ABOUT_ENCANTATION_ATTR = "KNOWS_ABOUT_ENC";

	public MorganMerlinsCrystalD(Player p) {
		super(p);
		if(p.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) == CONFRONT_KEEP_LA_FAYE) {
			addNPC(NPC, HeadE.CALM_TALK, "STOP! Please... spare my son");
			addPlayer(HeadE.HAPPY_TALKING, "Tell me how to untrap Merlin and I might.");
			addNPC(NPC, HeadE.CALM_TALK, "You have guessed correctly that I'm responsible for that. I suppose I can live with that fool Merlin " +
					"being loose for the sake of my son.");
			addNPC(NPC, HeadE.CALM_TALK, "Setting him free won't be easy though. You will need to find a magic symbol as close to the crystal as you " +
					"can find. You will then need to drop some bat's bones on the magic symbol while holding a lit black candle.");
			addNPC(NPC, HeadE.CALM_TALK, "This will summon a mighty spirit named Thrantax. You will need to bind him with magic words. Then you will need the " +
					"sword Excalibur with which the spell was bound in order to shatter the crystal.");
			addNext(() -> {
				p.startConversation(new MorganMerlinsCrystalD(p, true).getStart());
			});
		}else if(p.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) == THE_BLACK_CANDLE) {
            addNPC(NPC, HeadE.CALM_TALK, "That's all I know, I swear!");
            addPlayer(HeadE.SECRETIVE, "...");
            addNPC(NPC, HeadE.CALM_TALK, "...");

        } else {
			addNPC(NPC, HeadE.FRUSTRATED, "What do you want?");
			addPlayer(HeadE.CALM_TALK, "Nothing");
		}
	}

	public MorganMerlinsCrystalD(Player p, boolean filler) {
		super(p);
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("So where can I find Excalibur?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "So where can I find Excalibur?")
						.addNPC(NPC, HeadE.CALM_TALK, "The lady of the lake has it. I don't know if she'll give it to you though, she can be rather temperamental.",
								()-> {p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).setB(KNOWS_EXCALIBUR_ATTR, true);})
						.addNext(()->{
                            if(p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB(KNOWS_EXCALIBUR_ATTR) && p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB(KNOWS_ABOUT_ENCANTATION_ATTR))
                                p.getQuestManager().setStage(Quest.MERLINS_CRYSTAL, THE_BLACK_CANDLE);
                            p.startConversation(new MorganMerlinsCrystalD(p, true).getStart());
                        }));
				option("What are the magic words?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "What are the magic words?")
						.addNPC(NPC, HeadE.CALM_TALK, "You will find the magic words at the base of one of the chaos altars. Which chaos altar I cannot remember.",
								()-> {p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).setB(KNOWS_ABOUT_ENCANTATION_ATTR, true);})
						.addNext(()->{
                            if(p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB(KNOWS_EXCALIBUR_ATTR) && p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB(KNOWS_ABOUT_ENCANTATION_ATTR))
                                p.getQuestManager().setStage(Quest.MERLINS_CRYSTAL, THE_BLACK_CANDLE);
                            p.startConversation(new MorganMerlinsCrystalD(p, true).getStart());
                        }));
				if(p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB(KNOWS_EXCALIBUR_ATTR) && p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB(KNOWS_ABOUT_ENCANTATION_ATTR))
					option("Ok, I will go do all that.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Ok, I will go do all that.")
							.addSimple("Morgan Le Faye vanishes.", ()->{
								p.getQuestManager().setStage(Quest.MERLINS_CRYSTAL, THE_BLACK_CANDLE);
								for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
									if(npc.getId() == NPC)
										npc.finish();
							})
							);
			}
		});
	}

	public static NPCClickHandler handleMorganDialogue = new NPCClickHandler(new Object[] { NPC }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new MorganMerlinsCrystalD(e.getPlayer()).getStart());
		}
	};

}
