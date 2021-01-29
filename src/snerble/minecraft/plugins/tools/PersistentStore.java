package snerble.minecraft.plugins.tools;

/**
 * Defines mechanisms for interfacing with a persistent store.
 * @author Conor
 *
 */
public interface PersistentStore {
	/**
	 * Loads values from a persistent store.
	 */
	public void load();
	
	/**
	 * Writes the data to a persistent store.
	 */
	public void save();
}
