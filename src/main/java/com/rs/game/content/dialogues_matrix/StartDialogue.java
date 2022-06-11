// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.dialogues_matrix;

public class StartDialogue extends MatrixDialogue {
	@Override
	public void start() {
		sendDialogue("This is a dedicated Group IronMan server");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			sendDialogue("Edgeville will be your home...");
			stage = 2;
		} else if (stage == 2)
			player.setIronMan(true);
			player.setChosenAccountType(true);
			player.getAppearance().generateAppearanceData();
			end();
	}

	@Override
	public void finish() {

	}

}
