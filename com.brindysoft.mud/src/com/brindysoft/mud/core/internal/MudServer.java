package com.brindysoft.mud.core.internal;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.api.MudSocketHandler;

@Component(immediate = true, properties = { "event.topics=" + ExceptionEvent.TOPIC, "spawn=true" })
public class MudServer implements Runnable, EventHandler, ManagedService {

	private final Map<MudSocketHandler, ComponentInstance> socketHandlers = new HashMap<MudSocketHandler, ComponentInstance>();

	private Thread thread;
	private ServerSocket serverSocket;
	private int retry;
	private ComponentFactory socketHandlerFactory;

	private Logger logger;

	private Dictionary<String, String> properties;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference(target = "(component.factory=" + DefaultMudSocketHandler.FACTORY + ")")
	public void setSocketHandlerFactory(ComponentFactory socketHandlerFactory) {
		this.socketHandlerFactory = socketHandlerFactory;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		logger.debug("%s#updated() : %s", getClass().getSimpleName(), properties);
		this.properties = properties;
		stop();
		start();
	}

	@Activate
	public void start() {

		Dictionary<String, String> properties = getProperties();

		logger.debug("%s#start() - IN", getClass().getSimpleName());
		socketHandlers.clear();
		try {
			serverSocket = new ServerSocket(Integer.parseInt(properties.get("port")));
		} catch (NumberFormatException e) {
			logger.error(e, "%s#start() - configuration error", getClass().getSimpleName());
			throw e;
		} catch (IOException e) {
			logger.error(e, "%s#start() - MudServer NOT running, port already in use.", getClass().getSimpleName());
		}
		logger.debug("%s#start() - OUT", getClass().getSimpleName());
	}

	private Dictionary<String, String> getProperties() {
		if (null == properties) {
			properties = new Hashtable<String, String>();
			properties.put("port", "20128");
		}
		return properties;
	}

	@Override
	public void run() {
		logger.debug("%s(" + Thread.currentThread().getName() + ")#run() IN", getClass().getSimpleName());

		if (null == serverSocket) {
			logger.debug("%s(" + Thread.currentThread().getName() + ")#run() OUT - no server socket", getClass()
					.getSimpleName());
			return;
		}

		thread = Thread.currentThread();

		while (null != thread) {

			Socket socket = null;
			try {
				socket = serverSocket.accept();
				createSocketHandler(socket);
				retry = 0;
			} catch (SocketTimeoutException e) {
				// expected
			} catch (InterruptedIOException e) {
				// expected
			} catch (IOException e) {
				if (null != thread) {
					checkRetry(e);
				}
			} catch (Exception e) {
				logger.error(e, "Error creating socket");
				cleanup(socket);
			}

		}

		logger.debug("%s(" + Thread.currentThread().getName() + ")#run() OUT", getClass().getSimpleName());
	}

	@Deactivate
	public void stop() {
		logger.debug("MudServer#stop() - IN");

		Thread thread = this.thread;
		ServerSocket serverSocket = this.serverSocket;

		this.thread = null;
		this.serverSocket = null;

		thread.interrupt();
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.error(e, "MudServer#stop() - error closing socket");
		}

		logger.debug("MudServer#stop() - OUT");
	}

	@Override
	public void handleEvent(Event event) {
		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#handleEvent(" + event + ") IN");
		if (event instanceof ExceptionEvent) {
			handleExceptionEvent(event);
		}
		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#handleEvent(" + event + ") OUT");
	}

	private void cleanup(Socket socket) {
		if (null != socket) {
			try {
				socket.close();
			} catch (IOException e) {
				logger.error("Error cleaning up socket afte error", e);
			}
		}
	}

	private void handleExceptionEvent(Event event) {
		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#handleExceptionEvent() IN");
		ExceptionEvent exEvent = (ExceptionEvent) event;

		logger.info(exEvent.getThrowable(), "Connection threw exception");
		MudSocketHandler handler = exEvent.getHandler();

		socketHandlers.remove(handler).dispose();
		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#handleExceptionEvent() OUT");
	}

	private void createSocketHandler(Socket socket) {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(DefaultMudSocketHandler.SOCKET_PROPERTY, socket);
		ComponentInstance componentInstance = socketHandlerFactory.newInstance(properties);
		MudSocketHandler handler = (MudSocketHandler) componentInstance.getInstance();
		socketHandlers.put(handler, componentInstance);
	}

	private void checkRetry(IOException e) {
		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#checkRetry(" + e + ", " + retry + ") IN");
		retry++;
		if (retry > 3) {
			logger.debug("MudServer(" + Thread.currentThread().getName() + ")#checkRetry() throwing RuntimeException("
					+ e + ")");
			throw new RuntimeException("Unable to accept sockets", e);
		}
		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#checkRetry(" + retry + ") OUT");
	}

}
