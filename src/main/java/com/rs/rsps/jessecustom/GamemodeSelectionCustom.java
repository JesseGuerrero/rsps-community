package com.rs.rsps.jessecustom;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.Dialogue;
import com.rs.game.engine.dialogue.statements.SimpleStatement;
import com.rs.game.model.entity.player.Player;
import com.rs.rsps.jessecustom.customscape.CustomScape;
import com.rs.rsps.jessecustom.groupironman.GIM;

public class GamemodeSelectionCustom extends Conversation {

	public GamemodeSelectionCustom(Player player) {
		super(player);
		
		addNext("start", new SimpleStatement("Welcome to Darkan, we will start by setting up your gamemode options."));
		addOptions("Which type of account would you like?", ops -> {
			ops.add("CustomScape", new Dialogue().addOptions("Is a custom account alright with you?", confirm -> {
				confirm.add("Yes.", () -> {
					player.setIronMan(false);
					CustomScape.setPlayerCustomScape(player);
					player.setChosenAccountType(true);
					player.getAppearance().generateAppearanceData();
				});
				confirm.add("No, let me choose again.", new Dialogue().addGotoStage("start", this));
			}));
//			ops.add("Ironman", new Dialogue().addOptions("Is an ironman account alright with you?", confirm -> {
//				confirm.add("Yes", () -> {
//					player.setIronMan(true);
//					player.setChosenAccountType(true);
//					player.getAppearance().generateAppearanceData();
//				});
//				confirm.add("No, let me choose again.", new Dialogue().addGotoStage("start", this));
//			}));
			ops.add("Group Iron Man", new Dialogue().addOptions("Is a GIM account alright with you?", confirm -> {
				confirm.add("Yes", () -> {
					GIM.setGIMMode(player, true);
					player.setChosenAccountType(true);
					player.getAppearance().generateAppearanceData();
				});
				confirm.add("No, let me choose again.", new Dialogue().addGotoStage("start", this));
			}));
			ops.add("CustomScape Group Iron Man", new Dialogue().addOptions("Is a GIM account alright with you?", confirm -> {
				confirm.add("Yes", () -> {
					GIM.setGIMMode(player, true);
					CustomScape.setPlayerCustomScape(player);
					player.setChosenAccountType(true);
					player.getAppearance().generateAppearanceData();
				});
				confirm.add("No, let me choose again.", new Dialogue().addGotoStage("start", this));
			}));
		});

		create();
	}

}
