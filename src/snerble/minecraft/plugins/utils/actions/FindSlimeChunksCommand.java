package snerble.minecraft.plugins.utils.actions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import snerble.minecraft.plugins.utils.Tag;
import snerble.minecraft.plugins.utils.annotations.RegisterCommand;
import snerble.minecraft.plugins.utils.templates.ActionError;
import snerble.minecraft.plugins.utils.templates.ActionResult;
import snerble.minecraft.plugins.utils.templates.CommandBase;

@RegisterCommand
public class FindSlimeChunksCommand extends CommandBase {
	public FindSlimeChunksCommand() {
		super(Tag.SLIMECHUNKS.name);
		
		addArgument()
				.setTag("<radius>")
				.setParser(Integer::valueOf)
				.setValidator(o -> {
					int i = (int) o;
					if (i <= 0)
						return ActionResult.failed(new ActionError("'radius' out of range (0 < x)"));
					return ActionResult.SUCCESS;
				});
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, Object[] args) {
		if (!(sender instanceof Entity)) {
			chat.send(sender, "This command can only be issued by entities.");
			return false;
		}
		Entity entity = (Entity) sender;
		World world = entity.getWorld();
		
		int radius = (int) args[0];
		
		int entityChunkX = entity.getLocation().getChunk().getX();
		int entityChunkZ = entity.getLocation().getChunk().getZ();
		
		List<Chunk> slimeChunks = new ArrayList<Chunk>();
		
		outerloop:
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				Chunk chunk = world.getChunkAt(entityChunkX + x, entityChunkZ + z);
				if (chunk.isSlimeChunk())
					slimeChunks.add(chunk);
				
				if (slimeChunks.size() == 50)
					break outerloop;
			}
		}
		
		if (slimeChunks.isEmpty()) {
			chat.send(sender, "No slime chunks found.");
		}
		else {
			chat.send(sender, "Found %d slime chunk%s:",
					slimeChunks.size(),
					slimeChunks.size() == 1 ? "" : "s");
			slimeChunks.forEach(x -> chat.send(sender, "x: %d, z: %d", x.getX(), x.getZ()));
		}
		
		return true;
	}
}
