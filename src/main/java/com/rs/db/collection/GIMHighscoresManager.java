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
package com.rs.db.collection;

import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.rs.db.model.GIMHighscore;
import com.rs.db.model.Highscore;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.db.DBItemManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Rights;
import com.rs.lib.util.Logger;
import com.rs.rsps.jessecustom.groupironman.BroadCastFuncGIM;
import com.rs.rsps.jessecustom.groupironman.GIM;
import org.bson.Document;

import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

public class GIMHighscoresManager extends DBItemManager {

	public GIMHighscoresManager() {
		super("GIMHighscores");
	}

	@Override
	public void initCollection() {
		getDocs().createIndex(Indexes.compoundIndex(Indexes.text("groupDisplayName"), Indexes.text("groupName")));
		getDocs().createIndex(Indexes.descending("averageTotalLevel", "totalXp"));
	}

	public void save(Player player) {
		save(player, null);
	}

	public void save(Player player, Runnable done) {
		execute(() -> {
			saveSync(player);
			if (done != null)
				done.run();
		});
	}

	public void saveSync(Player player) {
		if (player.hasRights(Rights.OWNER) || player.hasRights(Rights.DEVELOPER) || player.hasRights(Rights.ADMIN) || player.getSkills().getTotalXp() < 10_000)
			return;
		try {
			if(GIM.hasTeam(player)) {
				GIM.openGIM(GIM.getGIMTeamName(player), group -> {
					group.broadcastFuncToAllPlayers(new BroadCastFuncGIM() {
						int totalAverage = 0;
						int totalXP = 0;
						@Override
						public void run() {
							GIMHighscore highscore = new GIMHighscore();
							highscore.setGroupName(group.getGroupName());
							highscore.setGroupDisplayName(group.getGroupDisplayName());
							for(Player member : getPlayers()) {
								totalAverage += member.getSkills().getTotalLevel();
								totalXP += member.getSkills().getTotalXp();
							}
							totalAverage = totalAverage/getPlayers().size();
							highscore.setAverageTotalLevel(totalAverage);
							highscore.setTotalXp(totalXP);
							highscore.setPrestige(group.getPrestigeManager().getPrestige());
							getDocs().findOneAndReplace(eq("groupName", GIM.getGIMTeamName(player)), Document.parse(JsonFileManager.toJson(highscore)), new FindOneAndReplaceOptions().upsert(true));
						}
					});
				});
			}
		} catch (Exception e) {
			Logger.handle(GIMHighscoresManager.class, "saveSync", e);
		}
	}
}
