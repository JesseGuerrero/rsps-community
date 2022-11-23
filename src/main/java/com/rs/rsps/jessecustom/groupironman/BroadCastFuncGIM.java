package com.rs.rsps.jessecustom.groupironman;

import com.rs.game.model.entity.player.Player;

import java.util.List;

public class BroadCastFuncGIM implements Runnable {
	private List<Player> players;
	public BroadCastFuncGIM() {

	}
	@Override
	public void run() {

	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public List<Player> getPlayers() {
		return players;
	}
}
