package snerble.minecraft.plugins.utils.templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import snerble.minecraft.plugins.tools.IHasAliases;
import snerble.minecraft.plugins.tools.IHasName;

public abstract class TabCompleterBase implements TabCompleter {
	private final List<Function<String, List<String>>> tabCompleters = new ArrayList<>();
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		// Ignore if more than one argument was passed here
		if (args.length > 1)
			return new ArrayList<>();
		
		// Get the first argument
		String arg = Arrays.stream(args).findFirst().orElse("");
		
		// Get this command's tab completion
		return getTabCompletion(arg);
	}
	
	protected TabCompleterBase addTabCompletion(Function<String, List<String>> getter) {
		tabCompleters.add(getter);
		return this;
	}
	protected TabCompleterBase addTabCompletion(List<String> values) {
		return addTabCompletion(text -> values.stream()
				.filter(x -> filter(x, text))
				.collect(Collectors.toList()));
	}
	protected TabCompleterBase addTabCompletion(Supplier<List<String>> values) {
		return addTabCompletion(text -> values.get().stream()
				.filter(x -> filter(x, text))
				.collect(Collectors.toList()));
	}
	protected <T> TabCompleterBase addTabCompletion(List<T> values, Function<T, String> getter) {
		return addTabCompletion(values.stream()
				.map(getter)
				.collect(Collectors.toList()));
	}
	protected <T extends Enum<T> & IHasName> TabCompleterBase addTabCompletion(Class<T> enumType) {
		return addTabCompletion(Arrays.stream(enumType.getEnumConstants())
				.map(x -> getNames(x))
				.flatMap(List::stream)
				.collect(Collectors.toList()));
	}

	protected List<String> getTabCompletion(String text) {
		return tabCompleters.stream()
				.map(x -> x.apply(text))
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
	
	private static boolean filter(String value, String text) {
		return value.toLowerCase().startsWith(text.toLowerCase());
	}
	
	/**
	 * Gets a list of names that include the name from {@link IHasName}.
	 * If value implements {@link IHasAliases}, then it's aliases are also added.
	 */
	static List<String> getNames(IHasName value) {
		// Prepare list of tab completion names
		List<String> names = new ArrayList<>();
		names.add(value.getName());
		
		// Add the aliases if value also implements IHasAliases
		if (value instanceof IHasAliases)
			names.addAll(Arrays.asList(((IHasAliases) value).getAliases()));
		
		return names;
	}
}
