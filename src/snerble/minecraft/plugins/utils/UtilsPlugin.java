package snerble.minecraft.plugins.utils;

import java.util.logging.Logger;

import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import snerble.minecraft.plugins.tools.MessageFormatter;
import snerble.minecraft.plugins.utils.annotations.RegisterCommand;
import snerble.minecraft.plugins.utils.templates.CommandBase;
import snerble.minecraft.plugins.utils.templates.ListenerBase;

public class UtilsPlugin extends JavaPlugin {
	/**
	 * Gets the main instance of this plugin.
	 */
	public static UtilsPlugin Instance;
	
	/**
	 * The message formatter for this plugin.
	 */
	public final MessageFormatter chat = new MessageFormatter(getName());
	
	/**
	 * The logger used by this plugin.
	 */
	private final Logger log;
	
	/**
	 * Initializes a new instance of {@link UtilsPlugin}.
	 */
	public UtilsPlugin() {
		Instance = this;
		log = getLogger();
	}
	
	/**
	 * Registers a new command executor using an instance of {@link CommandBase}.
	 * @param command - The command to register.
	 */
	private void registerCommand(CommandBase command)  {
		String name = command.getName();
		
		log.info(String.format("Registering command '%s' with instance of %s...",
				name,
				command.getClass().getSimpleName()));
		
		// Register the command executor to the command with it's name
		PluginCommand pluginCommand = getCommand(name);
		pluginCommand.setExecutor(command);
		
		// Set the command as a tab completer if it inherits from it
		pluginCommand.setTabCompleter((TabCompleter) command);
	}
	
	/**
	 * Registers a listener using an instance of {@link ListenerBase}.
	 * @param listener - The listener to register.
	 */
	private void registerListener(ListenerBase listener) {
		log.info(String.format("Registering listener %s...",
				listener.getClass().getSimpleName()));
		
		// Register the listener
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	@Override
	public void onEnable() {
		// Initialize database
		Database.Instance.load();

		// Initialize reflection module
		Reflections reflections = new Reflections("snerble.minecraft.plugins.utils.actions");
		
		// Register actions
		registerCommands(reflections);
		registerListeners(reflections);
	}
	
	/**
	 * Registers an instance of every {@link CommandBase} subclass in the given reflections
	 * object.
	 * @param reflections - A reflections object that represents a specific package.
	 */
	private void registerCommands(Reflections reflections) {
		for (Class<? extends CommandBase> type : reflections.getSubTypesOf(CommandBase.class)) {
			
			// Skip types that don't have the correct annotation
			if (!type.isAnnotationPresent(RegisterCommand.class))
				continue;
			
			CommandBase command;
			try {
				command = (CommandBase) type.getConstructor().newInstance();
			} catch (Exception e) {
				// Throw a runtime exception to interrupt loading the plugin
				throw new RuntimeException(e);
			}
			registerCommand(command);
		}
	}

	/**
	 * Registers an instance of every {@link ListenerBase} subclass in the given reflections
	 * object.
	 * @param reflections - A reflections object that represents a specific package.
	 */
	private void registerListeners(Reflections reflections) {
		for (Class<? extends ListenerBase> type : reflections.getSubTypesOf(ListenerBase.class)) {
			
			try {
				ListenerBase listener = (ListenerBase) type.getConstructor().newInstance();
				registerListener(listener);
			} catch (Exception e) {
				// Throw a runtime exception to interrupt loading the plugin
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public void onLoad() {
		
	}
	
	@Override
	public void onDisable() {
		Database.Instance.save();
	}
}
