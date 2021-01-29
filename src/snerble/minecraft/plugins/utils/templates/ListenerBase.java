package snerble.minecraft.plugins.utils.templates;

import java.util.logging.Logger;

import org.bukkit.event.Listener;

import snerble.minecraft.plugins.tools.MessageFormatter;
import snerble.minecraft.plugins.utils.UtilsPlugin;

public abstract class ListenerBase implements Listener {
	public final Logger log;
	public final MessageFormatter chat;
	
	public static String message;
	
	protected ListenerBase() {
		log = UtilsPlugin.Instance.getLogger();
		chat = UtilsPlugin.Instance.chat;
	}
}
