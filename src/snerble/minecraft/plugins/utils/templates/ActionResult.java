/**
 * 
 */
package snerble.minecraft.plugins.utils.templates;

import java.security.InvalidParameterException;
import java.util.Arrays;

/**
 * Describes the outcome of an action.
 * @author Conor
 *
 */
public class ActionResult {
	private final boolean succeeded;
	private final ActionError[] errors;
	
	/**
	 * Initializes a new instance of {@link ActionResult} with
	 * a success value and a list of errors.
	 * @param succeeded - Indicates a successful action.
	 * @param errors - An array of errors that occurred.
	 * @throws InvalidParameterException Thrown when succeeded is <code>true</code>
	 * but errors is not empty.
	 */
	protected ActionResult(boolean succeeded, ActionError... errors) {
		if (succeeded && errors.length != 0)
			throw new InvalidParameterException("A successful result cannot have any errors.");
		
		this.succeeded = succeeded;
		this.errors = errors;
	}

	/**
	 * Indicator for a successful action.
	 */
	public static final ActionResult SUCCESS = new ActionResult(true);
	
	/**
	 * @param errors - The errors that occurred.
	 * @return A new unsuccessful instance of {@link ActionResult} with
	 * the array of errors.
	 */
	public static ActionResult failed(ActionError... errors) {
		return new ActionResult(false, errors);
	}
	
	/**
	 * Merges an array of {@link ActionResult}s together into one.
	 * @param results - An array of {@link ActionResult}s to merge.
	 * @return A new {@link ActionResult} that contains the data from the merged values.
	 */
	public static ActionResult merge(ActionResult... results) {
		// Filter failed results
		results = Arrays.stream(results)
				.filter(x -> !x.succeeded)
				.toArray(ActionResult[]::new);
		
		// Return the SUCCESS singleton if all results were successful
		if (results.length == 0)
			return SUCCESS;
		
		return new ActionResult(
				false,
				Arrays.stream(results) // Flatten the arrays of errors into one
						.map(x -> x.errors)
						.flatMap(Arrays::stream)
						.toArray(ActionError[]::new));
	}
	
	/**
	 * @return Whether this {@link ActionResult} indicates success.
	 */
	public boolean succeeded() {
		return succeeded;
	}
	
	/**
	 * @return A copy of this {@link ActionResult}s list of errors.
	 * @throws IllegalAccessError Thrown when this {@link ActionResult} does
	 * not indicate success.
	 */
	public ActionError[] getErrors() {
		if (!succeeded())
			throw new IllegalAccessError("Successful results have no errors.");
		return errors.clone();
	}
	
	/**
	 * @param errorPrefix - A string to prepend to the error message.
	 * @return A message describing this {@link ActionResult} with
	 * a custom message prefix, or "Succeeded" if this {@link ActionResult}
	 * indicates success.
	 */
	public String getMessage(String errorPrefix) {
		if (succeeded)
			return "Succeeded";
		
		StringBuilder sb = new StringBuilder(errorPrefix);
		
		if (errors.length == 0) {
			sb.append("Unknown error");
		}
		else {
			sb.append(String.join(" | ", Arrays.stream(errors)
					.map(x -> x.toString())
					.toArray(String[]::new)));
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return getMessage("Error : ");
	}
}
