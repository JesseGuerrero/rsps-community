package com.rs.game.content.miniquests.handlers.abyss;

import java.util.ArrayList;

import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.miniquests.Miniquest;
import com.rs.game.content.miniquests.MiniquestHandler;
import com.rs.game.content.miniquests.MiniquestOutline;
import com.rs.game.content.skills.runecrafting.Abyss;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@MiniquestHandler(Miniquest.ENTER_THE_ABYSS)
@PluginEventHandler
public class EnterTheAbyss extends MiniquestOutline {

	@Override
	public int getCompletedStage() {
		return 4;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
			case 0 -> {
				lines.add("I can start this miniquest by speaking to the Zamorakian mage in the");
				lines.add("wilderness north of Edgeville.");
				lines.add("");
			}
			case 1 -> {
				lines.add("I spoke to the Zamorakian mage in the wilderness north of Edgeville.");
				lines.add("He told me I should meet him in Varrock.");
				lines.add("");
			}
			case 2 -> {
				lines.add("I was given a scrying orb and told to enter the rune essence mines");
				lines.add("via 3 different teleport methods to gather data on the teleport matrix.");
				lines.add("");
			}
			case 3 -> {
				lines.add("I gave the mage the scrying orb and he told me to speak to him after");
				lines.add("he's finished gathering the data from it. I should speak to him again.");
				lines.add("");
			}
			case 4 -> {
				lines.add("");
				lines.add("");
				lines.add("MINIQUEST COMPLETE!");
			}
			default -> lines.add("Invalid quest stage. Report this to an administrator.");
		}
		return lines;
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Skills.RUNECRAFTING, 1000);
		player.getInventory().addItemDrop(5509, 1);
		getQuest().sendQuestCompleteInterface(player, 5509, "1,000 Runecrafting XP", "Small pouch", "Access to the Abyss");
	}

	@Override
	public void updateStage(Player player) {
		player.getVars().setVar(492, player.getMiniquestManager().getStage(Miniquest.ENTER_THE_ABYSS));
	}

	public static NPCClickHandler handleMageOfZamorakWildy = new NPCClickHandler(new Object[] { 2257 }) {
		@Override
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
				case "Talk-to":
					switch(e.getPlayer().getMiniquestManager().getStage(Miniquest.ENTER_THE_ABYSS)) {
						case 0 -> e.getPlayer().startConversation(new Dialogue()
								.addPlayer(HeadE.CONFUSED, "Hello there, what are you doing here?")
								.addNPC(2257, HeadE.SECRETIVE, "I am researching an interesting phenomenon I call the 'Abyss' and selling runes.")
								.addPlayer(HeadE.CONFUSED, "Where do you get your runes?")
								.addNPC(2257, HeadE.FRUSTRATED, "This is no place to talk! Meet me at the Varrock Chaos Temple!", () -> e.getPlayer().getMiniquestManager().setStage(Miniquest.ENTER_THE_ABYSS, 1)));
						default -> e.getPlayer().startConversation(new Dialogue()
								.addNPC(2257, HeadE.FRUSTRATED, "This is no place to talk! Meet me at the Varrock Chaos Temple!"));
					}
					break;
				case "Trade":
					ShopsHandler.openShop(e.getPlayer(), "zamorak_mage_shop");
					break;
				case "Teleport":
					Abyss.teleport(e.getPlayer(), e.getNPC());
					break;
			}
		}
	};

	public static NPCClickHandler handleMageOfZamorakVarrock = new NPCClickHandler(new Object[] { 2260 }, new String[] { "Talk-to" }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new ZamorakMage(e.getPlayer()));
		}
	};
}
