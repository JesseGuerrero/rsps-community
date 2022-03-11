package com.rs.rsps;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.controllers.WildernessController;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.rsps.teleports.BossTeleport;
import com.rs.rsps.teleports.SlayerTeleport;
import com.rs.rsps.teleports.Teleport;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Home {
	
	@ServerStartupEvent
	public static void spawnNPCs() {
		/* Task master */
		//spawnNPC(14858, new WorldTile(3090, 3494, 0), "Cadet Cassandra", Direction.WEST, false);
		
		/* Slayer Masters */
		spawnNPC(8480, new WorldTile(3091, 3487, 0), Direction.SOUTH, false);
		spawnNPC(8481, new WorldTile(3092, 3487, 0), Direction.SOUTH, false);
		spawnNPC(1597, new WorldTile(3093, 3487, 0), Direction.SOUTH, false);
		spawnNPC(1598, new WorldTile(3094, 3487, 0), Direction.SOUTH, false);
		spawnNPC(7779, new WorldTile(3095, 3487, 0), Direction.SOUTH, false);
		spawnNPC(8466, new WorldTile(3096, 3487, 0), Direction.SOUTH, false);
		spawnNPC(9085, new WorldTile(3097, 3487, 0), Direction.SOUTH, false);
		spawnNPC(15661, new WorldTile(3082, 3490, 0), Direction.EAST, false);

		/* GE Clarks */
		spawnNPC(2240, new WorldTile(3096, 3492, 0), Direction.WEST, false);
		spawnNPC(2241, new WorldTile(3096, 3490, 0), Direction.WEST, false);
		spawnNPC(2593, new WorldTile(3096, 3494, 0), Direction.NORTHWEST, false);
		spawnNPC(2240, new WorldTile(3098, 3494, 0), Direction.NORTH, false);
		spawnNPC(2241, new WorldTile(3096, 3488, 0), Direction.WEST, false);

		/* Loyalty point shop */
		spawnNPC(13727, new WorldTile(3091, 3505, 0), Direction.SOUTH, false);
		
		spawnObject(2640, new WorldTile(3086, 3483, 0));
	}

	public static ObjectClickHandler handleShops = new ObjectClickHandler(new Object[] { 18789 }, new WorldTile(3095, 3499, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().startConversation(new Dialogue().addOptions("Which shop would you like to see?", new Options() {
				@Override
				public void create() {
					option("Melee shop", () -> ShopsHandler.openShop(e.getPlayer(), "gear_shop_melee"));
					option("Range shop", () -> ShopsHandler.openShop(e.getPlayer(), "gear_shop_range"));
					option("Magic shop", () -> ShopsHandler.openShop(e.getPlayer(), "gear_shop_magic"));
				}
			}));
		}
	};

	private static Teleport[] PVP_TELEPORTS = {
			new Teleport("Mage bank (SAFE)", new WorldTile(2539, 4715, 0)),
			new Teleport("Wests (level 10)", new WorldTile(2976, 3597, 0), p -> p.getControllerManager().startController(new WildernessController())),
			new Teleport("Chaos altar (level 12)", new WorldTile(3229, 3609, 0), p -> p.getControllerManager().startController(new WildernessController())),
			new Teleport("Dark warrior's fortress (level 15)", new WorldTile(3007, 3632, 0), p -> p.getControllerManager().startController(new WildernessController())),

			new Teleport("Revenant caves (level 18)", new WorldTile(3082, 10058, 0), p -> p.getControllerManager().startController(new WildernessController())),
			new Teleport("Easts (level 19)", new WorldTile(3350, 3667, 0), p -> p.getControllerManager().startController(new WildernessController())),
			new Teleport("West Lava Maze/KBD (level 40)", new WorldTile(3028, 3839, 0), p -> p.getControllerManager().startController(new WildernessController())),
			new Teleport("Red Dragon Isle (level 43)", new WorldTile(3202, 3860, 0), p -> p.getControllerManager().startController(new WildernessController())),

			new Teleport("Agility course (level 49)", new WorldTile(2997, 3909, 0), p -> p.getControllerManager().startController(new WildernessController())),
			null,
			null,
			null
	};

	public static ObjectClickHandler pvpPortal = new ObjectClickHandler(new Object[] { 46935 }, new WorldTile(3093, 3506, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().startConversation(new Dialogue()
					.addSimple("<col=FF0000><shad=000000>THIS PORTAL LEADS TO WILDERNESS LOCATIONS. USE AT YOUR OWN RISK. THE LEVEL OF WILDERNESS YOU WILL BE TELEPORTED TO ARE DISPLAYED ON EACH TELEPORT.")
					.addOptions("Where would you like to go?", new Options() {
						@Override
						public void create() {
							for (Teleport t : PVP_TELEPORTS)
								if (t != null)
									option(t.getName(), new Dialogue().addNext(() -> t.teleport(e.getPlayer())));
								else
									option("Nowhere.");
						}
					}));
		}
	};

	public static ObjectClickHandler bossPortal = new ObjectClickHandler(new Object[] { 46933 }, new WorldTile(3083, 3492, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getBossTask() == null || e.getPlayer().getBossTask().getTask() == null) {
				e.getPlayer().sendMessage("You don't have a boss task to teleport to currently.");
				return;
			}
			Teleport[] teleports = BossTeleport.forBoss(e.getPlayer().getBossTask().getTask()).getTeleports();
			if (teleports == null || teleports.length <= 0) {
				e.getPlayer().sendMessage("Your task doesn't have any teleports configured for it. Suggest it in discord.");
				return;
			}
			e.getPlayer().startConversation(new Dialogue().addOptions("Where would you like to go?", new Options() {
				@Override
				public void create() {
					for (Teleport t : teleports)
						option(t.getName(), new Dialogue().addNext(() -> t.teleport(e.getPlayer())));
					option("Nowhere.");
				}
			}));
		}
	};

	public static ObjectClickHandler slayerPortal = new ObjectClickHandler(new Object[] { 46934 }, new WorldTile(3095, 3483, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getSlayer().getTask() == null) {
				e.getPlayer().sendMessage("You don't have a slayer task to teleport to currently.");
				return;
			}
			Teleport[] teleports = SlayerTeleport.forMonster(e.getPlayer().getSlayer().getTask().getMonster()).getTeleports();
			if (teleports == null || teleports.length <= 0) {
				e.getPlayer().sendMessage("Your task doesn't have any teleports configured for it. Suggest it in discord.");
				return;
			}
			e.getPlayer().startConversation(new Dialogue().addOptions("Where would you like to go?", new Options() {
				@Override
				public void create() {
					for (Teleport t : teleports)
						option(t.getName(), new Dialogue().addNext(() -> t.teleport(e.getPlayer())));
					option("Nowhere.");
				}
			}));
		}
	};

	public static NPC spawnNPC(int id, WorldTile tile, String name, Direction direction, boolean randomWalk) {
		NPC npc = new NPC(id, tile);
		if (name != null)
			npc.setPermName(name);
		if (direction != null)
			npc.setFaceAngle(direction.getAngle());
		npc.setRandomWalk(randomWalk);
		return npc;
	}
	
	public static NPC spawnNPC(int id, WorldTile tile, Direction direction, boolean randomWalk) {
		return spawnNPC(id, tile, null, direction, randomWalk);
	}
	
	public static NPC spawnNPC(int id, WorldTile tile, String name) {
		return spawnNPC(id, tile, name, null, false);
	}
	
	public static WorldObject spawnObject(int id, WorldTile tile) {
		GameObject object = new GameObject(id, ObjectType.SCENERY_INTERACT, 0, tile);
		World.spawnObject(object);
		return object;
	}
}
