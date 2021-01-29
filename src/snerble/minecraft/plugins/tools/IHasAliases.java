package snerble.minecraft.plugins.tools;

/**
 * Defines a mechanism for obtaining aliases from an object.
 * @author Conor
 *
 */
public interface IHasAliases {
	/**
	 * Returns an array of aliases for this object.
	 */
	public String[] getAliases();
}
