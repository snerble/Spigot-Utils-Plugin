/**
 * 
 */
package snerble.minecraft.plugins.tools;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Defines mechanisms for formatting chat messages.
 * @author Conor
 *
 */
public class MessageFormatter {
	/**
	 * The color of the name in the chat messages.
	 */
	public ChatColor color = ChatColor.DARK_PURPLE;
	
	/**
	 * The format string used for formatting messages.
	 */
	public final String format = "%s[%s]" + ChatColor.RESET + " %s";
	
	/**
	 * The name that this formatter uses in it's messages.
	 */
	public final String name;
	
	/**
	 * Initializes a new instance of {@link MessageFormatter}.
	 * @param name - The name that this formatter uses.
	 */
	public MessageFormatter(String name) {
		this.name = name;
	}
	
	/**
	 * Formats the given text into a chat message.
	 * @param obj - The contents of the message.
	 * @return The formatted message.
	 */
	public String format(Object obj) {
		return String.format(format,
				color,
				name,
				obj.toString());
	}
	
	/**
	 * Formats the given arguments into the format into a
	 * chat message.
	 * @param format - The format string to format the arguments into.
	 * @param args - The arguments for the provided format string.
	 * @return The formatted message.
	 */
	public String format(String format, Object... args) {
		return format(String.format(format, args));
	}
	
	/**
	 * Sends a formatted message to the sender.
	 * @param sender - The target to send a message to.
	 * @param obj - The contents of the message.
	 */
	public void sendMessage(CommandSender sender, Object obj) {
		sender.sendMessage(format(obj));
	}

	/**
	 * Sends a formatted message to the sender.
	 * @param sender - The target to send a message to.
	 * @param format - The format string to format the arguments into.
	 * @param args - The arguments for the provided format string.
	 */
	public void sendMessage(CommandSender sender, String format, Object... args) {
		sender.sendMessage(format(format, args));
	}
}
