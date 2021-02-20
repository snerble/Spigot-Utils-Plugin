package snerble.minecraft.plugins.utils.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import snerble.minecraft.plugins.utils.Tag;
import snerble.minecraft.plugins.utils.annotations.RegisterCommand;
import snerble.minecraft.plugins.utils.templates.ActionError;
import snerble.minecraft.plugins.utils.templates.ActionResult;
import snerble.minecraft.plugins.utils.templates.CommandBase;
import snerble.minecraft.plugins.utils.templates.ToggleCommand;

/**
 * 
 * @author Conor
 *
 */
@RegisterCommand
public class AutoCraftCommand extends ToggleCommand {
	public AutoCraftCommand() {
		super(Tag.AUTOCRAFT_ENABLED, Tag.AUTOCRAFT_ENABLED.name);
		setDisplayName(AutoCrafter.NAME);
		
		addSubcommand(new AC_Recipes());
	}
	
	public static List<ItemStackEntry> getRecipesFor(Player player) {
		return ItemStackEntry.getEntriesFor(Tag.AUTOCRAFT_RECIPES, player);
	}
	
	public static void saveRecipesFor(List<ItemStackEntry> recipes, Player player) {
		ItemStackEntry.saveEntriesFor(Tag.AUTOCRAFT_RECIPES, recipes, player);
	}
	
	private static class AC_Recipes extends CommandBase {
		private static final String[] OPTIONS = Arrays.stream(Material.values())
				.map(x -> x.name().toLowerCase())
				.toArray(String[]::new);
		
		public AC_Recipes() {
			super(Tag.AUTOCRAFT_RECIPES.name);
			
			addSubcommand(new AC_R_Add());
			addSubcommand(new AC_R_Remove());
			addSubcommand(new AC_R_Clear());
		}
	
		@Override
		public boolean onCommand(CommandSender sender, Command command, String alias) {
			// Disallow if a non-player invoked this command
			if (!(sender instanceof Player)) {
				chat.send(sender, "Command may only be issued by a player.");
				return false;
			}
			
			Player player = (Player) sender;

			List<ItemStackEntry> currentRecipes = getRecipesFor(player);
			
			if (currentRecipes.isEmpty()) {
				chat.send(sender, "%s list is empty.", AutoCrafter.NAME);
			}
			else {
				chat.send(sender, "Recipes [%d]:", currentRecipes.size());
				currentRecipes.forEach(x -> chat.send(sender, "- %s", x));				
			}
			
			return true;
		}
		
		public static String[] getOptions() {
			return OPTIONS;
		}
		
		public static Material parse(String text) {
			return Material.valueOf(text.toUpperCase());
		}
		
		public static ActionResult validate(Object o) {
			Material m = (Material) o;
			List<Recipe> recipes = Bukkit.getServer().getRecipesFor(new ItemStack(m)).stream()
					.filter(x -> x instanceof ShapedRecipe || x instanceof ShapelessRecipe)
					.collect(Collectors.toList());
			
			if (recipes.isEmpty())
				return ActionResult.failed(new ActionError("No crafting recipes for %s", m.name().toLowerCase()));
			return ActionResult.SUCCESS;
		}
		
		private static class AC_R_Add extends CommandBase {
			public AC_R_Add() {
				super("add");
				
				addArgument()
						.setOptions(AC_Recipes::getOptions)
						.setParser(AC_Recipes::parse)
						.setValidator(AC_Recipes::validate);
				
				addArgument()
						.setTag("<amount>")
						.setParser(Integer::valueOf)
						.setValidator(o -> {
							int i = (int) o;
							if (i < -1)
								return ActionResult.failed(new ActionError("'amount' out of range (-1 <= x)"));
							return ActionResult.SUCCESS;
						});
			}
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String alias, Object[] args) {
				// Disallow if a non-player invoked this command
				if (!(sender instanceof Player)) {
					chat.send(sender, "Command may only be issued by a player.");
					return false;
				}

				Player player = (Player) sender;				
				
				Material material = (Material) args[0];
				int amount = (int) args[1];
				ItemStackEntry recipe = new ItemStackEntry(material, amount);
				
				List<ItemStackEntry> currentRecipes = getRecipesFor(player);
				
				Optional<ItemStackEntry> existingRecipe = currentRecipes.stream()
						.filter(x -> x.getMaterial().equals(material))
						.findAny();
				
				// Check if the stored value contains the exact recipe already
				if (existingRecipe.isPresent() && existingRecipe.get().getAmount() == amount) {
					chat.send(sender, "%s is already in the %s list.",
							recipe,
							AutoCrafter.NAME);
					return true;
				}
				
				// Remove the existing recipe if it is present
				existingRecipe.ifPresent(x -> currentRecipes.remove(x));
				
				// Add the recipe to the list of recipes
				currentRecipes.add(recipe);
				
				if (existingRecipe.isPresent()) {
					chat.send(sender, "Set limit for %s from %s to %s.",
							material.name(),
							existingRecipe.get().isUnlimited() ? "unlimited" : existingRecipe.get().getAmount(),
							recipe.isUnlimited() ? "unlimited" : amount);
				}
				else {					
					chat.send(sender, "Added %s to the %s list.",
							recipe,
							AutoCrafter.NAME);
				}
				
				// Save the modified list
				saveRecipesFor(currentRecipes, player);
				
				return true;
			}
		}
		
		private static class AC_R_Remove extends CommandBase {
			public AC_R_Remove() {
				super("remove");

				addArgument()
						.setOptions(AC_Recipes::getOptions)
						.setParser(AC_Recipes::parse)
						.setValidator(AC_Recipes::validate);
			}

			@Override
			public boolean onCommand(CommandSender sender, Command command, String alias, Object[] args) {
				// Disallow if a non-player invoked this command
				if (!(sender instanceof Player)) {
					chat.send(sender, "Command may only be issued by a player.");
					return false;
				}

				Player player = (Player) sender;
				Material material = (Material) args[0];

				List<ItemStackEntry> currentRecipes = getRecipesFor(player);
				
				Optional<ItemStackEntry> existingRecipe = currentRecipes.stream()
						.filter(x -> x.getMaterial().equals(material))
						.findAny();
				
				// Check if the stored value contained the material
				if (!existingRecipe.isPresent()) {
					chat.send(sender, "%s is not in the %s list.",
							material.name().toLowerCase(),
							AutoCrafter.NAME);
					return true;
				}
				
				currentRecipes.remove(existingRecipe.get());
				
				// Save the modified list
				saveRecipesFor(currentRecipes, player);
				
				chat.send(sender, "Removed %s from the %s list.",
						material.name().toLowerCase(),
						AutoCrafter.NAME);
				
				return true;
			}
		}
	
		private static class AC_R_Clear extends CommandBase {
			public AC_R_Clear() {
				super("clear");
			}
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String alias) {
				// Disallow if a non-player invoked this command
				if (!(sender instanceof Player)) {
					chat.send(sender, "Command may only be issued by a player.");
					return false;
				}
				
				Player player = (Player) sender;
				
				// Set the value to an empty list
				saveRecipesFor(new ArrayList<>(), player);
				
				chat.send(sender, "Cleared the %s list.", AutoCrafter.NAME);
				
				return true;
			}
		}
	}
}
