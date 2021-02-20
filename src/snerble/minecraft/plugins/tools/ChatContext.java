/**
 * 
 */
package snerble.minecraft.plugins.tools;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Provides mechanisms for sending chat messages.
 * @author Conor
 *
 */
public abstract class ChatContext {
	/**
	 * Sends a message to the sender.
	 * @param sender - The target to send a message to.
	 * @param message - The message object.
	 * @param args - The arguments to format into the provided message.
	 */
	public final void send(CommandSender sender, Object message, Object... args) {
		if (message == null)
			message = "null";
		sender.sendMessage(getMessage(String.format(message.toString(), args)));
	}

	/**
	 * Broadcasts a message to the current server.
	 * @param message - The message object.
	 * @param args - The arguments to format into the provided message.
	 */
	public final void broadcast(Object message, Object... args) {
		if (message == null)
			message = "null";
		Bukkit.broadcastMessage(getMessage(String.format(message.toString(), args)));
	}
	
	/**
	 * Broadcasts a message to every user with the given permission in the current server.
	 * @param permission - The permission to broadcast the message to.
	 * @param message - The message object.
	 * @param args - The arguments to format into the provided message.
	 */
	public final void broadcast(Permission permission, Object message, Object... args) {
		if (message == null)
			message = "null";
		Bukkit.broadcast(getMessage(String.format(message.toString(), args)), permission.getName());
	}
	
	/**
	 * Formats the given contents into a message.
	 * @param content - The contents of the message.
	 * @return A formatted message.
	 */
	protected abstract String getMessage(String content);
}
