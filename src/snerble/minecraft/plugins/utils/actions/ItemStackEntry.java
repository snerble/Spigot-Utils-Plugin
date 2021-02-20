package snerble.minecraft.plugins.utils.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import snerble.minecraft.plugins.utils.Database;

/**
 * Represents a material with a specified quantity that is stored in the database.
 * @author Conor
 *
 */
public final class ItemStackEntry {
	private final Material material;
	private final int amount;

	public ItemStackEntry(Material material, int amount) {
		this.material = material;
		this.amount = amount;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public boolean isUnlimited() {
		return getAmount() < 0;
	}
	
	public String toShortString() {
		return String.format("%s*%d", getMaterial().name(), getAmount());
	}
	
	public static ItemStackEntry valueOf(String s) {
		String[] parts = s.split("\\*");
		
		Material material = Material.valueOf(parts[0]);
		int amount = Integer.valueOf(parts[1]);
		
		return new ItemStackEntry(material, amount);
	}
	
	public static List<ItemStackEntry> getEntriesFor(Object key, Player player) {
		return Database.Instance.getValue(player, key, new ArrayList<String>())
				.stream()
				.map(ItemStackEntry::valueOf)
				.collect(Collectors.toList());
	}
	
	public static void saveEntriesFor(Object key, List<ItemStackEntry> entries, Player player) {
		Database.Instance.setValue(player, key, entries.stream()
				.map(ItemStackEntry::toShortString)
				.collect(Collectors.toList()));
	}
	
	@Override
	public String toString() {
		return String.format(isUnlimited() ? "%s" : "%s x %d", getMaterial().name(), getAmount());
	}
}