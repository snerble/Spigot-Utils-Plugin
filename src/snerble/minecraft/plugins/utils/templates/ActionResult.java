/**
 * 
 */
package snerble.minecraft.plugins.utils.templates;

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
	 */
	protected ActionResult(boolean succeeded, ActionError... errors) {
		this.succeeded = succeeded;
		this.errors = errors;
	}

	/**
	 * Indicator for a successfull action.
	 */
	public static final ActionResult SUCCESS = new ActionResult(true);
	
	/**
	 * Constructs a new {@link ActionResult} with the an array of errors.
	 * @param errors - The errors that occurred.
	 * @return The newly constructed {@link ActionResult}.
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
		
		// Return the SUCCESS singleton if all results were successfull
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
	 * Returns whether this {@link ActionResult} indicates success.
	 */
	public boolean succeeded() {
		return succeeded;
	}
	
	/**
	 * Returns a message describing this {@link ActionResult} with
	 * a custom error message prefix.
	 * @param errorPrefix - A string to prepend to the error message.
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
