package com.brindysoft.mud.core.internal;

import java.lang.Thread.UncaughtExceptionHandler;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;

@Component(immediate = true)
public class Threader implements UncaughtExceptionHandler {

	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference(dynamic = true, multiple = true, optional = true, target = "(spawn=true)")
	public void addRunnable(Runnable runnable) {
		logger.debug("%s#addRunnable(%s)", getClass().getName(), runnable);
		Thread t = new Thread(runnable);
		t.setUncaughtExceptionHandler(this);
		t.start();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable t) {
		logger.error(t, "Uncaught exception in thread [%s]", thread.getName());
	}

}
