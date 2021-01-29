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
 * Manages a value from the database.
 * @author Conor
 *
 */
// TODO Create a superclass from which this and ToggleCommand inherit, since they are so similar
public class SetValueCommand extends ValidationCommandBase {
	protected final Database database;

	private final Object key;
	
	private String displayName;
	private boolean hasArgument = false;
	private boolean global = false;
	
	public SetValueCommand(
			Object key,
			String name,
			String... aliases) {
		super(name, aliases);
		
		this.database = Database.Instance;
		this.key = key;
		this.displayName = name;
	}

	@Override
	protected final boolean onCommand(CommandSender sender, Command command, Object[] args) {
		// Disallow if a non-player invoked this command when it is not a global value
		if (!(sender instanceof Player) && !global) {
			chat.sendMessage(sender, "Command may only be issued by a player.");
			return false;
		}

		Player player = null;
		if (!global)
			player = (Player) sender;
		
		Object newValue = args[0];
		Optional<Object> oldValue = database.hasKey(player, key)
				? Optional.of(database.getValue(player, key, (Object) "<null>"))
				: Optional.empty();

		database.setValue(player, key, newValue);
		onSetValue(sender, command, oldValue, newValue);
		return true;
	}
	
	/**
	 * Invoked when the value gets set.
	 */
	protected void onSetValue(CommandSender sender, Command command, Optional<Object> oldValue, Object newValue) {
		if (oldValue.isPresent() && oldValue.get().equals(newValue)) {
			chat.sendMessage(sender, "%s is already %s.",
					displayName,
					newValue);
			return;
		}

		chat.sendMessage(sender, "%s set from %s to %s.",
				displayName,
				oldValue.orElse("<undefined>"),
				newValue);
	}

	public final String getDisplayName() {
		return displayName;
	}
	public final void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}
	
	@Override
	public Argument addArgument() {
		if (hasArgument)
			throw new IllegalStateException("An argument is already present.");
		
		Argument argument = super.addArgument();
		hasArgument = true;
		return argument;
	}
}
