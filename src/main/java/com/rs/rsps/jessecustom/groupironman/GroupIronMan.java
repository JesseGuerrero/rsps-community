package com.rs.rsps.jessecustom.groupironman;

import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.model.entity.player.Bank;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupIronMan {
	private String groupName;//This is the key
	private List<String> players = new ArrayList<>();

	private PrestigeGIMManager prestigeManager = new PrestigeGIMManager();

	private Map<String, Object> savingAttributes;
	private Bank bank1 = new Bank();
	private Bank bank2 = new Bank();
	private Bank bank3 = new Bank();
	private boolean bank1Open = false;
	private boolean bank2Open = false;
	private boolean bank3Open = false;

	public GroupIronMan(String groupName, Player founder) {
		this.groupName = groupName;
		this.players.add(0, founder.getUsername());
	}

	public void init() {
		if(this.prestigeManager == null)
			this.prestigeManager = new PrestigeGIMManager();
		this.prestigeManager.setGroup(this);
	}

	public List<String> getPlayers() {
		return players;
	}

	public PrestigeGIMManager getPrestigeManager() {
		return prestigeManager;
	}

	public Bank getBank1() {
		return bank1;
	}
	public Bank getBank2() {
		return bank2;
	}
	public Bank getBank3() {
		return bank3;
	}

	public boolean isBank1Open() {
		return bank1Open;
	}

	public boolean isBank2Open() {
		return bank2Open;
	}

	public boolean isBank3Open() {
		return bank3Open;
	}

	public String getGroupName() {
		return groupName;
	}

	public int getSize() {
		return players.size();
	}

	public void addPlayer(Player p) {
		players.add(p.getUsername());
	}

	public void setGroupName(String groupName) {
		for(String member : getPlayers())
			World.forceGetPlayerByDisplay(member, player -> {
				player.save("GIM Team", groupName);
			});
		this.groupName = groupName;
	}

	public void setBank2Open(boolean bank2Open) {
		this.bank2Open = bank2Open;
	}

	public void setBank3Open(boolean bank3Open) {
		this.bank3Open = bank3Open;
	}

	public void setBank1Open(boolean bank1Open) {
		this.bank1Open = bank1Open;
	}

	public void broadcastMessage(String message) {
		for(String member : players) {
			Player p = World.getPlayerByUsername(member);
			if(p != null)
				p.sendMessage(message);
		}
	}

	public boolean isGroupLeader(Player p) {
		return players.get(0).equalsIgnoreCase(p.getUsername());
	}

	public void save(String key, Object value) {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<>();
		savingAttributes.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getO(String name) {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<>();
		if (savingAttributes.get(name) == null)
			return null;
		return (T) savingAttributes.get(name);
	}

	public Object get(String key) {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<>();
		if (savingAttributes.get(key) != null)
			return savingAttributes.get(key);
		return false;
	}

	public int getI(String key) {
		return getI(key, -1);
	}

	public int getI(String key, int def) {
		Object val = get(key);
		if (val == Boolean.FALSE)
			return def;
		return (int) (val instanceof Integer ? (int) val : (double) val);
	}

	public long getL(String key) {
		return getL(key, -1L);
	}

	public long getL(String key, long def) {
		Object val = get(key);
		if (val == Boolean.FALSE)
			return def;
		return (val instanceof Long ? (long) val : ((Double) val).longValue());
	}

	public boolean getBool(String key) {
		Object val = get(key);
		if (val == Boolean.FALSE)
			return false;
		return (Boolean) val;
	}

	public Map<String, Object> getSavingAttributes() {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<>();
		return savingAttributes;
	}

	public void delete(String key) {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<>();
		savingAttributes.remove(key);
	}

	public void updateDBGIM() {
		WorldDB.getGIMS().saveSync(this);
	}
}
