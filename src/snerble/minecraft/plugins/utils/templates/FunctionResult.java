package snerble.minecraft.plugins.utils.templates;

import java.security.InvalidParameterException;

/**
 * Describes the outcomes of a function that returs a value on success.
 * @author Conor
 */
public class FunctionResult extends ActionResult {
	private final Object value;
	
	/**
	 * Initializes a new instance of {@link FunctionResult} with a success value,
	 * function return value and list of errors.
	 * @param succeeded - Indicates a successful operation.
	 * @param value - The value of an operation.
	 * @param errors - An array of errors that occurred.
	 * @throws InvalidParameterException Thrown when succeeded is <code>true</code>
	 * but errors is not empty.
	 */
	protected FunctionResult(boolean succeeded, Object value, ActionError... errors) {
		super(succeeded, errors);
		this.value = value;
	}

	/**
	 * @param value - The value of a successful operation.
	 * @return A new {@link FunctionResult} with the given value that indicates
	 * success.
	 */
	public static FunctionResult success(Object value) {
		return new FunctionResult(true, value);
	}
	
	/**
	 * @param errors - A list of errors that occurred.
	 * @return A new unsuccessful {@link FunctionResult} with the given
	 * list of errors.
	 */
	public static FunctionResult failed(ActionError... errors) {
		return new FunctionResult(false, null, errors);
	}
	
	/**
	 * @return The value of this {@link FunctionResult}.
	 * @throws IllegalAccessError Thrown when this {@link FunctionResult} does not indicate success.
	 */
	public Object getValue() {
		if (!succeeded())
			throw new IllegalAccessError("Unsuccessful results have no value.");
		return value;
	}
}
