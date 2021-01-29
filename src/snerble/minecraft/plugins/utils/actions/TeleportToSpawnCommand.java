package snerble.minecraft.plugins.utils.actions;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import snerble.minecraft.plugins.utils.annotations.RegisterCommand;
import snerble.minecraft.plugins.utils.templates.CommandBase;

@RegisterCommand
public final class TeleportToSpawnCommand extends CommandBase {
	public TeleportToSpawnCommand() {
		super("spawn");
		
		addSubcommand(new TP_Bed());
		addSubcommand(new TP_Spawn());
		addSubcommand(new TP_Overworld());
	}
	
	private final class TP_Bed extends CommandBase {
		public TP_Bed() {
			super("bed");
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String[] args) {
			if (!(sender instanceof Player)) {
				chat.sendMessage(sender, "Command may only be issued by a player.");
				return false;
			}
			
			Player player = (Player) sender;
			Location newLocation = player.getBedSpawnLocation();
			
			if (newLocation == null) {
				// Fallback to the overworld command
				return TeleportToSpawnCommand.this.getSubcommands(TP_Overworld.class).get(0).onCommand(sender, command, getName(), args);
			}
			
			teleport(player, newLocation);
			chat.sendMessage(sender, "Teleported to bed.");
			
			return true;
		}
	}
	
	private final class TP_Spawn extends CommandBase {
		public TP_Spawn() {
			super("spawn");
		}

		@Override
		public boolean onCommand(CommandSender sender, Command command, String[] args) {
			if (!(sender instanceof Player)) {
				chat.sendMessage(sender, "Command may only be issued by a player.");
				return false;
			}
			
			Player player = (Player) sender;
			Location newLocation = player.getWorld().getSpawnLocation();
			
			teleport(player, newLocation);
			chat.sendMessage(sender, "Teleported to spawn.");
			
			return true;
		}
	}
	
	private final class TP_Overworld extends CommandBase {
		public TP_Overworld() {
			super("overworld");
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String[] args) {
			if (!(sender instanceof Player)) {
				chat.sendMessage(sender, "Command may only be issued by a player.");
				return false;
			}
			
			Player player = (Player) sender;
			Location newLocation = player.getServer().getWorlds().get(0).getSpawnLocation();
			
			teleport(player, newLocation);
			chat.sendMessage(sender, "Teleported to overworld spawn.");
			
			return true;
		}
	}
	
	private static void teleport(Player player, Location newLocation) {
		newLocation.setDirection(player.getLocation().getDirection());
		
		effect(player);
		player.teleport(newLocation);
		effect(player);
	}
	
	private static void effect(Player player) {
		World world = player.getWorld();
		
		// Spawn 8 poof particles at the player's feet and head
		Location effectLocation = player.getLocation().clone();
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		
		effectLocation.add(0, 1, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, effectLocation, 0);
		
		// Play teleport sound
		world.playSound(effectLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String[] args) {
		return getSubcommands(TP_Bed.class).get(0).onCommand(sender, command, args);
	}
}
