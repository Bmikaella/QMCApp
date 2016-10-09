package icontract;

import java.text.MessageFormat;

/**
 * Class that checks if the specified conditions are fulfilled before the
 * execution of a method. It serves as a contract that states what must be
 * fulfilled for the code to be performed.
 * 
 * @author bmihaela
 *
 */
public class Preconditions {

	/**
	 * Requires the given expression to be true or it throws an
	 * IllegalArgumentException. In the message that will be printed variable
	 * values can be included. Example.
	 * "Number of elements must be a natural number not {0}.", height. {number}
	 * -> number in brackets will be replaced with the corresponding value that
	 * is on the corresponding place.
	 * 
	 * @param result expression that is the condition for the normal execution
	 *            of a method
	 * @param message message that will be written to explain what happened
	 * @param objects variable whose values will be printed on standard output
	 * @exception IllegalArgumentException if the given expression is not true
	 */
	public static void require(boolean result, String message, Object... objects) {
		if (!result) {
			throw new IllegalArgumentException(MessageFormat.format(message, objects));
		}
	}

}
