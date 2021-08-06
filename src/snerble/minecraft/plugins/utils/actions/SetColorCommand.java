package snerble.minecraft.plugins.utils.actions;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import snerble.minecraft.plugins.utils.Database;
import snerble.minecraft.plugins.utils.Tag;
import snerble.minecraft.plugins.utils.UtilsChatContext;
import snerble.minecraft.plugins.utils.annotations.RegisterCommand;
import snerble.minecraft.plugins.utils.templates.SetValueCommand;

/**
 * Manages the plugin color variable.
 * @author Conor
 *
 */
@RegisterCommand
public class SetColorCommand extends SetValueCommand {
	public SetColorCommand() {
		super(Tag.PLUGIN_COLOR, "setColor");
		
		setGlobal(true);
		setDisplayName("Color");
		setFallback(ChatColor.DARK_PURPLE.name());
		
		// Initialize plugin color
		((UtilsChatContext) chat).setColor(
				ChatColor.valueOf(Database.Instance.getValue(Tag.PLUGIN_COLOR,
						(String) getFallback())));
		
		// Add the argument for all chat colors
		addArgument()
				.setOptions(() -> Arrays.stream(ChatColor.values())
						.filter(x -> x.isColor())
						.map(x -> x.name())
						.toArray(String[]::new))
				.setParser(SetColorCommand::colorParser);
	}
	
	private static String colorParser(String text) {
		return Arrays.stream(ChatColor.values())
				.filter(x -> x.isColor() && x.name().equalsIgnoreCase(text))
				.findFirst().get().name();
	}
	
	@Override
	protected void onSetValue(CommandSender sender, Command command, Optional<Object> oldValue, Object newValue) {
		ChatColor newColor = ChatColor.valueOf((String) newValue);
		((UtilsChatContext) chat).setColor(newColor);
		
		ChatColor oldColor = ChatColor.valueOf((String) oldValue.orElse(getFallback()));
		chat.send(sender, "%s changed from %s%s%s to %s%s",
				getDisplayName(),
				oldColor, oldColor.name(),
				ChatColor.RESET,
				newColor, newColor.name());
	}

	@Override
	protected void onGetValue(CommandSender sender, Command command, Optional<Object> value) {
		ChatColor color = ChatColor.valueOf((String) value.orElse(getFallback()));
		
		chat.send(sender, "%s is %s%s%s.",
				getDisplayName(),
				color, color.name(),
				ChatColor.RESET);
	}
}
