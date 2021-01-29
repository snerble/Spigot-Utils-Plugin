package snerble.minecraft.plugins.utils;

/**
 * Defines a list of tag names uses in conjunction with {@link PlayerDatabase}
 * to store certain values.
 * @author Conor
 *
 */
public enum Tag {
	// Plugin
	PLUGIN_COLOR("sP_Color", "color"),
	PLUGIN_VERSION("sP_Version"),
	
	// AutoTorch
	AUTOTORCH_ENABLED("bAT_Enabled"),
	
	// TreeCutter
	TREECUTTER_ENABLED("bTC_Enabled"),
	TREECUTTER_REPLANT("bTC_Replant", "replant"),
	TREECUTTER_BREAKAXE("bTC_BreakAxe", "breakAxe"),
	TREECUTTER_BREAKLEAVES("bTC_BreakLeaves", "breakLeaves"),
	TREECUTTER_BLOCKLIMIT("iTC_BlockLimit", "blockLimit"),
	TREECUTTER_PLAYEROWNED("TC_Placed");
	
	public final String value;
	public final String name;
	
	Tag(String value) {
		this(value, value);
	}
	
	Tag(String value, String name) {
		this.value = value;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
