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
	
	// AutoCraft
	AUTOCRAFT_ENABLED("bAC_Enabled", "autoCraft"),
	AUTOCRAFT_RECIPES("lAC_Recipes", "recipes"),
	
	// AutoTorch
	AUTOTORCH_ENABLED("bAT_Enabled", "autoTorch"),
	
	// TreeCutter
	TREECUTTER_ENABLED("bTC_Enabled"),
	TREECUTTER_REPLANT("bTC_Replant", "replant"),
	TREECUTTER_BREAKAXE("bTC_BreakAxe", "breakAxe"),
	TREECUTTER_BREAKLEAVES("bTC_BreakLeaves", "breakLeaves"),
	TREECUTTER_BLOCKLIMIT("iTC_BlockLimit", "blockLimit"),
	TREECUTTER_PLAYEROWNED("TC_Placed"),
	
	// FindSlimeChunks
	SLIMECHUNKS(null, "slimeChunks"),
	
	// No-pickup
	NOPICKUP_ENABLE("bNP_Enabled", "nopickup"),
	NOPICKUP_MATERIALS("lNP_Materials", "items"),
	
	// XPMultiplier
	XPMULTIPLIER_AMOUNT("fXPM_Amount", "xpMult");
	
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
