package com.rs.rsps.jessecustom;

import com.rs.game.World;
import com.rs.game.model.entity.player.Bank;
import com.rs.game.model.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GroupIronMan {
	private String groupName;//This is the key
	private List<String> players = new ArrayList<>();
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

	public List<String> getPlayers() {
		return players;
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
}
