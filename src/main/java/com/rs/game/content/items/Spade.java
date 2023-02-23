package com.rs.game.content.items;

import com.rs.game.content.minigames.barrows.BarrowsController;
import com.rs.game.content.quests.piratestreasure.PiratesTreasure;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Spade {
	
	public static ItemClickHandler digSpade = new ItemClickHandler(new Object[] { 952 }, new String[] { "Dig" }, e -> dig(e.getPlayer()));

	public static void dig(final Player player) {
		player.resetWalkSteps();
		player.setNextAnimation(new Animation(830));
		player.lock();
		WorldTasks.schedule(() -> {
			player.unlock();
			if (player.getTreasureTrailsManager().useDig(false) || BarrowsController.digIntoGrave(player))
				return;
			if (player.getX() == 3005 && player.getY() == 3376 || player.getX() == 2999 && player.getY() == 3375 || player.getX() == 2996 && player.getY() == 3377 || player.getX() == 2989 && player.getY() == 3378 || player.getX() == 2987
					&& player.getY() == 3387 || player.getX() == 2984 && player.getY() == 3387) {
				// mole
				player.setNextWorldTile(WorldTile.of(1752, 5137, 0));
				player.sendMessage("You seem to have dropped down into a network of mole tunnels.");
				return;
			}
			if (Utils.getDistance(player.getTile(), WorldTile.of(2749, 3734, 0)) < 3) {
				player.useStairs(-1, WorldTile.of(2690, 10124, 0), 0, 1);
				return;
			}
			//Pirate's Treasure
			if(player.getQuestManager().getStage(Quest.PIRATES_TREASURE) == PiratesTreasure.GET_TREASURE)
				PiratesTreasure.findTreasure(player);
			player.sendMessage("You find nothing.");
		});
	}
	
}
