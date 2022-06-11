package com.rs.db.collection;

import com.google.gson.JsonIOException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Indexes;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.db.DBItemManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Item;
import com.rs.lib.util.Logger;
import com.rs.rsps.jessecustom.GroupIronMan;
import org.bson.Document;

import java.io.IOException;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

public class GroupIronmanManager extends DBItemManager  {
	public GroupIronmanManager() {
		super("GIM");
	}

	@Override
	public void initCollection() {
		getDocs().createIndex(Indexes.text("groupName"));
	}

	public void getByGroupName(String groupName, Consumer<GroupIronMan> func) {
		execute(() -> {
			func.accept(getGroupSyncName(groupName));
		});
	}

	public void save(GroupIronMan group) {
		save(group, null);
	}

	public void save(GroupIronMan group, Runnable done) {
		execute(() -> {
			saveSync(group);
			if (done != null)
				done.run();
		});
	}

	public void saveSync(GroupIronMan group) {
		getDocs().findOneAndReplace(eq("groupName", group.getGroupName()), Document.parse(JsonFileManager.toJson(group)), new FindOneAndReplaceOptions().upsert(true));
	}

	public GroupIronMan getGroupSyncName(String groupName) {
		Document accDoc = getDocs().find(eq("groupName", groupName)).first();
		if (accDoc == null)
			return null;
		try {
			return JsonFileManager.fromJSONString(JsonFileManager.toJson(accDoc), GroupIronMan.class);
		} catch (JsonIOException | IOException e) {
			Logger.handle(e);
			return null;
		}
	}

	public void remove(GroupIronMan group, Runnable done) {
		execute(() -> {
			removeSync(group);
			if (done != null)
				done.run();
		});
	}

	public void removeSync(GroupIronMan group) {
		getDocs().findOneAndDelete(Filters.eq("groupName", group.getGroupName()));
	}

	public boolean groupExists(String groupName) {
		return getGroupSyncName(groupName) != null;
	}

}
