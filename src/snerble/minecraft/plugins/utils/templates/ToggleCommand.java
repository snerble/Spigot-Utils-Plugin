package snerble.minecraft.plugins.utils.templates;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import snerble.minecraft.plugins.utils.Database;

public class ToggleCommand extends CommandBase {
	private static enum Option {
		TOGGLE,
		ENABLE,
		DISABLE
	}
	
	private final Database database;
	private final Object key;
	
	private String displayName;
	private boolean global = false;
	
	/**
	 * Initializes a new instance of {@link ToggleCommand}.
	 */
	public ToggleCommand(Object key, String name) {
		super(name);
		
		this.database = Database.Instance;
		this.key = key;
		this.displayName = name;
		
		addArgument()
				.setOptions(() -> Arrays.stream(Option.values())
						.map(x -> x.name().toLowerCase())
						.toArray(String[]::new))
				.setParser(text -> {
					try {						
						return Option.valueOf(text.toUpperCase());
					} catch (Exception e) {
						throw new RuntimeException(String.format("Invalid argument '%s'", text));
					}
				});
	}

	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, Object[] args) {
		// Disallow if a non-player invoked this command when it is not a global value
		if (!(sender instanceof Player) && !global) {
			chat.send(sender, "Command may only be issued by a player.");
			return false;
		}

		Player player = null;
		if (!global)
			player = (Player) sender;
		
		boolean newValue, oldValue = database.getValue(player, key, false);

		switch ((Option) args[0]) {
		case DISABLE:
			newValue = false;
			break;
			
		case ENABLE:
			newValue = true;
			break;
		
		default:
		case TOGGLE:
			newValue = !oldValue;
			break;
		}
		
		if (newValue == oldValue) {
			chat.send(sender, "%s is already %s.",
					displayName,
					newValue ? "enabled" : "disabled");
			return true;
		}
		
		database.setValue(player, key, newValue);
		chat.send(sender, "%s is now %s.",
				displayName,
				newValue ? "enabled" : "disabled");
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias) {
		// Disallow if a non-player invoked this command when it is not a global value
		if (!(sender instanceof Player) && !global) {
			chat.send(sender, "Command may only be issued by a player.");
			return false;
		}
		
		Player player = null;
		if (!global)
			player = (Player) sender;
		
		boolean value = database.getValue(player, key, false);
		
		chat.send(sender, "%s is %s.",
				displayName,
				value ? "enabled" : "disabled");
		return true;
	}
}
