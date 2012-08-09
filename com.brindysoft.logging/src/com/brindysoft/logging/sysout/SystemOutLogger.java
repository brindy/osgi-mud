package com.brindysoft.logging.sysout;

import java.util.Date;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.logging.api.Logger;

@Component
public class SystemOutLogger implements Logger {

	@Override
	public void debug(String message) {
		log("debug", message, null);
	}

	@Override
	public void debug(String message, Exception ex) {
		log("debug", message, ex);
	}

	@Override
	public void error(String message) {
		log("error", message, null);
	}

	@Override
	public void error(String message, Exception ex) {
		log("error", message, ex);
	}

	@Override
	public void warn(String message) {
		log("warn", message, null);
	}

	@Override
	public void warn(String message, Exception ex) {
		log("warn", message, ex);
	}

	@Override
	public void info(String message) {
		log("info", message, null);
	}

	@Override
	public void info(String message, Exception ex) {
		log("info", message, ex);
	}

	private void log(String type, String message, Exception ex) {

		System.out.println(new Date() + " : " + type + " : " + message);
		if (null != ex) {
			System.out.println(new Date() + " : " + type + " : " + ex.toString());
			System.out.print(new Date() + " : ");
			ex.printStackTrace(System.out);
		}

	}

}