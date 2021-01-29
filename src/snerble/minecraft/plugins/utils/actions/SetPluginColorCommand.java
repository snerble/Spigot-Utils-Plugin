package snerble.minecraft.plugins.utils.actions;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import snerble.minecraft.plugins.utils.Database;
import snerble.minecraft.plugins.utils.Tag;
import snerble.minecraft.plugins.utils.annotations.RegisterCommand;
import snerble.minecraft.plugins.utils.templates.SetValueCommand;

/**
 * Manages the plugin color variable.
 * @author Conor
 *
 */
@RegisterCommand
public class SetPluginColorCommand extends SetValueCommand {
	public SetPluginColorCommand() {
		super(Tag.PLUGIN_COLOR, "setColor");
		setGlobal(true);
		setDisplayName("Color");
		
		// Initialize plugin color
		chat.color = ChatColor.valueOf(
				Database.Instance.getValue(Tag.PLUGIN_COLOR,
						ChatColor.DARK_PURPLE.name()));
		
		// Add the argument for all chat colors
		addArgument()
				.setOptions(() -> Arrays.stream(ChatColor.values())
						.filter(x -> x.isColor())
						.map(x -> x.name())
						.toArray(String[]::new))
				.setParser(SetPluginColorCommand::colorParser);
	}
	
	private static String colorParser(String text) {
		return Arrays.stream(ChatColor.values())
				.filter(x -> x.isColor() && x.name().equalsIgnoreCase(text))
				.findFirst().get().name();
	}
	
	@Override
	protected void onSetValue(CommandSender sender, Command command, Optional<Object> oldValue, Object newValue) {
		ChatColor newColor = ChatColor.valueOf((String) newValue);
		chat.color = newColor;
		
		if (!oldValue.isPresent()) {
			chat.sendMessage(sender, "%s set to %s%s",
					getDisplayName(),
					newColor, newColor.name());
			return;
		}
		
		ChatColor oldColor = ChatColor.valueOf((String) oldValue.get());
		chat.sendMessage(sender, "%s changed from %s%s%s to %s%s",
				getDisplayName(),
				oldColor, oldColor.name(),
				ChatColor.RESET,
				newColor, newColor.name());
	}
}
