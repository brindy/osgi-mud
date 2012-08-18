package com.brindysoft.logging.sysout;

import java.util.Date;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.logging.api.Logger;

@Component
public class SystemOutLogger implements Logger {

	@Override
	public void debug(String message, Object... args) {
		log("debug", null, message, args);
	}

	@Override
	public void debug(Throwable t, String message, Object... args) {
		log("debug", t, message, args);
	}

	@Override
	public void warn(String message, Object... args) {
		log("warn ", null, message, args);
	}

	@Override
	public void warn(Throwable t, String message, Object... args) {
		log("warn ", t, message, args);
	}

	@Override
	public void error(String message, Object... args) {
		log("error", null, message, args);
	}

	@Override
	public void error(Throwable t, String message, Object... args) {
		log("error", t, message, args);
	}

	@Override
	public void info(String message, Object... args) {
		log("info ", null, message, args);
	}

	@Override
	public void info(Throwable t, String message, Object... args) {
		log("info ", t, message, args);
	}

	private void log(String type, Throwable ex, String message, Object... args) {
		String timeStamp = new Date().toString();
		String formattedMessage = String.format(message, args);
		String outputMessage = String.format("%s : %s : %s", timeStamp, type, formattedMessage);
		System.out.println(outputMessage);
		
		while (null != ex) {
			System.out.println(String.format("%s : %s : %s", timeStamp, type, ex.toString()));
			ex.printStackTrace(System.out);
			ex = ex.getCause();
		}
		
	}

}