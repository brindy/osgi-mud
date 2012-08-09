package com.brindysoft.mud.core;

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

@Component(immediate = true, provide = EventHandler.class, properties = "event.topics=com/brindysoft/mud/core/SocketHandler/*")
public class MudServer implements Runnable, EventHandler {

	private final Map<SocketHandler, ComponentInstance> socketHandlers = new HashMap<SocketHandler, ComponentInstance>();

	private Thread thread;
	private ServerSocket serverSocket;
	private int retry;
	private ComponentFactory socketHandlerFactory;

	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference(target = "(component.factory=" + SocketHandler.FACTORY + ")")
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

			try {
				Socket socket = serverSocket.accept();
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
			}

		}

		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#run() OUT");
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
		if (event instanceof SocketHandler.ExceptionEvent) {
			handleExceptionEvent(event);
		}
		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#handleEvent(" + event + ") OUT");
	}

	private void handleExceptionEvent(Event event) {
		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#handleExceptionEvent() IN");
		SocketHandler handler = ((SocketHandler.ExceptionEvent) event).getHandler();
		socketHandlers.remove(handler).dispose();
		logger.debug("MudServer(" + Thread.currentThread().getName() + ")#handleExceptionEvent() OUT");
	}

	private void createSocketHandler(Socket socket) {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(SocketHandler.SOCKET_PROPERTY, socket);
		ComponentInstance componentInstance = socketHandlerFactory.newInstance(properties);
		SocketHandler handler = (SocketHandler) componentInstance.getInstance();
		socketHandlers.put(handler, componentInstance);
	}

}
