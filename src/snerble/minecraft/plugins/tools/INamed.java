package snerble.minecraft.plugins.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines mechanisms for obtaining names and aliases from an object.
 * @author Conor
 *
 */
public interface INamed {
	/**
	 * Returns the name of this object.
	 */
	public String getName();
	/**
	 * Returns an array of aliases for this object.
	 */
	public String[] getAliases();
	
	/**
	 * Returns all names and aliases from an {@link INamed} object.
	 * @param o - The object whose names to get.
	 */
	public static List<String> getNames(INamed o) {
		List<String> names = new ArrayList<>();
		names.add(o.getName());
		names.addAll(Arrays.asList(o.getAliases()));
		return names;
	}
}
