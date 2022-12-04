package com.rs.rsps;

import com.rs.cache.loaders.ObjectType;
import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.rsps.jessecustom.CustomScape;
import com.rs.rsps.jessecustom.bosses.kalphitequeen.KalphiteQueenScalingInstanceController;
import com.rs.rsps.teleports.BossTeleport;
import com.rs.rsps.teleports.SlayerTeleport;
import com.rs.rsps.teleports.Teleport;
import com.rs.utils.shop.ShopsHandler;
import com.rs.game.content.combat.CombatDefinitions.Spellbook;

@PluginEventHandler
public class Home {
	
	@ServerStartupEvent
	public static void spawnNPCs() {
		/* Task master */
		//spawnNPC(14858, new WorldTile(3090, 3494, 0), "Cadet Cassandra", Direction.WEST, false);

		/* Scale master */
		spawnNPC(2253, new WorldTile(3167, 3491, 0), "Wise Old Man", Direction.EAST, false);

		/* Slayer Masters */
//		spawnNPC(8480, new WorldTile(3091, 3487, 0), Direction.SOUTH, false);
//		spawnNPC(8481, new WorldTile(3092, 3487, 0), Direction.SOUTH, false);
//		spawnNPC(1597, new WorldTile(3093, 3487, 0), Direction.SOUTH, false);
//		spawnNPC(1598, new WorldTile(3094, 3487, 0), Direction.SOUTH, false);
//		spawnNPC(7779, new WorldTile(3095, 3487, 0), Direction.SOUTH, false);
//		spawnNPC(8466, new WorldTile(3096, 3487, 0), Direction.SOUTH, false);
		spawnNPC(9085, new WorldTile(3165, 3489, 0), "Master Slayer", Direction.SOUTH, false);
		spawnNPC(15661, new WorldTile(3166, 3486, 0), Direction.WEST, false);
		spawnNPC(15147, new WorldTile(3164, 3489, 0), "Slayer Assistant", Direction.SOUTH, false);//Slayer teleport

		/* GIM */
		spawnNPC(1512, new WorldTile(3165, 3482, 0), "GIM Tutor", Direction.SOUTH, false);
		World.unclipTile(new WorldTile(3165, 3482, 0));
	}

	public static LoginHandler loginBlockThings = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if(e.getPlayer().getBool("Group IronMan"))
				if(e.getPlayer().getO("GIM Team") == null) {
					e.getPlayer().sendMessage("<col=FF0000><shad=000000>You are XP locked until you are in a team...");
					e.getPlayer().setXpLocked(true);
				}
				if(e.getPlayer().getO("GIM Team") != null)
					e.getPlayer().setXpLocked(false);
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

	public static ObjectClickHandler healingWell = new ObjectClickHandler(new Object[] { 28715 }, new WorldTile(3172, 3466, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("You feel refreshed.");
			e.getPlayer().getPrayer().restorePrayer(e.getPlayer().getSkills().getLevelForXp(Constants.PRAYER) * 10);
			e.getPlayer().getPoison().reset();
			e.getPlayer().setHitpoints(e.getPlayer().getMaxHitpoints());
			e.getPlayer().refreshHitPoints();
			e.getPlayer().getSkills().set(Constants.SUMMONING, e.getPlayer().getSkills().getLevelForXp(Constants.SUMMONING));
			e.getPlayer().sendOptionDialogue("Would you like to switch up your prayers or magic?", o1 -> {
				o1.add("Select new magic book", new Dialogue().addOptions(o2 -> {
					o2.add("Modern", () -> e.getPlayer().getCombatDefinitions().setSpellbook(Spellbook.MODERN));
					o2.add("Ancient", () -> e.getPlayer().getCombatDefinitions().setSpellbook(Spellbook.ANCIENT));
					o2.add("Lunar", () -> e.getPlayer().getCombatDefinitions().setSpellbook(Spellbook.LUNAR));
				}));
				o1.add("Select new prayer book", new Dialogue().addOptions(o2 -> {
					o2.add("Modern", () -> e.getPlayer().getPrayer().setPrayerBook(false));
					o2.add("Ancient curses", () -> e.getPlayer().getPrayer().setPrayerBook(true));
				}));
			});
		}
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

	public static NPCClickHandler slayerTeleHandler = new NPCClickHandler(new Object[] { 15147 }) {
		@Override
		public void handle(NPCClickEvent e) {
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

	public static NPCClickHandler scaleNPCHandler = new NPCClickHandler(new Object[] { 2253 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if(CustomScape.isPlayerCustomScape(e.getPlayer())) {
				int NPC = 2253;
				e.getPlayer().startConversation(new Dialogue()
						.addOptions("Would you like to scale the world?", option -> {
							option.add("Yes", new Dialogue().addNext(() -> {
								e.getPlayer().sendInputName("What scale would you like?", scaleString -> {
									try {
										int scale = Integer.parseInt(scaleString);
										if(scale < 0)
											throw new NumberFormatException();
										if(scale == 0) {
											e.getPlayer().delete("CustomScapeScale");
											return;
										}
										e.getPlayer().save("CustomScapeScale", scale);
										e.getPlayer().sendMessage("The world is now scaled by " + (scale*10 + 100) + "%");
									} catch(NumberFormatException n) {
										e.getPlayer().sendMessage("Improper scale formatting, try again.");
									}

								});
							}));
							option.add("No", new Dialogue().addNext(()->{e.getPlayer().delete("CustomScapeScale"); e.getPlayer().sendMessage("No longer scaled...");}));
						})

				);
				return;
			}
			e.getPlayer().sendMessage("You must be a custom scaper to use scaling...");
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
