package com.rs.rsps.jessecustom;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.statements.SimpleStatement;
import com.rs.game.model.entity.player.Player;

public class GamemodeSelectionCustom extends Conversation {

	public GamemodeSelectionCustom(Player player) {
		super(player);
		
		addNext("start", new SimpleStatement("Welcome to Darkan, we will start by setting up your gamemode options."));
		addOptions("Which type of account would you like?", ops -> {
			ops.add("Normal", new Dialogue().addOptions("Is a normal account alright with you?", confirm -> {
				confirm.add("Yes.", () -> {
					player.setIronMan(false);
					player.setChosenAccountType(true);
					player.getAppearance().generateAppearanceData();
				});
				confirm.add("No, let me choose again.", new Dialogue().addGotoStage("start", this));
			}));
			ops.add("Ironman", new Dialogue().addOptions("Is an ironman account alright with you?", confirm -> {
				confirm.add("Yes", () -> {
					player.setIronMan(true);
					player.setChosenAccountType(true);
					player.getAppearance().generateAppearanceData();
				});
				confirm.add("No, let me choose again.", new Dialogue().addGotoStage("start", this));
			}));
			ops.add("Group Iron Man", new Dialogue().addOptions("Is a GIM account alright with you?", confirm -> {
				confirm.add("Yes", () -> {
					player.setIronMan(true);
					player.setGIM(true);
					player.setChosenAccountType(true);
					player.getAppearance().generateAppearanceData();
				});
				confirm.add("No, let me choose again.", new Dialogue().addGotoStage("start", this));
			}));
		});

		create();
	}

}
