package com.rs.rsps.teleports;

import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;

import java.util.function.Consumer;

public class Teleport {
	
	private String name;
	private WorldTile tile;
	private Consumer<Player> onTeleport;
	
	public Teleport(String name, WorldTile tile, Consumer<Player> onTeleport) {
		this.name = name;
		this.tile = tile;
		this.onTeleport = onTeleport;
	}
	
	public Teleport(String name, WorldTile tile) {
		this(name, tile, null);
	}
	
	public String getName() {
		return name;
	}

	public WorldTile getTile() {
		return tile;
	}
	
	public void teleport(Player player) {
		Magic.sendNormalTeleportSpell(player, tile, onTeleport);
	}

}
