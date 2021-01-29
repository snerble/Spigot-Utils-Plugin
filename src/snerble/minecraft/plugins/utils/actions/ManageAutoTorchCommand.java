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
public class ManageAutoTorchCommand extends ToggleCommand {
	public static final String NAME = "AutoTorch";
	
	public ManageAutoTorchCommand() {
		super(Tag.AUTOTORCH_ENABLED, "autotorch");
		setDisplayName(NAME);
	}
}
