package snerble.minecraft.plugins.utils.actions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import snerble.minecraft.plugins.utils.Database;
import snerble.minecraft.plugins.utils.Tag;
import snerble.minecraft.plugins.utils.templates.ListenerBase;

public class AutoTorchPlacer extends ListenerBase {

	/**
	 * Defines faces that will be tested when placing torches.
	 */
	private static final BlockFace[] placeableFaces = new BlockFace[] {
			BlockFace.DOWN
		};
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if (!Database.Instance.getValue(player, Tag.AUTOTORCH_ENABLED, false))
			return;
		
		Location from = event.getFrom();
		Location to = event.getTo().clone();
		
		// Ignore redundant calls
		if (from.getBlockX() == to.getBlockX() &&
			from.getBlockY() == to.getBlockY() &&
			from.getBlockZ() == to.getBlockZ())
			return;
		
		// Round the feet location up to account for partial blocks
		to.setY(Math.round(to.getY()));

		// Get a stack of torches in the player's inventory
		int torchStackIndex = player.getInventory().first(Material.TORCH);
		if (torchStackIndex == -1) {
			chat.sendMessage(player, String.format("Disabled %s; no torches in inventory.",
					ManageAutoTorchCommand.NAME));
			
			// Set autoTorch to false
			Database.Instance.setValue(player, Tag.AUTOTORCH_ENABLED, false);
			return;
		}
		
		Block feetBlock = to.getBlock();
		
		// Check block light level
		if (feetBlock.getLightFromBlocks() < 7) {
			
			// Place torch at player's feet
			if (placeTorch(to)) {
				ItemStack torches = player.getInventory().getItem(torchStackIndex);
				torches.setAmount(torches.getAmount() - 1);
			}
				
		}
	}
	
	/**
	 * Places a torch on the specified side
	 * @param loc - The location of the torch.
	 */
	private boolean placeTorch(Location loc) {
		// Try to place a torch on every face in the list
		for (BlockFace face : placeableFaces) {
			Block block = loc.getBlock();
			
			// Ensure block touches the specified face
		    if (block.getType() == Material.AIR
		    		&& block.getRelative(face).getType().isSolid()
		    		&& block.getRelative(face).getType().isOccluding()) {

	            loc.getBlock().setType(Material.TORCH);
	            return true;
		    }
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private String formatLocation(Location l) {
		return String.format("{x:%d,y:%d,z:%d}", l.getBlockX(), (int) Math.ceil(l.getY()), l.getBlockZ());
	}
}
