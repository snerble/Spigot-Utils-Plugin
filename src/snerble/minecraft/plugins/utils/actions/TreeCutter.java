/**
 * 
 */
package snerble.minecraft.plugins.utils.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import snerble.minecraft.plugins.utils.Database;
import snerble.minecraft.plugins.utils.Tag;
import snerble.minecraft.plugins.utils.templates.ListenerBase;

/**
 * Processes events related to the TreeCutter module.
 * @author Conor
 *
 */
public class TreeCutter extends ListenerBase {
	private static final String AXE_SUFFIX = "_AXE";
	private static final String LOG_SUFFIX = "_LOG";
	private static final String WOOD_SUFFIX = "_WOOD";
	private static final String LEAVES_SUFFIX = "_LEAVES";
	private static final String SAPLING_SUFFIX = "_SAPLING";
	
	private final static HashSet<Material> AXES = new HashSet<>();
	private final static HashSet<Material> LOGS = new HashSet<>();
	private final static HashSet<Material> LEAVES = new HashSet<>();
	private final static HashSet<Material> SAPLINGS = new HashSet<>();
	private final static Material[] SOIL = new Material[] {
		Material.DIRT,
		Material.GRASS_BLOCK,
		Material.PODZOL
	};
	
	private final Random random = new Random();
	
	public TreeCutter() {
		// Initialize the material lists
		for (Material material : Material.values()) {
			if (material.isItem() && material.name().contains(AXE_SUFFIX)) {				
				AXES.add(material);
			}
			else if (material.isBlock()) {
				if (material.name().contains(LOG_SUFFIX)
						|| material.name().contains(WOOD_SUFFIX))
					LOGS.add(material);
				else if (material.name().contains(LEAVES_SUFFIX)) {
					LEAVES.add(material);
				}
				else if (material.name().contains(SAPLING_SUFFIX)
						&& !material.name().contains("POTTED_")) {
					SAPLINGS.add(material);
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		// Ignore if no player broke the block
		// 	OR if the broken block is not a log
		// 	OR if they disabled treecutter
		if (player == null
				|| !isLog(block)
				|| !Database.Instance.getValue(player, Tag.TREECUTTER_ENABLED, false))
			return;
		
		
		// Create supplier for obtaining the blocks
		Supplier<HashSet<Block>> getBlocks = () -> {
			// Obtain some player treecutter variables
			int limit = Database.Instance.getValue(player, Tag.TREECUTTER_BLOCKLIMIT, ManageTreeCutterCommand.BLOCK_BREAK_LIMIT);
			boolean breakLeaves = Database.Instance.getValue(player, Tag.TREECUTTER_BREAKLEAVES, true);
			
			// Prepare the list of blocks that should be returned
			HashSet<Material> typesToFind = new HashSet<>(LOGS);
			if (breakLeaves)
				typesToFind.addAll(LEAVES);
			
			// Search for the blocks
			HashSet<Block> set = searchBlocks(
					block,
					x -> typesToFind.contains(x.getType()), 
					limit);

			return set;
		};
		
		// Get if the trees need to be replanted
		boolean replant = Database.Instance.getValue(player, Tag.TREECUTTER_REPLANT, true);
		
		// Break the blocks immediately if the player is in creative mode
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			event.setCancelled(true);
			getBlocks.get().forEach(b -> {
				Material sapling = getSapling(b.getType()).get();
				// Replant the saplings
				if (replant && isLog(b) && isAboveSoil(b)) {
					b.setType(sapling);
				}
				// Destroy block
				else {
					b.setType(Material.AIR);
				}
			});
			return;
		}
		
		// Find the player's axe
		ItemStack axe = getAxe(player).orElse(null);
		// Cancel if the player is not holding an axe
		if (axe == null)
			return;
		
		// Stop the original event from proceeding.
		event.setCancelled(true);
		
		// Get if the axe should be broken
		boolean breakAxe = Database.Instance.getValue(player, Tag.TREECUTTER_BREAKAXE, false);
		
		// Store drops so they can be teleported to the first block
		Inventory drops = Bukkit.createInventory(null, 18);
		
		for (Block b : getBlocks.get()) {
			// Stop if the axe is no longer in the player's inventory (axe probably broke)
			if (!player.getInventory().contains(axe))
				break;
			
			Damageable d = (Damageable) axe.getItemMeta();
			int durability = axe.getType().getMaxDurability() - d.getDamage();
		
			// Cancel before breaking the axe
			if (!breakAxe && durability <= 1) {
				chat.sendMessage(player, "Cancelled; %s is at 1 durability.",
						getToolName(axe));
				break;
			}
			// Destroy the axe if the durability fell below 1
			else if (durability < 1) {
				chat.sendMessage(player, "Goodbye %s!", getToolName(axe));
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1); // break sound effect
				player.getInventory().remove(axe);
				player.updateInventory();
			}
			
			// Store the drops
			b.getDrops(axe, player).forEach(drop -> drops.addItem(drop));

			// Damage the tool
			if (isLog(b) && shouldApplyDamage(axe)) {
				d.setDamage(d.getDamage() + 1);
				axe.setItemMeta((ItemMeta) d);
			}
			
			// Replant the saplings
			if (replant && isLog(b) && isAboveSoil(b)) {
				Material sapling = getSapling(b.getType()).get();
				
				// Remove one sapling either from the drops or the player
				if (takeOne(drops, sapling)
						|| takeOne(player.getInventory(), sapling)) {
					b.setType(sapling);
				}
				else {
					b.setType(Material.AIR);
				}
			}
			// Destroy block
			else {
				b.setType(Material.AIR);
			}		
		}

		drops.forEach(x -> {
			if (x != null)
				block.getWorld().dropItemNaturally(block.getLocation(), x);
		});
		drops.clear();
		
		// Update if saplings may have been taken
		if (replant)
			player.updateInventory();
	}
	
	/**
	 * Returns the first item slot containing an axe in the player's inventory.
	 */
	private Optional<ItemStack> getAxe(Player player) {
		ItemStack heldItem = player.getInventory().getItemInMainHand();
		
		if (AXES.contains(heldItem.getType()))
			return Optional.of(heldItem);
		return Optional.empty();
	}
	
	/**
	 * Returns whether the block is a log.
	 */
	private static boolean isLog(Block block) {
		return LOGS.contains(block.getType());
	}
	
	private static boolean isAboveSoil(Block block) {
		return Arrays.asList(SOIL).contains(block.getRelative(BlockFace.DOWN).getType());
	}
	
	private static HashSet<Block> searchBlocks(
			Block startBlock,
			Predicate<Block> predicate,
			int limit) {
		HashSet<Block> blocks = new LinkedHashSet<>(); // Blocks whose neighbours have been checked
		HashSet<Block> newBlocks = new LinkedHashSet<>(); // Blocks whose neighbours have not been checked

		newBlocks.add(startBlock);
		
		HashSet<Block> searchBuffer = new LinkedHashSet<>();
		while (!newBlocks.isEmpty()) {
			for (Block block : newBlocks) {
				blocks.add(block);
			
				if (blocks.size() >= limit)
					return blocks;
				
				searchBuffer.addAll(getAdjacentBlocks(block).stream()
						.filter(predicate)
						.filter(x -> !blocks.contains(x))
						.collect(Collectors.toList()));
			}
			
			newBlocks.clear();
			newBlocks.addAll(searchBuffer);
			searchBuffer.clear();
		}
		
		return blocks;
	}
	
	private static HashSet<Block> getAdjacentBlocks(Block block) {
		HashSet<Block> l = new HashSet<>();
		for (int modX = -1; modX <= 1; modX++)
			for (int modY = -1; modY <= 1; modY++)
				for (int modZ = -1; modZ <= 1; modZ++)
					l.add(block.getRelative(modX, modY, modZ));
		l.remove(block);
		return l;
	}
	
	private boolean shouldApplyDamage(ItemStack item) {
		int enchantment = item.getEnchantmentLevel(Enchantment.DURABILITY);
		return random.nextFloat() * (enchantment + 1) < 1f;
	}
	
	private static String getToolName(ItemStack item) {
		String colors = "";
		if (!item.getEnchantments().isEmpty())
			colors += ChatColor.AQUA;
		if (item.getItemMeta().hasDisplayName())
			colors += ChatColor.ITALIC;
		
		return String.format("%s%s%s",
				colors,
				item.getItemMeta().hasDisplayName()
						? item.getItemMeta().getDisplayName()
						: WordUtils.capitalizeFully(item.getType().name().replaceAll("_"," ")),
				ChatColor.RESET);
	}
	
	private static Optional<Material> getSapling(Material m) {
		String species = m.name().split("_")[0];
		return SAPLINGS.stream()
				.filter(x -> x.name().contains(species))
				.findFirst();
	}
	
	private static boolean takeOne(Inventory i, Material m) {
		int index = i.first(m);
		if (index >= 0) {
			ItemStack s = i.getItem(index);
			s.setAmount(s.getAmount() - 1);
			if (s.getAmount() == 0)
				i.clear(index);
			return true;
		}
		return false;
	}
}
