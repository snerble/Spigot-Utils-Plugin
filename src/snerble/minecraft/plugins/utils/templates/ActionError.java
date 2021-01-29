package snerble.minecraft.plugins.utils.templates;

import java.security.InvalidParameterException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

/**
 * Describes an error that occurred during an action.
 * @author Conor
 *
 */
public class ActionError {
	private final String message;
	
	/**
	 * Initializes a new instance of {@link ActionError} with the
	 * given message and optional string format arguments.
	 * @param message - A message that describes the error.
	 * @param args - An array of string format arguments that are applied to the message.
	 */
	public ActionError(String message, Object... args) {
		if (StringUtils.isEmpty(message))
			throw new InvalidParameterException("message may not be null or empty");
		
		this.message = String.format(message, args);
	}
	
	/**
	 * Initializes a new instance of {@link ActionError} with the given
	 * exception.
	 * @param e
	 */
	public ActionError(Exception e) {
		StackTraceElement[] stack = e.getStackTrace();

		StackTraceElement top = Arrays.stream(stack)
				.filter(x -> x.getLineNumber() >= 0)
				.findFirst().orElse(null);

		String fileName = top == null ? "<unknown>" : top.getFileName();
		String lineNumber = top == null ? "?" : String.valueOf(top.getLineNumber());
		
		StringBuilder sb = new StringBuilder(e.getClass().getSimpleName());
		sb.append(" at ");
		sb.append(fileName);
		sb.append(':');
		sb.append(lineNumber);
		if (StringUtils.isNotEmpty(e.getMessage())) {
			sb.append(" - ");
			sb.append(e.getMessage());
		}
		
		message = sb.toString();
	}
	
	/**
	 * Returns the message of this {@link ActionError}.
	 */
	@Override
	public String toString() {
		return message;
	}
}
