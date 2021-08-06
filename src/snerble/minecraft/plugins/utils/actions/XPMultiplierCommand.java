package snerble.minecraft.plugins.utils.actions;

import snerble.minecraft.plugins.utils.Tag;
import snerble.minecraft.plugins.utils.annotations.RegisterCommand;
import snerble.minecraft.plugins.utils.templates.ActionError;
import snerble.minecraft.plugins.utils.templates.ActionResult;
import snerble.minecraft.plugins.utils.templates.SetValueCommand;

@RegisterCommand
public class XPMultiplierCommand extends SetValueCommand {

	public XPMultiplierCommand() {
		super(Tag.XPMULTIPLIER_AMOUNT, Tag.XPMULTIPLIER_AMOUNT.name);
		
		setDisplayName("Experience Multiplier");
		setGlobal(true);
		
		addArgument()
				.setTag("<amount>")
				.setParser(Float::valueOf)
				.setValidator(o -> {
					float f = (float) o;
					if (f < 0)
						return ActionResult.failed(new ActionError("'amount' outside of range (0 <= x)"));
					return ActionResult.SUCCESS;
				});
	}
}
