package snerble.minecraft.plugins.tools;

import java.util.function.Function;

public final class ArrayHelper {
	private ArrayHelper() {}

	public static <T> T first(T[] values, Function<T, Boolean> predicate) {
		for (T value : values)
			if (predicate.apply(value))
				return value;
		
		return null;
	}	
}
