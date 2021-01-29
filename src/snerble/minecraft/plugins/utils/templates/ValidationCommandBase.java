/**
 * 
 */
package snerble.minecraft.plugins.utils.templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Validates arguments passed to a command before executing it's code.
 * @author Conor
 *
 */
public abstract class ValidationCommandBase extends CommandBase {
	public static final class Argument extends TabCompleterBase {
		public static final class ParseResult extends ActionResult {
			private final Optional<Object> value;
			
			private ParseResult(Optional<Object> value, ActionError... errors) {
				super(value.isPresent(), errors);
				this.value = value;
			}
			
			public static ParseResult success(Object value) {
				return new ParseResult(Optional.ofNullable(value));
			}
			public static ParseResult failed(ActionError... errors) {
				return new ParseResult(Optional.empty(), errors);
			}
			
			public Object getValue() {
				return value.get();
			}
		}
		
		private boolean hasTabCompletion = false;
		private Function<String, Object> parser = x -> x;
		private Function<Object, ActionResult> validator = x -> ActionResult.SUCCESS;
		
		public Argument setTag(String tag) {
			assertNoTabCompletion();
			addTabCompletion(text -> Arrays.asList(new String[] { tag }));
			return this;
		}
		public Argument setOptions(Supplier<String[]> options) {
			assertNoTabCompletion();
			addTabCompletion(text -> 
					Arrays.stream(options.get())
							.filter(x -> x.toLowerCase().startsWith(text.toLowerCase()))
							.collect(Collectors.toList()));
			return this;
		}
		public Argument setParser(Function<String, ? super Object> parser) {
			this.parser = parser;
			return this;
		}
		public Argument setValidator(Function<Object, ActionResult> validator) {
			this.validator = validator;
			return this;
		}
		
		private void assertNoTabCompletion() {
			if (hasTabCompletion)
				throw new IllegalStateException("Tab completion is already provided.");
			hasTabCompletion = true;
		}
		
		public ParseResult parse(String text) {
			try {
				return ParseResult.success(parser.apply(text));
			}
			catch (Exception e) {
				return ParseResult.failed(new ActionError("'%s'", text));
			}
		}
		public ActionResult validate(Object value) {
			try {
				return validator.apply(value);
			} catch (Exception e) {
				return ActionResult.failed(new ActionError(e));
			}
		}
	}
	
	private final List<Argument> arguments = new ArrayList<>();
	
	/**
	 * Initializes a new instance of {@link ValidationCommandBase}.
	 */
	protected ValidationCommandBase(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	public final boolean onCommand(CommandSender sender, Command command, String[] args) {
		int missingParams = arguments.size() - args.length;
		if (missingParams > 0) {
			// Indicate that some parameters are missing
			ActionResult result = ActionResult.failed(
					new ActionError("Missing %d argument%s.",
							missingParams,
							missingParams == 1 ? "" : "s"));

			// Display the errors to the sender
			chat.sendMessage(sender, result);
			return false;
		}
		else {
			Object[] params = new Object[arguments.size()];
			
			// Parse the arguments
			ActionResult result = ActionResult.merge(
					IntStream.range(0, params.length)
							.mapToObj(i -> {
									Argument.ParseResult r = arguments.get(i).parse(args[i]);
									if (r.succeeded())
										params[i] = r.getValue();
									return r;
							})
							.toArray(ActionResult[]::new));

			// Display the errors to the sender
			if (!result.succeeded()) {
				chat.sendMessage(sender, result.getMessage("Unable to parse: "));
				return false;
			}
			
			// Validate the arguments
			result = ActionResult.merge(
					IntStream.range(0, params.length)
							.mapToObj(i -> arguments.get(i).validate(params[i]))
							.toArray(ActionResult[]::new));

			// Display the errors to the sender
			if (!result.succeeded()) {
				chat.sendMessage(sender, result.getMessage("Invalid arguments: "));
				return false;
			}
			
			return onCommand(sender, command, params);
		}
	}
	
	protected abstract boolean onCommand(CommandSender sender, Command command, Object[] args);

	@Override
	public final List<String> onTabComplete(CommandSender sender, Command command, String[] args) {
		// Get the index of the argument to use for further tab completion
		int argIndex = Math.max(0, Math.min(arguments.size(), args.length) - 1);

		// Get the tab completions from the argument at the last index
		List<String> tabCompletions = arguments.get(argIndex)
				.onTabComplete(sender, command, getName(), Arrays.copyOfRange(args, argIndex, args.length));
		
		// Append this command's tab completions if there is 1 arg
		if (args.length == 1)
			tabCompletions.addAll(super.onTabComplete(sender, command, args));
		
		return tabCompletions;
	}
	
	public Argument addArgument() {
		Argument argument = new Argument();
		arguments.add(argument);
		return argument;
	}
}
