/**
 * 
 */
package snerble.minecraft.plugins.utils.templates;

import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import snerble.minecraft.plugins.utils.Database;

/**
 * Command template which manages a specific value from the database.
 * @author Conor
 *
 */
// TODO Implement a converter which is 2 methods, one that takes Object and returns T and another that takes T and returns Object.
public class SetValueCommand extends CommandBase {
	/**
	 * The key of the value in the database that should be managed.
	 */
	private final Object key;
	
	/**
	 * The fallback value used in case the value is not defined yet.
	 */
	private Object fallback = "<undefined>";
	
	/**
	 * The name used when describing the value in messages.
	 */
	private String displayName;
	/**
	 * A boolean indicating whether the value is global or per player.
	 */
	private boolean global = false;
	
	/**
	 * Initializes a new instance of {@link SetValueCommand} with the specified
	 * key and command name.
	 * @param key - The key of the database value to affect.
	 * @param name - The name of this command.
	 */
	public SetValueCommand(Object key, String name) {
		super(name);
		
		this.key = key;
		this.displayName = name;
	}

	@Override
	public final boolean onCommand(CommandSender sender, Command command, String alias, Object[] args) {
		// Disallow if a non-player invoked this command when it is not a global value
		if (!(sender instanceof Player) && !isGlobal()) {
			chat.send(sender, "Command may only be issued by a player.");
			return false;
		}

		Player player = null;
		if (!isGlobal())
			player = (Player) sender;
		
		Object newValue = args[0];
		Optional<Object> oldValue = Database.Instance.hasKey(player, key)
				? Optional.of(Database.Instance.getValue(player, key, (Object) null))
				: Optional.empty();

		// Set the value in the database and call the onSetValue event handler.
		Database.Instance.setValue(player, key, newValue);
		onSetValue(sender, command, oldValue, newValue);
		return true;
	}
	
	@Override
	public final boolean onCommand(CommandSender sender, Command command, String alias) {
		// Disallow if a non-player invoked this command when it is not a global value
		if (!(sender instanceof Player) && !isGlobal()) {
			chat.send(sender, "Command may only be issued by a player.");
			return false;
		}

		// Get the player who invoked the command (or null)
		Player player = null;
		if (!isGlobal())
			player = (Player) sender;
		
		// Get the current value, or Optional.empty() if it isn't defined.
		Optional<Object> value = Database.Instance.hasKey(player, key)
				? Optional.of(Database.Instance.getValue(player, key, (Object) null))
				: Optional.empty();
		onGetValue(sender, command, value);
		
		return true;
	}
	

	/**
	 * Invoked when the value gets set.
	 */
	protected void onSetValue(CommandSender sender, Command command, Optional<Object> oldValue, Object newValue) {
		if (oldValue.isPresent() && oldValue.get().equals(newValue)) {
			chat.send(sender, "%s is already %s.",
					getDisplayName(),
					newValue);
			return;
		}

		chat.send(sender, "%s set from %s to %s.",
				getDisplayName(),
				oldValue.orElse(getFallback()),
				newValue);
	}

	/**
	 * Invoked when this command is invoked with no arguments and should tell the
	 * sender what the current value is.
	 */
	protected void onGetValue(CommandSender sender, Command command, Optional<Object> value) {
		// Tell the sender what the current value is
		chat.send(sender, "%s is %s.",
				getDisplayName(),
				value.orElse(getFallback()));
	}
	
	/**
	 * @return The display name of this command.
	 */
	public final String getDisplayName() {
		return displayName;
	}
	/**
	 * Sets the display name of this command.
	 */
	public final void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return Whether the value managed by this command is global or per player.
	 */
	public boolean isGlobal() {
		return global;
	}
	/**
	 * Sets whether the value managed by this command is global or per player.
	 */
	public void setGlobal(boolean global) {
		this.global = global;
	}
	
	/**
	 * @return The fallback value used when the value is not yet defined.
	 */
	public Object getFallback() {
		return fallback;
	}

	/**
	 * @param fallback - The value to set as a fallback for when the value is not yet defined.
	 */
	public void setFallback(Object fallback) {
		this.fallback = fallback;
	}
	

	@Override
	public Argument addArgument() {
		if (!super.getArguments().isEmpty())
			throw new IllegalStateException("An argument is already present.");

		return super.addArgument();
	}
}
