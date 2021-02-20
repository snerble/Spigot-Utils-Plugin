package snerble.minecraft.plugins.utils.actions;

import snerble.minecraft.plugins.utils.Tag;
import snerble.minecraft.plugins.utils.annotations.RegisterCommand;
import snerble.minecraft.plugins.utils.templates.ToggleCommand;

/**
 * Manages the autoTorch settings.
 * @author Conor
 *
 */
@RegisterCommand
public class AutoTorchCommand extends ToggleCommand {
	public static final String NAME = "AutoTorch";
	
	public AutoTorchCommand() {
		super(Tag.AUTOTORCH_ENABLED, Tag.AUTOTORCH_ENABLED.name);
		setDisplayName(NAME);
	}
}
