package com.rs.rsps.jessecustom.customscape;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.handlers.NPCDropHandler;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class ScalingMultipliers {
	public static List<Object> getItemsByNameStackable(String name) {
		List<Object> items = new ArrayList<>();
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			if (!ItemDefinitions.getDefs(i).getName().toLowerCase().contains(name.toLowerCase())
					|| ItemDefinitions.getDefs(i).isLended()) {
				continue;
			}
			if(!ItemDefinitions.getDefs(i).isStackable())
				continue;
			items.add(i);
		}
		return items;
	}


	public static Object[] scalingAmountMultipliers() {
		List<Object> items = new ArrayList<>();
		items.addAll(getItemsByNameStackable("coins"));
		items.addAll(getItemsByNameStackable("rune"));
		items.addAll(getItemsByNameStackable("arrow"));
		items.addAll(getItemsByNameStackable("dart"));
		items.addAll(getItemsByNameStackable("feather"));
		items.addAll(getItemsByNameStackable("wine of zamorak"));
		items.addAll(getItemsByNameStackable("javelin"));
		items.addAll(getItemsByNameStackable("knife"));
		items.addAll(getItemsByNameStackable("bone"));
		items.addAll(getItemsByNameStackable("Cannon balls"));
		return items.stream().distinct().toArray();
	}



	public static NPCDropHandler increaseAmountByScale = new NPCDropHandler(null, ScalingMultipliers.scalingAmountMultipliers()) {
		@Override
		public void handle(NPCDropEvent e) {
			if(e.getNPC().getTempAttribs() == null)
				return;
			if(e.getNPC() instanceof DungeonNPC)
				return;
			if(CustomScape.isPlayerCustomScape(e.getPlayer())) {
				int playerScale = e.getPlayer().getI("CustomScapeScale", 0);
				double playerCombatScale = 1 + (playerScale / 10.0);
				double npcScale = e.getNPC().getTempAttribs().getI("CustomScapeScale");
				double npcCombatScale = 1 + (npcScale / 10.0);
				double scale = 1;
				if (npcCombatScale >= playerCombatScale) {
					scale = playerCombatScale;
					if (Settings.getConfig().isDebug())
						e.getPlayer().sendMessage("This one");
				}
				if (npcCombatScale < playerCombatScale) {
					scale = npcCombatScale;
					if (Settings.getConfig().isDebug())
						e.getPlayer().sendMessage("This two " + e.getNPC().getTempAttribs().getD("CustomScapeScale"));
				}
				if (Settings.getConfig().isDebug())
					e.getPlayer().sendMessage("scale " + scale);
				e.getItem().setAmount((int) (e.getItem().getAmount() * scale));
			}

			if (e.getItem().getId() == 995 && e.getPlayer().getInventory().containsItem(25351, 1) && e.getPlayer().getInventory().hasRoomFor(e.getItem())) {
				e.getPlayer().getInventory().addItem(new Item(e.getItem()));
				e.deleteItem();
				return;
			}
		}
	};
}
