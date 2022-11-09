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
package com.rs.game.content.death;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.miniquests.Miniquest;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.model.object.GameObject;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;

public class DeathOfficeController extends Controller {

	public enum Hub {
		LUMBRIDGE(new WorldTile(3222, 3219, 0)),
		VARROCK(new WorldTile(3212, 3422, 0)),
		EDGEVILLE(new WorldTile(3094, 3502, 0)),
		FALADOR(new WorldTile(2965, 3386, 0)),
		SEERS_VILLAGE(new WorldTile(2725, 3491, 0)),
		ARDOUGNE(new WorldTile(2662, 3305, 0)),
		YANILLE(new WorldTile(2605, 3093, 0)),
		KELDAGRIM(new WorldTile(2845, 10210, 0)) {
			@Override
			public boolean meetsRequirements(Player player) {
				return player.isQuestComplete(Quest.GIANT_DWARF);
			}
		},
		DORGESH_KAAN(new WorldTile(2720, 5351, 0)) {
			@Override
			public boolean meetsRequirements(Player player) {
				return player.isQuestComplete(Quest.DEATH_TO_DORGESHUUN);
			}
		},
		LLETYA(new WorldTile(2341, 3171, 0)) {
			@Override
			public boolean meetsRequirements(Player player) {
				return player.isQuestComplete(Quest.ROVING_ELVES);
			}
		},
		ETCETERIA(new WorldTile(2614, 3894, 0)) {
			@Override
			public boolean meetsRequirements(Player player) {
				return player.isQuestComplete(Quest.THRONE_OF_MISCELLANIA);
			}
		},
		DAEMONHEIM(new WorldTile(3450, 3718, 0)),
		CANIFIS(new WorldTile(3496, 3489, 0)) {
			@Override
			public boolean meetsRequirements(Player player) {
				return player.isQuestComplete(Quest.PRIEST_IN_PERIL);
			}
		},
		TZHAAR_CITY(new WorldTile(4651, 5151, 0)),
		BURTHORPE(new WorldTile(2889, 3528, 0)),
		AL_KHARID(new WorldTile(3275, 3166, 0)),
		DRAYNOR_VILLAGE(new WorldTile(3079, 3250, 0)),

		//Extra unlocked hubs
		LUMBRIDGE_CASTLE(new WorldTile(3222, 3219, 0)),
		FALADOR_CASTLE(new WorldTile(2971, 3343, 0)),
		CAMELOT(new WorldTile(2758, 3486, 0)),
		SOUL_WARS(new WorldTile(1891, 3177, 0));

		private WorldTile tile;

		Hub(WorldTile tile) {
			this.tile = tile;
		}

		public boolean meetsRequirements(Player player) {
			return true;
		}
	}

	public static List<Hub> getUnlockedHubs(Player player) {
		List<Hub> hubs = new ArrayList<>();
		hubs.add(Hub.LUMBRIDGE_CASTLE);
		if (player.isQuestComplete(Quest.RECRUITMENT_DRIVE)) {
			player.getVars().setVarBit(668, 1);
			hubs.add(Hub.FALADOR_CASTLE);
		}
		if (player.isMiniquestComplete(Miniquest.KNIGHTS_WAVE_TRAINING_GROUNDS)) {
			player.getVars().setVarBit(3910, 1);
			hubs.add(Hub.CAMELOT);
		}
		if (player.isQuestComplete(Quest.NOMADS_REQUIEM)) {
			player.getVars().setVarBit(6982, 1);
			hubs.add(Hub.SOUL_WARS);
		}
		return hubs;
	}

	public static Hub getCurrentHub(Player player, WorldTile tile) {
		Hub nearestHub = null;
		int distance = Integer.MAX_VALUE;
		for (Hub hub : Hub.values()) {
			if (hub.ordinal() >= Hub.LUMBRIDGE_CASTLE.ordinal())
				break;
			if (!hub.meetsRequirements(player))
				continue;
			int d = (int) Utils.getDistance(hub.tile, tile);
			if (d < distance) {
				distance = d;
				nearestHub = hub;
			}
		}
		return nearestHub;
	}

	public static Hub getRespawnHub(Player player) {
		return getCurrentHub(player, new WorldTile(player.getTile()));
	}
	public static final WorldTile[] RESPAWN_LOCATIONS = { new WorldTile(3222, 3219, 0), new WorldTile(2971, 3343, 0), new WorldTile(2758, 3486, 0), new WorldTile(1891, 3177, 0), new WorldTile(2889, 3528, 0) };
	private transient DynamicRegionReference region = new DynamicRegionReference(2, 2);
	private Stages stage;
	private Integer[][] slots;
	private Hub defaultHub;
	private Hub currentHub;
	private List<Hub> optionalHubs;
	private WorldTile deathTile;
	private boolean hadSkull;

	public DeathOfficeController(WorldTile deathTile, boolean hadSkull) {
		this.deathTile = new WorldTile(deathTile);
		this.hadSkull = hadSkull;
	}

	@Override
	public void start() {
		loadRoom();
	}

	@Override
	public boolean login() {
		loadRoom();
		return false;
	}

	@Override
	public boolean logout() {
		player.getTile().setLocation(new WorldTile(1978, 5302, 0));
		destroyRoom();
		return false;
	}

	@Override
	public boolean canTakeItem(GroundItem item) {
		return false;
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean canPlayerOption1(Player target) {
		return false;
	}

	@Override
	public boolean canPlayerOption2(Player target) {
		return false;
	}

	@Override
	public boolean canPlayerOption3(Player target) {
		return false;
	}

	@Override
	public boolean canPlayerOption4(Player target) {
		return false;
	}

	private static enum Stages {
		LOADING, RUNNING, DESTROYING
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().removeSubs(Sub.TAB_COMBAT, Sub.TAB_ACHIEVEMENT, Sub.TAB_SKILLS, Sub.TAB_INVENTORY, Sub.TAB_EQUIPMENT, Sub.TAB_PRAYER, Sub.TAB_MAGIC, Sub.TAB_EMOTES);
	}

	public void loadRoom() {
		stage = Stages.LOADING;
		player.lock();

		if (region == null)
			region = new DynamicRegionReference(2, 2);

		region.copyMapSinglePlane(246, 662, () -> {
			player.reset();
			player.setNextWorldTile(region.getLocalTile(10, 6));
			WorldTasks.delay(1, () -> {
				player.setNextAnimation(new Animation(-1));
				player.getMusicsManager().playSongAndUnlock(683);
				player.getPackets().setBlockMinimapState(2);
				sendInterfaces();
				player.unlock();
				stage = Stages.RUNNING;
			});
		});
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 45803) {
			if(player.getI("death coffer") > 10_000) {
				player.startConversation(new Dialogue().addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Use 10K to overwrite death?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I want to overwrite my death!")
								.addNPC(15661, HeadE.CALM, "Okay but it will cost you!")
								.addNext(()->{
									player.save("death is saved", true);
									player.sendMessage("You will keep your items!");
									getReadyToRespawn();
								})
						);
						option("Just respawn me...", new Dialogue()
								.addNPC(15661, HeadE.CALM, "Are you sure you want to leave a gravestone?")
								.addPlayer(HeadE.HAPPY_TALKING, "Yes I am sure")
								.addNext(()->{getReadyToRespawn();})
						);
					}
				}));
				return false;
			}
			player.sendMessage("Your death coffer was empty!");
			getReadyToRespawn();
			return false;
		}
		return true;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (interfaceId == 18) {
			if (componentId == 9) {
				if (packet == ClientPacket.IF_OP1)
					unprotect(slotId);
			} else if (componentId == 17) {
				if (packet == ClientPacket.IF_OP1)
					protect(slotId2);
			} else if (componentId == 45) {
				int index = slotId-1;
				if (index < 0) {
					currentHub = defaultHub;
					player.getVars().setVarBit(9228, 0);
					return true;
				}
				if (index >= optionalHubs.size())
					return false;
				Hub hub = optionalHubs.get(slotId-1);
				if (hub == null || !hub.meetsRequirements(player))
					return false;
				currentHub = hub;
				player.getVars().setVarBit(9228, slotId);
			}
			return false;
		}
		return true;
	}

	public void getReadyToRespawn() {
		slots = GraveStone.getItemSlotsKeptOnDeath(player, false, hadSkull(), player.getPrayer().isProtectingItem());
		currentHub = defaultHub = getCurrentHub(player, getDeathTile());
		optionalHubs = getUnlockedHubs(player);
		player.getInterfaceManager().sendInterface(18);
		if (slots[0].length > 0) {
			player.getVars().setVarBit(9227, slots[0].length);
			player.save("protectSlots", slots[0].length);
			sendProtectedItems();
		} else {
			player.getVars().setVarBit(9222, -1);
			player.getVars().setVarBit(9227, 1);
			player.save("protectSlots", 1);
		}
		player.getVars().setVar(105, -1); //unlocks choose respawn location
		player.getVars().setVarBit(9231, currentHub.ordinal());
		player.getPackets().setIFRightClickOps(18, 9, 0, slots[0].length, 0);
		player.getPackets().setIFRightClickOps(18, 17, 0, 100, 0);
		player.getPackets().setIFRightClickOps(18, 45, 0, 6, 0);
		player.setCloseInterfacesEvent(() -> {
			WorldTile respawnTile = Hub.LUMBRIDGE.tile;
			synchronized (slots) {
				if (!player.hasRights(Rights.ADMIN))
					player.sendItemsOnDeath(null, getDeathTile(), currentHub.tile, false, slots);
				if (!player.hasRights(Rights.ADMIN) || player.getBool("death is saved")) {
					player.delete("death is saved");
					player.sendItemsOnDeath(null, getDeathTile(), respawnTile, false, slots);
				}
				else
					player.sendMessage("Slots saved: " + Arrays.deepToString(GraveStone.getItemsKeptOnDeath(player, slots)));
			}
			player.setCloseInterfacesEvent(null);
			Magic.sendObjectTeleportSpell(player, true, currentHub.tile);
		});
	}

	public void sendProtectedItems() {
		for (int i = 0; i < getProtectSlots(); i++)
			player.getVars().setVarBit(9222 + i, i >= slots[0].length ? -1 : slots[0][i]);
	}

	public void protect(int itemId) {
		synchronized (slots) {
			int slot = -1;
			for (int i = 0; i < slots[1].length; i++) {

				Item item = slots[1][i] >= 16 ? player.getInventory().getItem(slots[1][i] - 16) : player.getEquipment().getItem(slots[1][i] - 1);
				if (item == null)
					continue;
				if (item.getId() == itemId) {
					slot = i;
					break;
				}
			}
			if (slot == -1 || getProtectSlots() <= slots[0].length)
				return;
			slots[0] = Arrays.copyOf(slots[0], slots[0].length + 1);
			slots[0][slots[0].length - 1] = slots[1][slot];
			Integer[] lItems = new Integer[slots[1].length - 1];
			System.arraycopy(slots[1], 0, lItems, 0, slot);
			System.arraycopy(slots[1], slot + 1, lItems, slot, lItems.length - slot);
			slots[1] = lItems;
			sendProtectedItems();
		}

	}

	public void unprotect(int slot) {
		synchronized (slots) {
			if (slot >= slots[0].length)
				return;
			slots[1] = Arrays.copyOf(slots[1], slots[1].length + 1);
			slots[1][slots[1].length - 1] = slots[0][slot];
			Integer[] pItems = new Integer[slots[0].length - 1];
			System.arraycopy(slots[0], 0, pItems, 0, slot);
			System.arraycopy(slots[0], slot + 1, pItems, slot, pItems.length - slot);
			slots[0] = pItems;
			sendProtectedItems();
		}

	}

	public int getProtectSlots() {
		return player.get("protectSlots") != null ? (Integer) player.get("protectSlots") : 1;
	}

	public WorldTile getDeathTile() {
		return deathTile;
	}

	public boolean hadSkull() {
		return hadSkull;
	}

	@Override
	public void magicTeleported(int type) {
		destroyRoom();
		player.getPackets().setBlockMinimapState(0);
		player.getInterfaceManager().sendSubDefaults(Sub.TAB_COMBAT, Sub.TAB_ACHIEVEMENT, Sub.TAB_SKILLS, Sub.TAB_INVENTORY, Sub.TAB_EQUIPMENT, Sub.TAB_PRAYER, Sub.TAB_MAGIC, Sub.TAB_EMOTES);
		removeController();
	}

	public void destroyRoom() {
		if (stage != Stages.RUNNING)
			return;
		stage = Stages.DESTROYING;
		region.destroy();
	}

	@Override
	public void forceClose() {
		destroyRoom();
	}

}