package com.brindysoft.mud.core.api;

public interface MudIo {

	String readLine();

	/**
	 * Same as {@link #print(String, Object...)} but with a carriage return.
	 */
	void println(String message, Object... params);

	/**
	 * Print a localized string filed with given parameters as per
	 * {@link String#format(String, Object...)}.
	 * 
	 * @param message
	 *            the message to display will be used as a lookup in to
	 *            localization files
	 * @param params
	 *            any parameters used by the message
	 */
	void print(String message, Object... params);

}
