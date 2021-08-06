package snerble.minecraft.plugins.utils.actions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

import snerble.minecraft.plugins.utils.Database;
import snerble.minecraft.plugins.utils.Tag;
import snerble.minecraft.plugins.utils.templates.ListenerBase;

public class XPMultiplier extends ListenerBase {
	
	@EventHandler
	public void onExpChange(PlayerExpChangeEvent event) {
		float mult = Math.max(0, Database.Instance.getValue(Tag.XPMULTIPLIER_AMOUNT, 1f));
		
		event.setAmount((int)(event.getAmount() * mult));
	}
	
}
