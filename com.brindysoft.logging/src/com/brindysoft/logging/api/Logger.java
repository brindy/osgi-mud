package com.brindysoft.logging.api;

public interface Logger {

	void debug(String message, Object... args);

	void debug(Throwable t, String message, Object... args);

	void warn(String message, Object... args);

	void warn(Throwable t, String message, Object... args);

	void error(String message, Object... args);

	void error(Throwable t, String message, Object... args);

	void info(String message, Object... args);

	void info(Throwable t, String message, Object... args);

}
