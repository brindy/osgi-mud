package com.brindysoft.logging.api;

public interface Logger {

	void debug(String message);

	void debug(String message, Exception ex);

	void warn(String message);

	void warn(String message, Exception ex);

	void error(String message);

	void error(String message, Exception ex);

	void info(String message);

	void info(String message, Exception ex);

}
