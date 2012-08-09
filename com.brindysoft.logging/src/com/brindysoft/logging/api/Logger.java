package com.brindysoft.logging.api;

public interface Logger {

	void debug(String message);

	void debug(String message, Throwable t);

	void warn(String message);

	void warn(String message, Throwable t);

	void error(String message);

	void error(String message, Throwable t);

	void info(String message);

	void info(String message, Throwable t);

}
