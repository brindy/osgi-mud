package com.brindysoft.mud.core;

import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

public class ExceptionEvent extends Event {

	public static final String TOPIC = "mud/EXCEPTION";

	private final Throwable throwable;

	private final MudSocketHandler handler;

	public ExceptionEvent(Throwable throwable, MudSocketHandler handler) {
		super(TOPIC, (Map<?, ?>) null);
		this.throwable = throwable;
		this.handler = handler;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public MudSocketHandler getHandler() {
		return handler;
	}

	public static void postEvent(EventAdmin admin, Throwable throwable, MudSocketHandler handler) {
		admin.postEvent(new ExceptionEvent(throwable, handler));
	}

}