package com.brindysoft.mud.core;

import java.io.IOException;

public interface MudIo {

	String FACTORY = "com.brindysoft.mud.core.MudEngine";

	String INPUT_PROPERTY = "input";

	String OUTPUT_PROPERTY = "output";

	String SOCKET_HANDLER_PROPERTY = "socketHandler";

	void run() throws IOException;

	String readLine() throws IOException;

	/**
	 * Same as {@link #print(String, Object...)} but with a carriage return.
	 */
	void println(String message, Object... params) throws IOException;

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
	void print(String message, Object... params) throws IOException;

}
