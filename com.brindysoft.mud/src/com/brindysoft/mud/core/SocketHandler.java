package com.brindysoft.mud.core;

import java.util.Map;

import org.osgi.service.event.Event;

public interface SocketHandler {

	String FACTORY = "com.brindysoft.mud.core.SocketHandler";

	String SOCKET_PROPERTY = "socket";

	public static class ExceptionEvent extends Event {

		public static final String TOPIC = FACTORY.replaceAll("\\.", "/") + "/EXCEPTION";

		private final Exception ex;
		private final SocketHandler handler;

		public ExceptionEvent(SocketHandler handler, Exception ex) {
			super(TOPIC, (Map<?, ?>) null);
			this.ex = ex;
			this.handler = handler;
		}

		public Exception getException() {
			return ex;
		}

		public SocketHandler getHandler() {
			return handler;
		}
	}

}
