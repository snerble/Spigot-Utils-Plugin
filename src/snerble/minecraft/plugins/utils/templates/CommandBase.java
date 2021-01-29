package snerble.minecraft.plugins.utils.templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import snerble.minecraft.plugins.tools.INamed;
import snerble.minecraft.plugins.tools.MessageFormatter;
import snerble.minecraft.plugins.utils.UtilsPlugin;

/**
 * Defines mechanisms used for commands throughout this plugin.
 * @author Conor
 *
 */
public abstract class CommandBase extends TabCompleterBase implements CommandExecutor, INamed {
	protected final Logger log;
	protected final MessageFormatter chat;

	private final String name;
	private final String[] aliases;
	private final List<CommandBase> subcommands = new ArrayList<>();
	
	protected CommandBase(String name, String... aliases) {
		log = UtilsPlugin.Instance.getLogger();
		chat = UtilsPlugin.Instance.chat;
		
		addTabCompletion(getDefaultTabCompletion());
		
		this.name = name;
		this.aliases = aliases;
	}
	
	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (args.length >= 1) {
				// Try to get a subcommand
				Optional<CommandBase> subcommand = getSubcommand(args[0]);
				
				if (subcommand.isPresent()) {
					// Invoke the subcommand while removing the first element of args
					return subcommand.get().onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
				}
			}
			
			// Run this command
			return onCommand(sender, command, args);
		}
		catch (Exception e) {
			log.log(Level.WARNING, "Unhandled exception", e);
			chat.sendMessage(sender, ActionResult.failed(new ActionError(e)));
			return false;
		}
	}
	public boolean onCommand(CommandSender sender, Command command, String[] args) {
		if (!getSubcommands().isEmpty()) {
			if (args.length != 0) {
				chat.sendMessage(sender, "Invalid argument '%s'.", args[0]);
				return false;
			}
			chat.sendMessage(sender, "Missing argument.");
			return false;			
		}
		
		// TODO put fallback here?
		
		chat.sendMessage(sender, "Not implemented.");
		return false;
	}
	
	@Override
	public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		try {
			// Forward to subcommands, but only if there is an extra argument that it can use for tab completion
			if (args.length >= 2) {
				// Try to get a subcommand
				Optional<CommandBase> subcommand = getSubcommand(args[0]);
				
				if (subcommand.isPresent()) {
					// Forward to the subcommand while removing the first element of args
					return subcommand.get().onTabComplete(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
				}
			}
			
			// TODO combine fallback with this thing's tab completion
			
			// Execute onTabComplete implementation
			return onTabComplete(sender, command, args);
		}
		catch (Exception e) {
			// Log unhandled exception
			log.log(Level.WARNING, "Unhandled exception", e);
			chat.sendMessage(sender, ActionResult.failed(new ActionError(e)));
			return new ArrayList<>();
		}
	}
	
	// TODO Consider removing
	public List<String> onTabComplete(CommandSender sender, Command command, String[] args) {
		return super.onTabComplete(sender, command, name, args);
	}
	
	/**
	 * Adds a subcommand to this command.
	 * @param command - The command to add.
	 * @return The given command instance.
	 */
	public <T extends CommandBase> T addSubcommand(T command) {
		subcommands.add(command);
		return command;
	}

	// TODO Consider moving to a concrete implementation of Command
	//  Default is only 100% useful if we know that the default command
	//  implementation is not overridden.
	/**
	 * Sets a subcommand as the fallback command.
	 */
	protected <T extends CommandBase> T setDefault(T command) {
		throw new NotImplementedException();
	}
	
	protected List<CommandBase> getSubcommands() {
		// TODO include fallback's subcommands
		return subcommands;
	}
	protected Optional<CommandBase> getSubcommand(String name) {
		return getSubcommands().stream()
				.filter(x -> INamed.getNames(x).contains(name))
				.findAny();
	}
	protected <T extends CommandBase> List<CommandBase> getSubcommands(Class<T> type) {
		return getSubcommands().stream()
				.filter(x -> type.isInstance(x))
				.collect(Collectors.toList());
	}

	private Function<String, List<String>> getDefaultTabCompletion() {
		return text -> {
			return getSubcommands().stream()
				.map(x -> INamed.getNames(x))
				.flatMap(List::stream)
				.filter(x -> x.toLowerCase().startsWith(text.toLowerCase()))
				.collect(Collectors.toList());
		};
	}
	
	@Override
	public final String getName() {
		return name;
	}
	public final String[] getAliases() {
		return aliases;
	}
}
