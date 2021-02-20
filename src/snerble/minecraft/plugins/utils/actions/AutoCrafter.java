package snerble.minecraft.plugins.utils.actions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import snerble.minecraft.plugins.tools.InventoryUtils;
import snerble.minecraft.plugins.utils.Database;
import snerble.minecraft.plugins.utils.Tag;
import snerble.minecraft.plugins.utils.UtilsPlugin;
import snerble.minecraft.plugins.utils.templates.ListenerBase;

/**
 * @author Conor
 *
 */
public final class AutoCrafter extends ListenerBase {
	public static final String NAME = "AutoCraft";
	
	private final Inventory temp;
	
	public AutoCrafter() {
		temp = Bukkit.createInventory(null, 9);
		temp.setStorageContents(temp.getContents());
	}
	
	@EventHandler
	public void onPickupItem(EntityPickupItemEvent e) {
		// Ignore non-players
		if (!(e.getEntity() instanceof Player))
			return;
		
		Player player = (Player) e.getEntity();
		
		// Ignore if player disabled autocraft
		if (!Database.Instance.getValue(player, Tag.AUTOCRAFT_ENABLED, false))
			return;
		
		// Run autocraft after this event
		scheduleAutoCraft(player);
	}
	
	public void scheduleAutoCraft(Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(UtilsPlugin.Instance, new Runnable() {
			@Override
			public void run() {
				autoCraft(player);				
			}
		}, 1);
	}
	
	private void autoCraft(Player player) {
		// TODO only one recipe per tick
		
		List<ItemStackEntry> autocraftRecipes = AutoCraftCommand.getRecipesFor(player);
		
		boolean craftedSomething = false;
		for (ItemStackEntry autoCraftRecipe : autocraftRecipes) {
			// Get how many items are in the player's inventory
			int alreadyInInventory = 0;
			if (!autoCraftRecipe.isUnlimited()) {
				alreadyInInventory = InventoryUtils.getAmount(
					player.getInventory(),
					new RecipeChoice.MaterialChoice(autoCraftRecipe.getMaterial()));
			}
			
			List<Recipe> recipes = Bukkit.getRecipesFor(new ItemStack(autoCraftRecipe.getMaterial())).stream()
					.filter(x -> x instanceof ShapedRecipe || x instanceof ShapelessRecipe)
					.collect(Collectors.toList());
			
			for (Recipe r : recipes) {
				// Skip recipe if it will exceed the autocraft recipe's limit
				if (!autoCraftRecipe.isUnlimited()
						&& alreadyInInventory + r.getResult().getAmount() > autoCraftRecipe.getAmount())
					continue;
				
				if (r instanceof ShapedRecipe) {
					ShapedRecipe recipe = (ShapedRecipe) r;
					
					if (!tryCollectRequirements(recipe, player.getInventory()))
						continue;
					
					craftedSomething = true;
					player.getInventory().addItem(recipe.getResult());
				}
				else if (r instanceof ShapelessRecipe) {
					ShapelessRecipe recipe = (ShapelessRecipe) r;
					
					if (!tryCollectRequirements(recipe, player.getInventory()))
						continue;
					
					craftedSomething = true;
					player.getInventory().addItem(recipe.getResult());
				}
			}
		}
		
		if (craftedSomething) {
			player.updateInventory();
			
			// Schedule autocraft again to repeat untill nothing was crafted.
			scheduleAutoCraft(player);
		}
	}
	
	private boolean tryCollectRequirements(ShapedRecipe r, Inventory inventory) {
		try {
			// Put all the requirements into temp
			r.getChoiceMap().forEach((c, choice) -> {
				ItemStack ingredients = r.getIngredientMap().get(c);
				
				// Skip empty crafting slots
				if (choice == null || ingredients == null)
					return;
				
				// Take the ingredients (exception will return the items)
				temp.addItem(InventoryUtils.tryTake(inventory, choice, ingredients.getAmount()).get());
			});
			
			// Return the crafting remainders of any consumed items (if any)
			Arrays.stream(temp.getStorageContents())
					.filter(x -> x != null)
					.forEach(x -> {
						Material remainingItem = x.getType().getCraftingRemainingItem();
						if (remainingItem != null)
							inventory.addItem(new ItemStack(remainingItem, x.getAmount()));
					});
			
			return true;
		}
		catch (Exception e) {
			// Return the items that were taken
			InventoryUtils.tryCloneInto(temp, inventory);
			
			return false;
		}
		finally {
			// Clear the crafting storage
			temp.clear();
		}
	}
	
	private boolean tryCollectRequirements(ShapelessRecipe r, Inventory inventory) {
		try {
			// Put all the requirements into temp
			IntStream.range(0, r.getChoiceList().size())
					.forEach(i -> {
						RecipeChoice choice = r.getChoiceList().get(i);
						ItemStack ingredients = r.getIngredientList().get(i);
						
						// Skip empty crafting slots
						if (choice == null || ingredients == null)
							return;
						
						// Take the ingredients (exception will return the items)
						temp.addItem(InventoryUtils.tryTake(inventory, choice, ingredients.getAmount()).get());
					});
			
			// Return the crafting remainders of any consumed items (if any)
			Arrays.stream(temp.getStorageContents())
			.filter(x -> x != null)
			.forEach(x -> {
				Material remainingItem = x.getType().getCraftingRemainingItem();
				if (remainingItem != null)
					inventory.addItem(new ItemStack(remainingItem, x.getAmount()));
			});
			
			return true;
		}
		catch (Exception e) {
			// Return the items that were taken
			InventoryUtils.tryCloneInto(temp, inventory);
			
			return false;
		}
		finally {
			// Clear the crafting storage
			temp.clear();
		}
	}
}
