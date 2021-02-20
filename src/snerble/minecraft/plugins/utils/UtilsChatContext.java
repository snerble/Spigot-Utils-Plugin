package snerble.minecraft.plugins.utils;

import java.security.InvalidParameterException;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import snerble.minecraft.plugins.tools.ChatContext;

/**
 * {@link ChatContext} implementation for this plugin.
 * @author Conor
 *
 */
public class UtilsChatContext extends ChatContext {
	private static final String FORMAT = "%s[%s]" + ChatColor.RESET + " %s";
	
	private ChatColor color = ChatColor.RESET;
	private String name;
	
	public UtilsChatContext(String name) {
		setName(name);
	}
	
	/**
	 * @return The message tag color
	 */
	public ChatColor getColor() {
		return color;
	}
	/**
	 * @param color - The message tag color
	 */
	public void setColor(ChatColor color) {
		this.color = color;
	}

	/**
	 * @return The name used by this chat context
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name - The name for this chat context
	 */
	public void setName(String name) {
		if (StringUtils.isEmpty(name))
			throw new InvalidParameterException("'name' must be non-null and non-empty.");
		this.name = name;
	}

	@Override
	protected String getMessage(String content) {
		return String.format(FORMAT, 
				getColor(),
				getName(),
				content);
	}
}
