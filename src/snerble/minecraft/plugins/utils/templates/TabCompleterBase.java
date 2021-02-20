package snerble.minecraft.plugins.utils.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

abstract class TabCompleterBase {
	private Optional<TabCompleter> tabCompleter = Optional.empty();

	public List<String> getTabCompletions(String text) {
		return tabCompleter.orElse(x -> new ArrayList<>()).apply(text);
	}
	
	protected void setTabCompleter(TabCompleter tabCompleter) {
		if (this.tabCompleter.isPresent())
			throw new IllegalStateException("Tab completer has already been provided.");
		
		this.tabCompleter = Optional.of(tabCompleter);
	}
}
