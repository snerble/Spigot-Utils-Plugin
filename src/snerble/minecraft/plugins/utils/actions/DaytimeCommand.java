/**
 * 
 */
package snerble.minecraft.plugins.utils.actions;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

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
	public boolean onCommand(CommandSender sender, Command command, String[] args) {
		if (sender instanceof Entity) {
			World world = ((Entity)sender).getWorld();
			
			world.setFullTime((long) (Math.ceil(world.getFullTime() / TICKS_PER_INGAME_DAY) * TICKS_PER_INGAME_DAY));
			
			chat.sendMessage(sender, "Time set to %s", world.getFullTime());
			return true;
		}
		else {
			chat.sendMessage(sender, "This command can only be issued by entities.");
			return false;
		}
	}
}
