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
package com.rs.db.model;

import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.rsps.jessecustom.groupironman.GroupIronMan;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GIMHighscore {

	private String groupName;
	private String groupDisplayName;
	private int averageTotalLevel;
	private long totalXp;
	private int prestige;

	public GIMHighscore() {
	}


	public int getAverageTotalLevel() {
		return averageTotalLevel;
	}

	public void setAverageTotalLevel(int averageTotalLevel) {
		this.averageTotalLevel = averageTotalLevel;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setGroupDisplayName(String groupDisplayName) {
		this.groupDisplayName = groupDisplayName;
	}

	public void setTotalXp(long totalXp) {
		this.totalXp = totalXp;
	}

	public void setPrestige(int prestige) {
		this.prestige = prestige;
	}

	public long getTotalXp() {
		return totalXp;
	}

	public int getPrestige() {
		return prestige;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getGroupDisplayName() {
		return groupDisplayName;
	}
}
