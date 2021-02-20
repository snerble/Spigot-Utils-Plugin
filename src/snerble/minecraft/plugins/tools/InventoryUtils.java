package snerble.minecraft.plugins.tools;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

/**
 * Provides helper functions for bukkit inventories.
 * @author Conor
 *
 */
public final class InventoryUtils {
	private InventoryUtils() { }
	
	/**
	 * @param source - The inventory to clone.
	 * @param newHolder - The holder for the cloned inventory. May be null.
	 * @return A new {@link Inventory} with the contents of the given
	 * source inventory.
	 */
	public static Inventory clone(Inventory source, InventoryHolder newHolder) {
		Inventory clone = Bukkit.createInventory(newHolder, source.getStorageContents().length);
		clone.setStorageContents(source.getStorageContents());
		return clone;
	}
	
	/**
	 * Attempts to copy the items from one inventory and insert them into another.
	 * @param from - An inventory containing items to clone and add to another inventory.
	 * @param to - An inventory to which the cloned items will be added.
	 * @return <code>true</code> if the operation was successfull. Otherwise <code>false</code>.
	 */
	public static boolean tryCloneInto(Inventory from, Inventory to) {
		// Clone the destination inventory
		Inventory clone = clone(to, null);
		
		// Add the contents from the source to the clone
		boolean success = Arrays.stream(from.getStorageContents())
				.filter(x -> x != null)
				.allMatch(x -> clone.addItem(x).isEmpty());
		
		if (success) {
			// Copy contents of the clone onto the destination
			to.setStorageContents(clone.getStorageContents());
		}
		
		return success;
	}
	
	/**
	 * @param inventory - The inventory to look through.
	 * @param predicate - A predicate to test the items with.
	 * @return The amount of items in the inventory that match a given predicate.
	 */
	public static int getAmount(Inventory inventory, Predicate<ItemStack> predicate) {
		return Arrays.stream(inventory.getStorageContents())
				.filter(x -> x != null)
				.filter(predicate)
				.mapToInt(ItemStack::getAmount)
				.sum();
	}
	
	/**
	 * Attempts to remove the items in one inventory from another inventory.
	 * @param itemsToRemove - An inventory containing items to remove from the other inventory.
	 * @param target - The inventory whose items will be removed.
	 * @return <code>true</code> if the operation was successfull. Otherwise <code>false</code>.
	 */
	public static boolean tryRemoveFrom(Inventory itemsToRemove, Inventory target) {
		// Clone the destination inventory
		Inventory clone = clone(target, null);
		
		if (!Arrays.stream(itemsToRemove.getStorageContents())
				.filter(x -> x != null)
				.allMatch(x -> tryTake(clone, x).isPresent())) {
			// Could not remove one of the items
			return false;
		}
		
		// Copy contents of the clone to the destination inventory
		target.setStorageContents(clone.getStorageContents());
		
		return true;
	}
	
	/**
	 * Attempts to remove items from the given inventory.
	 * @param inventory - The inventory to remove the items from.
	 * @param items - The items to remove.
	 * @return The actual items that were removed, or an empty value if the operation failed.
	 */
	public static Optional<ItemStack[]> tryTake(Inventory inventory, ItemStack items) {
		// Create a new MaterialChoice and use overload
		return tryTake(inventory,
				new RecipeChoice.MaterialChoice(items.getType()),
				items.getAmount());
	}

	/**
	 * Attempts to remove items from the given inventory.
	 * @param inventory - The inventory to remove the items from.
	 * @param choice - A {@link RecipeChoice} that determines which items will be removed.
	 * @param amount - The amount of items to remove.
	 * @return The actual items that were removed, or an empty value if the operation failed.
	 */
	public static Optional<ItemStack[]> tryTake(Inventory inventory, RecipeChoice choice, int amount) {
		// Wrapper for use in the anyMatch method
		Wrapper<Integer> amountFound = new Wrapper<>(0);
		
		// Test if the inventory contains enough items
		if (!Arrays.stream(inventory.getStorageContents())
				.filter(x -> x != null)
				.filter(choice::test)
				.mapToInt(ItemStack::getAmount)
				.anyMatch(x -> {
					// Find if any stack had enough items to push the counter to amount
					amountFound.setValue(amountFound.getValue() + x);
					return amountFound.getValue() >= amount;
				})) {
			// Inventory does not contain enough elements
			return Optional.empty();
		}
		
		// Create small inventory for combining the stacks of removed items
		Inventory removed = Bukkit.createInventory(null, 9);
		
		// Remove items from matching stacks (smallest stacks first)
		int toRemove = amount;
		for (Iterator<ItemStack> iterator = Arrays.stream(inventory.getStorageContents())
				.filter(x -> x != null)
				.filter(choice::test)
				.sorted((a, b) -> Integer.compare(a.getAmount(), b.getAmount()))
				.iterator();
				iterator.hasNext();) {

			ItemStack stack = iterator.next();

			// Remove items from the stack
			int canRemove = Math.min(stack.getAmount(), toRemove);

			removed.addItem(new ItemStack(stack.getType(), canRemove));
			
			stack.setAmount(stack.getAmount() - canRemove);
			toRemove -= canRemove;
			
			// Remove empty stacks
			if (stack.getAmount() == 0)
				inventory.remove(stack);
			
			if (toRemove == 0)
				break;
		}
		
		return Optional.of(Arrays.stream(removed.getStorageContents())
				.filter(x -> x != null)
				.toArray(ItemStack[]::new));
	}
	
	private static final class Wrapper<T> {
		private T value;

		public Wrapper(T value) {
			this.value = value;
		}
		
		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}
	}
}
