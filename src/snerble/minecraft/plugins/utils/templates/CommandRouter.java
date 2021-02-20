/**
 * 
 */
package snerble.minecraft.plugins.utils.templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handles command events and command routing.
 * @author Conor
 *
 */
final class CommandRouter implements CommandExecutor, TabCompleter {
	private final List<CommandBase> commands;
	
	/**
	 * Initializes a new instance of {@link CommandRouter} and registers it
	 * on the plugin for each of the given commands.
	 * @param commands - A list of {@link CommandBase} instances. 
	 */
	public CommandRouter(JavaPlugin plugin, List<CommandBase> commands) {
		this.commands = commands;

		// Register this router for every command in the list
		commands.forEach(command -> {
			PluginCommand pluginCommand = plugin.getCommand(command.getName());
			
			if (pluginCommand == null)
				throw new IllegalStateException(
						String.format("No command named '%s'", command.getName()));

			Commands.log.info(String.format("Registering command '%s' with instance of %s...",
					command.getName(),
					command.getClass().getSimpleName()));
			
			pluginCommand.setExecutor(this);
			pluginCommand.setTabCompleter(this);
			command.setCommand(pluginCommand);
		});
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		// Get the command
		CommandBase c = commands.stream()
				.filter(x -> x.getName().equalsIgnoreCase(command.getName()))
				.findFirst().get();
		
		try {
			// Recursively find and run the command
			return onCommand(c, sender, command, alias, args);
		}
		catch (Exception e) {
			Commands.log.log(Level.WARNING, "Unhandled exception", e);
			Commands.chat.send(sender, ActionResult.failed(new ActionError(e)));
			return false;
		}
	}
	private static boolean onCommand(CommandBase c, CommandSender sender, Command command, String alias, String[] args) {
		Optional<String> arg = Arrays.stream(args).findFirst();
		
		// If an argument is present, try to return a subcommand
		if (arg.isPresent()) {			
			// Try to get a subcommand
			Optional<CommandBase> subcommand = c.getSubcommand(arg.get());
			
			if (subcommand.isPresent()) {
				// Search through the subcommand while removing the first element of args
				return onCommand(subcommand.get(), sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
			}
		}

		// Run the command
		return c.onCommand(sender, command, alias, args);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// Get the command
		CommandBase c = commands.stream()
				.filter(x -> x.getName().equalsIgnoreCase(command.getName()))
				.findFirst().get();

		try {
			// Recursively find and invoke the correct command
			return onTabComplete(c, sender, command, alias, args);
		}
		catch (Exception e) {
			Commands.log.log(Level.WARNING, "Unhandled exception", e);
			Commands.chat.send(sender, ActionResult.failed(new ActionError(e)));
			return new ArrayList<>();
		}
	}
	private static List<String> onTabComplete(CommandBase c, CommandSender sender, Command command, String alias, String[] args) {
		Optional<String> arg = Arrays.stream(args).findFirst();
		
		// If an argument is present, try to return a subcommand
		if (arg.isPresent()) {			
			// Try to get a subcommand
			Optional<CommandBase> subcommand = c.getSubcommand(arg.get());
			
			if (subcommand.isPresent()) {
				// Search through the subcommand while removing the first element of args
				return onTabComplete(subcommand.get(), sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
			}
		}
		
		return c.onTabComplete(sender, command, alias, args);
	}
}
