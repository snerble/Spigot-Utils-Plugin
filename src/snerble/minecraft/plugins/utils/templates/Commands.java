package snerble.minecraft.plugins.utils.templates;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import snerble.minecraft.plugins.tools.ChatContext;
import snerble.minecraft.plugins.tools.ChatContextProvider;
import snerble.minecraft.plugins.utils.annotations.RegisterCommand;

public final class Commands {
	static Logger log;
	static ChatContext chat;
	
	private Commands() {}
	
	/**
	 * AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
	 * @param log
	 * @param chat
	 */
	public static <T extends JavaPlugin & ChatContextProvider> void initialize(T plugin) {
		Logger log = plugin.getLogger();
		ChatContext chat = plugin.getChatContext();
		
		if (log == null || chat == null)
			throw new InvalidParameterException();
		
		Commands.log = log;
		Commands.chat = chat;
		
		Reflections reflections = new Reflections(plugin.getClass().getPackage().getName());
		new CommandRouter(plugin, getCommands(reflections));
	}
	
	/**
	 * Creates an instance of every {@link CommandBase} subclass with the given reflections
	 * object.
	 * @param reflections - A reflections object that represents a specific package.
	 */
	private static List<CommandBase> getCommands(Reflections reflections) {
		return reflections.getSubTypesOf(CommandBase.class).stream()
				.filter(x -> x.isAnnotationPresent(RegisterCommand.class))
				.map(x -> {
					try {
						return (CommandBase) x.getConstructor().newInstance();
					} catch (Exception e) {
						// Wrap exception
						throw new RuntimeException(e);
					}
				})
				.collect(Collectors.toList());
	}
}
