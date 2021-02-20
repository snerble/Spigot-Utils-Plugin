package snerble.minecraft.plugins.utils.templates;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class Argument extends TabCompleterBase {
	private Function<String, Object> parser = x -> x;
	private Function<Object, ActionResult> validator = x -> ActionResult.SUCCESS;
	
	// TODO make argument subclasses instead of these setTabCompleter ones
	public Argument setTag(String tag) {
		setTabCompleter(text -> Arrays.asList(new String[] { tag }));
		return this;
	}
	public Argument setOptions(Supplier<String[]> options) {
		setTabCompleter(text ->
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
	
	public FunctionResult parse(String text) {
		try {
			return FunctionResult.success(parser.apply(text));
		}
		catch (Exception e) {
			return FunctionResult.failed(new ActionError("'%s'", text));
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