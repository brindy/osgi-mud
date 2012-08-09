package com.brindysoft.mud.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.event.EventAdmin;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;

@Component(factory = SocketHandler.FACTORY)
public class DefaultSocketHandler implements SocketHandler, Runnable {

	private Socket socket;
	private Thread thread;
	private InputStream inputStream;
	private OutputStream outputStream;
	private ComponentFactory mudEngineFactory;
	private ComponentInstance mudEngineFactoryInstance;
	private EventAdmin eventAdmin;
	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference(target = "(component.factory=" + MudEngine.FACTORY + ")")
	public void setMudEngineFactory(ComponentFactory factory) {
		this.mudEngineFactory = factory;
	}

	@Reference
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	@Activate
	public void activate(Map<String, Object> properties) {
		logger.debug("DefaultSocketHandler(" + Thread.currentThread().getName() + ")#run() IN");
		try {
			socket = (Socket) properties.get(SocketHandler.SOCKET_PROPERTY);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			thread = new Thread(this);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
			fireExceptionEvent(e);
		}
		logger.debug("DefaultSocketHandler(" + Thread.currentThread().getName() + ")#run() IN");
	}

	@Override
	public void run() {
		logger.debug("DefaultSocketHandler(" + Thread.currentThread().getName() + ")#run() IN");
		MudEngine engine = createMudEngine();
		try {
			while (engine.run(inputStream, outputStream)) {
				logger.debug("SocketThread(" + Thread.currentThread().getName() + ").while()");
			}
		} catch (IOException e) {
			e.printStackTrace();
			fireExceptionEvent(e);
		}
		logger.debug("DefaultSocketHandler(" + Thread.currentThread().getName() + ")#run() OUT");
	}

	@Deactivate
	public void deactivate() throws Exception {
		logger.debug("DefaultSocketHandler(" + Thread.currentThread().getName() + ")#deactivate() IN");
		mudEngineFactoryInstance.dispose();

		Thread thread = this.thread;
		Socket socket = this.socket;

		this.socket = null;
		this.thread = null;

		thread.interrupt();
		socket.close();

		logger.debug("DefaultSocketHandler(" + Thread.currentThread().getName() + ")#deactivate() OUT");
	}

	private void fireExceptionEvent(IOException e) {
		logger.debug("firingExceptionEvent");
		eventAdmin.postEvent(new ExceptionEvent(this, e));
	}

	private MudEngine createMudEngine() {
		mudEngineFactoryInstance = mudEngineFactory.newInstance(null);
		return (MudEngine) mudEngineFactoryInstance.getInstance();
	}

}
