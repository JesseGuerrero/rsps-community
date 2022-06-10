package com.rs.rsps.jessecustom;

import com.rs.db.collection.GroupIronmanManager;
import com.rs.game.model.entity.player.Bank;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;

import java.util.ArrayList;
import java.util.List;

public class GroupIronMan {
	private String groupName;//This is the key
	private List<String> players = new ArrayList<>();
	private Bank bank = new Bank();

	public GroupIronMan(String groupName, Player founder) {
		this.groupName = groupName;
		this.players.add(0, founder.getUsername());
	}

	public List<String> getPlayers() {
		return players;
	}

	public Bank getBank() {
		return bank;
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
}
