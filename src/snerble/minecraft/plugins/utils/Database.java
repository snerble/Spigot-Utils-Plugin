package snerble.minecraft.plugins.utils;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import snerble.minecraft.plugins.tools.ISectionedDatabase;
import snerble.minecraft.plugins.tools.PersistentStore;

/**
 * Provides mechanisms for storing persistent data.
 * @author Conor
 *
 */
public class Database implements ISectionedDatabase, PersistentStore {
	private static final String GLOBAL_SECTION = "Global";
	
	/**
	 * The single instance of the {@link Database} class.
	 */
	public final static Database Instance = new Database();
	
	private final Logger log;
	
	private FileConfiguration config;
	
	private Database() {
		log = UtilsPlugin.Instance.getLogger();
	}
	
	/**
	 * Returns a global value with a specified key.
	 * @param <T> - The type to cast the value to.
	 * @param key - The key of the value.
	 * @param fallback - Returned when the requested value is not defined.
	 */
	public <T> T getValue(Object key, T fallback) {
		return getValue(GLOBAL_SECTION, key, fallback);
	}
	/**
	 * Returns a value with given key for a player.
	 * @param <T> - The type to cast the value to.
	 * @param player - The player whose values to get.
	 * @param key - The key of the value.
	 * @param fallback - Returned when the requested value is not defined.
	 */
	public <T> T getValue(Player player, Object key, T fallback) {
		if (player == null)
			return getValue(key, fallback);
		return getValue(player.getUniqueId().toString(), key, fallback);
	}
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(String section, Object key, T fallback) {
		assertLoaded();
		
		if (!hasKey(section, key))
			return fallback;
		return (T) config.get(section + '.' + key);
	}

	/**
	 * Sets a value in the global section with the specified key.
	 * @param key - The key of the new value.
	 * @param value - The value to set.
	 */
	public void setValue(Object key, Object value) {
		setValue(GLOBAL_SECTION, key, value);
	}
	/**
	 * Sets a value with the specified key for a player.
	 * @param player - The player whose values to set.
	 * @param key - The key of the new value.
	 * @param value - The value to set.
	 */
	public void setValue(Player player, Object key, Object value) {
		if (player == null)
			setValue(key, value);
		else
			setValue(player.getUniqueId().toString(), key, value);
	}
	@Override
	public void setValue(String section, Object key, Object value) {
		assertLoaded();
		
		config.set(section + '.' + key, value);
	}

	/**
	 * Returns whether the global section contains the given key.
	 * @param key - The key to check.
	 */
	public boolean hasKey(Object key) {
		return hasKey(GLOBAL_SECTION, key);
	}
	/**
	 * Returns whether the player has the given key.
	 * @param player - The player whose values to check.
	 * @param key - The key to check.
	 */
	public boolean hasKey(Player player, Object key) {
		if (player == null)
			return hasKey(key);
		return hasKey(player.getUniqueId().toString(), key);
	}
	@Override
	public boolean hasKey(String section, Object key) {
		assertLoaded();
		
		return config.contains(section + '.' + key, true);
	}

	@Override
	public void load() {
		// Don't reload on startup
		if (config != null)
			UtilsPlugin.Instance.reloadConfig();
		
		config = UtilsPlugin.Instance.getConfig();

		// Add a version tag if it is missing
		if (!hasKey(Tag.PLUGIN_VERSION)) {
			setValue(Tag.PLUGIN_VERSION, UtilsPlugin.Instance.getDescription().getVersion());
			save();
		}
		// Clear database if plugin version value does not match
		else if (!getValue(Tag.PLUGIN_VERSION, "").equals(UtilsPlugin.Instance.getDescription().getVersion())) {
			log.warning("Database version mismatch detected! Reverting to default state...");
			
			UtilsPlugin.Instance.saveDefaultConfig();

			// Set version value
			setValue(Tag.PLUGIN_VERSION, UtilsPlugin.Instance.getDescription().getVersion());
			save();
		}
		
		log.fine("Finished loading database");
	}
	
	/**
	 * Loads defaults into the current database.
	 */
	public void loadDefaults() {
		log.fine("Loading database defaults...");
		
		UtilsPlugin.Instance.saveDefaultConfig();
		load();
	}

	@Override
	public void save() {
		if (config == null)
			return;
		
		UtilsPlugin.Instance.saveConfig();
		log.fine("Saved database");
	}
	
	/**
	 * Raises an exception if the database is not loaded.
	 */
	private void assertLoaded() {
		if (config == null)
			throw new IllegalStateException("Database is not initialized.");
	}
}
