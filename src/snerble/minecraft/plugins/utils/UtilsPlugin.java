package snerble.minecraft.plugins.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import snerble.minecraft.plugins.tools.ChatContext;
import snerble.minecraft.plugins.tools.ChatContextProvider;
import snerble.minecraft.plugins.utils.templates.Commands;
import snerble.minecraft.plugins.utils.templates.ListenerBase;

public class UtilsPlugin extends JavaPlugin implements ChatContextProvider {
	/**
	 * Gets the main instance of this plugin.
	 */
	public static UtilsPlugin Instance;
	
	/**
	 * The message formatter for this plugin.
	 */
	public final ChatContext chat = new UtilsChatContext(getName());
	
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
		
		// Add custom recipes
		getCustomRecipes().forEach(Bukkit::addRecipe);

		// Initialize commands module
		Commands.initialize(this);
		
		// Initialize reflection module
		Reflections reflections = new Reflections(getClass().getPackage().getName());

		// Register listeners
		registerListeners(reflections);
	}

	/**
	 * Registers an instance of every {@link ListenerBase} subclass with the given reflections
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

	public List<Recipe> getCustomRecipes() {
		List<Recipe> recipes = new ArrayList<>();
		
		// Quartz block to 4 quartz
		recipes.add(new ShapelessRecipe(new NamespacedKey(this, "quartz"), new ItemStack(Material.QUARTZ, 4))
				.addIngredient(Material.QUARTZ_BLOCK));
		
		
		// Glowstone to 4 glowstone dust
		recipes.add(new ShapelessRecipe(new NamespacedKey(this, "glowstone_dust"), new ItemStack(Material.GLOWSTONE_DUST, 4))
				.addIngredient(Material.GLOWSTONE));
		
		// Clay to 4 clay ball
		recipes.add(new ShapelessRecipe(new NamespacedKey(this, "clay_ball"), new ItemStack(Material.CLAY_BALL, 4))
				.addIngredient(Material.CLAY));
		
		return recipes;
	}
	
	@Override
	public ChatContext getChatContext() {
		return chat;
	}
}
