package snerble.minecraft.plugins.tools;

/**
 * Defines mechanisms for interacting with a persistent data store
 * using sections and key-value pairs.
 * @author Conor
 *
 */
public interface ISectionedDatabase {
	/**
	 * Returns a value with a specified key from a section.
	 * @param <T> - The type to cast the value to.
	 * @param section - The name of the section that contains the value.
	 * @param key - The key of the value.
	 * @param fallback - Returned when the requested value is not defined.
	 */
	public <T> T getValue(String section, Object key, T fallback);
	
	/**
	 * Sets a value in the given section with the specified key.
	 * @param section - The name of the section.
	 * @param key - The key of the new value.
	 * @param value - The value to set.
	 */
	public void setValue(String section, Object key, Object value);
	
	/**
	 * Returns whether the section contains the given key.
	 * @param section - The name of the section.
	 * @param key - The key to check.
	 */
	public boolean hasKey(String section, Object key);
}
