package snerble.minecraft.plugins.utils.templates;

import java.util.List;

@FunctionalInterface
public interface TabCompleter {
	List<String> apply(String text);
}
