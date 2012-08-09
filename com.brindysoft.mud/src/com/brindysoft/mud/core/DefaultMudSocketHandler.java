package com.brindysoft.mud.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.event.EventAdmin;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;

@Component(factory = MudSocketHandler.FACTORY)
public class DefaultMudSocketHandler implements MudSocketHandler, Runnable {

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

	@Reference(target = "(component.factory=" + MudIo.FACTORY + ")")
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
			socket = (Socket) properties.get(MudSocketHandler.SOCKET_PROPERTY);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			thread = new Thread(this);
			thread.start();
		} catch (IOException e) {
			ExceptionEvent.postEvent(eventAdmin, e, this);
		}
		logger.debug("DefaultSocketHandler(" + Thread.currentThread().getName() + ")#run() IN");
	}

	@Override
	public void run() {
		logger.debug("DefaultSocketHandler(" + Thread.currentThread().getName() + ")#run() IN");
		MudIo io = createIo();
		try {
			io.run();
		} catch (IOException e) {
			ExceptionEvent.postEvent(eventAdmin, e, this);
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

	private MudIo createIo() {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(MudIo.INPUT_PROPERTY, inputStream);
		properties.put(MudIo.OUTPUT_PROPERTY, outputStream);
		properties.put(MudIo.SOCKET_HANDLER_PROPERTY, this);

		mudEngineFactoryInstance = mudEngineFactory.newInstance(properties);
		return (MudIo) mudEngineFactoryInstance.getInstance();
	}

}
