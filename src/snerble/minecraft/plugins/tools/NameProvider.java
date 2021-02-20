package snerble.minecraft.plugins.tools;

/**
 * Defines a mechanism for obtaining names from an object.
 * @author Conor
 *
 */
public interface NameProvider {
	/**
	 * @return The collection of names of this object.
	 */
	String[] getNames();
}
