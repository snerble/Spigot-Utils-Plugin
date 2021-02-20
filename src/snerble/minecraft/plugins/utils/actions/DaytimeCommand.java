/**
 * 
 */
package snerble.minecraft.plugins.utils.actions;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import net.md_5.bungee.api.ChatColor;
import snerble.minecraft.plugins.utils.annotations.RegisterCommand;
import snerble.minecraft.plugins.utils.templates.CommandBase;

/**
 * Advances time without messing everything up.
 * @author Conor
 *
 */
@RegisterCommand
public class DaytimeCommand extends CommandBase {
	public DaytimeCommand() {
		super("daytime");
	}

	private final double TICKS_PER_INGAME_DAY = 24000;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias) {
		if (sender instanceof Entity) {
			Entity entity = (Entity) sender;
			World world = entity.getWorld();
			
			world.setFullTime((long) (Math.ceil(world.getFullTime() / TICKS_PER_INGAME_DAY) * TICKS_PER_INGAME_DAY));
			
			chat.broadcast("%s%s%s made it daytime.",
					ChatColor.GREEN,
					entity.getName(),
					ChatColor.RESET);
			return true;
		}
		else {
			chat.send(sender, "This command can only be issued by entities.");
			return false;
		}
	}
}
