package snerble.minecraft.plugins.utils.actions;

import snerble.minecraft.plugins.utils.Tag;
import snerble.minecraft.plugins.utils.annotations.RegisterCommand;
import snerble.minecraft.plugins.utils.templates.ToggleCommand;
import snerble.minecraft.plugins.utils.templates.ActionError;
import snerble.minecraft.plugins.utils.templates.ActionResult;
import snerble.minecraft.plugins.utils.templates.CommandBase;
import snerble.minecraft.plugins.utils.templates.SetValueCommand;

@RegisterCommand
public class ManageTreeCutterCommand extends ToggleCommand {
	public static final int BLOCK_BREAK_LIMIT = 2000;
	
	public static final String NAME = "TreeCutter";
	
	private static final Tag[] BOOLEAN_SETTINGS = new Tag[] {
			Tag.TREECUTTER_BREAKAXE,
			Tag.TREECUTTER_BREAKLEAVES,
			Tag.TREECUTTER_REPLANT
	};
	
	public ManageTreeCutterCommand() {
		super(Tag.TREECUTTER_ENABLED, "tc");
		setDisplayName(NAME);
		
		// Intermediate command
		CommandBase setterCommand = addSubcommand(new CommandBase("set") { });
		
		// Register the boolean settings in bulk
		for (Tag tag : BOOLEAN_SETTINGS) {			
			setterCommand.addSubcommand(new SetValueCommand(tag, tag.name))
					.addArgument() // TODO Overload with addBooleanArgument
							.setOptions(() -> new String[] { "true", "false" })
							.setParser(Boolean::valueOf);
		}
		
		setterCommand.addSubcommand(new SetValueCommand(Tag.TREECUTTER_BLOCKLIMIT, Tag.TREECUTTER_BLOCKLIMIT.name))
				// TODO pass argument to addArgument to fix builder pattern
				.addArgument()
						.setTag("<amount>")
						.setParser(Integer::valueOf)
						.setValidator(o -> {
							Integer i = (Integer) o;
							if (i < 0 || i > BLOCK_BREAK_LIMIT)
								return ActionResult.failed(
										new ActionError("'amount' out of range (0 <= x <= %d)",
												BLOCK_BREAK_LIMIT));
							return ActionResult.SUCCESS;
						});
	}
}
