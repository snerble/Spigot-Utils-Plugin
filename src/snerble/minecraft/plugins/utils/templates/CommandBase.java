package snerble.minecraft.plugins.utils.templates;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import snerble.minecraft.plugins.tools.ChatContext;
import snerble.minecraft.plugins.tools.NameProvider;

/**
 * Defines mechanisms used for commands throughout this plugin.
 * @author Conor
 *
 */
public abstract class CommandBase extends TabCompleterBase implements CommandExecutor, org.bukkit.command.TabCompleter, NameProvider {
	protected final Logger log;
	protected final ChatContext chat;

	private final List<Argument> arguments = new ArrayList<>();
	private final List<CommandBase> subcommands = new ArrayList<>();

	private String[] names = new String[1];
	
	private Optional<Command> command = Optional.empty();
	
	protected CommandBase(String name) {
		setName(name);
		
		log = Commands.log;
		chat = Commands.chat;
		
		setTabCompleter(getDefaultTabCompleter());
		
		names = new String[] { name };
	}
	
	@Override
	public final boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		// Invoke parameterless commandHandler if no args were passed
		if (args.length == 0)
			return onCommand(sender, command, alias);
		
		if (args.length == arguments.size()) {
			Object[] params = new Object[arguments.size()];
			
			// Parse the arguments
			ActionResult result = ActionResult.merge(
					IntStream.range(0, params.length)
							.mapToObj(i -> {
									FunctionResult r = arguments.get(i).parse(args[i]);
									if (r.succeeded())
										params[i] = r.getValue();
									return r;
							})
							.toArray(ActionResult[]::new));

			// Display the errors to the sender
			if (!result.succeeded()) {
				chat.send(sender, result.getMessage("Unable to parse : "));
				return false;
			}
			
			// Validate the arguments
			result = ActionResult.merge(
					IntStream.range(0, params.length)
							.mapToObj(i -> arguments.get(i).validate(params[i]))
							.toArray(ActionResult[]::new));

			// Display the errors to the sender
			if (!result.succeeded()) {
				chat.send(sender, result.getMessage("Invalid argument(s) : "));
				return false;
			}
			
			return onCommand(sender, command, alias, params);
		}
		// Incorrect parameter count
		else {
			ActionResult result = ActionResult.failed(
					new ActionError("Expected %d, got %d.",
							arguments.size(),
							args.length));
			
			chat.send(sender, result.getMessage("Parameter count mismatch : "));
			return false;
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String alias, Object[] args) {
		if (!getSubcommands().isEmpty()) {
			if (args.length != 0) {
				chat.send(sender, "Invalid argument '%s'.", args[0]);
				return false;
			}
			chat.send(sender, "Missing argument.");
			return false;
		}
		
		chat.send(sender, "Not implemented.");
		return false;
	}
	
	/**
	 * Invoked when no parameters are passed to this {@link ValidationCommandBase}.
	 * @return A value indicating success.
	 */
	public boolean onCommand(CommandSender sender, Command command, String alias) {
		ActionResult result;
		
		// If there are no subcommands, indicate missing positional args
		if (getSubcommands().isEmpty()) {
			result = ActionResult.failed(
					new ActionError("Missing %d argument%s.",
							arguments.size(),
							arguments.size() == 1 ? "" : "s"));
		}
		// If there are subcommands, indicate missing argument.
		else {
			result = ActionResult.failed(new ActionError("Missing argument."));
		}

		// Display the errors to the sender
		chat.send(sender, result);
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// Get the index of the argument to use for further tab completion
		int argIndex = Math.min(arguments.size(), args.length) - 1;
		
		List<String> tabCompletions = new ArrayList<>();
		if (argIndex >= 0) {
			// Get the tab completions from the argument at the last index
			tabCompletions.addAll(arguments.get(argIndex)
					.getTabCompletions(args[argIndex]));
		}
		
		// Append this command's tab completions if there is 1 arg
		if (args.length == 1)
			tabCompletions.addAll(getTabCompletions(args[0]));
		
		return tabCompletions;
	}

	public Argument addArgument() {
		Argument argument = new Argument();
		arguments.add(argument);
		return argument;
	}
	List<Argument> getArguments() {
		return arguments;
	}
	Argument getArgument(int index) {
		return arguments.get(index);
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
	
	protected List<CommandBase> getSubcommands() {
		return subcommands;
	}
	protected Optional<CommandBase> getSubcommand(String name) {
		return getSubcommands().stream()
				.filter(x -> Arrays.stream(x.getNames()).anyMatch(name::equalsIgnoreCase))
				.findAny();
	}
	protected <T extends CommandBase> List<CommandBase> getSubcommands(Class<T> type) {
		return getSubcommands().stream()
				.filter(x -> type.isInstance(x))
				.collect(Collectors.toList());
	}

	private TabCompleter getDefaultTabCompleter() {
		return text -> {
			return getSubcommands().stream()
				.map(x -> x.getNames())
				.flatMap(Arrays::stream)
				.filter(x -> x.toLowerCase().startsWith(text.toLowerCase()))
				.collect(Collectors.toList());
		};
	}
	
	/**
	 * @return The name of this command.
	 */
	public final String getName() {
		return names[0];
	}
	/**
	 * @param name - The name of this command.
	 */
	public final void setName(String name) {
		if (StringUtils.isEmpty(name))
			throw new InvalidParameterException();
		
		command.ifPresent(x -> x.setName(name));
		names[0] = name;
	}
	
	@Override
	public final String[] getNames() {
		return names.clone();
	}
	
	/**
	 * @return This command's aliases.
	 */
	public final String[] getAliases() {
		return Arrays.copyOfRange(names, 1, getNames().length);
	}
	/**
	 * @param aliases - An array of command aliases.
	 * @return This {@link CommandBase} instance.
	 */
	public final CommandBase setAliases(String[] aliases) {
		command.ifPresent(x -> x.setAliases(Arrays.asList(aliases)));
		
		String name = getName();
		names = new String[aliases.length + 1];
		names[0] = name;
		System.arraycopy(aliases, 0, names, 1, aliases.length);

		
		return this;
	}
	
	/**
	 * @param command - The command that this {@link CommandBase} is now registered to.
	 */
	void setCommand(Command command) {
		this.command = Optional.of(command);
		command.setAliases(Arrays.asList(getAliases()));
	}
}
