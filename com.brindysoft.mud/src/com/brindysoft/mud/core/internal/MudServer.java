package com.brindysoft.mud.core.internal;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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

@Component(immediate = true, provide = EventHandler.class, properties = "event.topics=" + ExceptionEvent.TOPIC)
public class MudServer implements Runnable, EventHandler {

	private final Map<MudSocketHandler, ComponentInstance> socketHandlers = new HashMap<MudSocketHandler, ComponentInstance>();

	private Thread thread;
	private ServerSocket serverSocket;
	private int retry;
	private ComponentFactory socketHandlerFactory;

	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference(target = "(component.factory=" + DefaultMudSocketHandler.FACTORY + ")")
	public void setSocketHandlerFactory(ComponentFactory socketHandlerFactory) {
		this.socketHandlerFactory = socketHandlerFactory;
	}

	@Activate
	public void start() throws Exception {
		logger.debug("MudServer#start() - IN");
		socketHandlers.clear();
		serverSocket = new ServerSocket(20128);
		thread = new Thread(this);
		thread.start();
		logger.debug("MudServer#start() - OUT");
	}

	@Override
	public void run() {
		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#run() IN");

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

		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#run() OUT");
	}

	@Deactivate
	public void stop() throws Exception {
		logger.debug("MudServer#stop() - IN");

		Thread thread = this.thread;
		ServerSocket serverSocket = this.serverSocket;

		this.thread = null;
		this.serverSocket = null;

		thread.interrupt();
		serverSocket.close();

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
