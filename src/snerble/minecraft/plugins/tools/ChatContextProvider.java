package snerble.minecraft.plugins.tools;

/**
 * Defines a mechanism for obtaining a {@link ChatContext} instance.
 * @author Conor
 *
 */
public interface ChatContextProvider {
	/**
	 * @return A {@link ChatContext} instance.
	 */
	ChatContext getChatContext();
}
