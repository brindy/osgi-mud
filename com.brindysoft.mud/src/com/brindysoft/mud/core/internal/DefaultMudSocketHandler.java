package com.brindysoft.mud.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.event.EventAdmin;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.core.api.ExceptionEvent;
import com.brindysoft.mud.core.api.MudAuthenticator;
import com.brindysoft.mud.core.api.MudEngine;
import com.brindysoft.mud.core.api.MudSocketHandler;
import com.brindysoft.mud.core.api.MudUser;

@Component(factory = MudSocketHandler.FACTORY)
public class DefaultMudSocketHandler implements MudSocketHandler, Runnable {

	private MudEngine engine;
	private MudAuthenticator authenticator;
	private MudLocalizer localizer;
	private EventAdmin eventAdmin;
	private Logger logger;

	private Socket socket;
	private Thread thread;
	private InputStream inputStream;
	private OutputStream outputStream;

	private Locale locale = Locale.getDefault();

	@Reference
	public void setEngine(MudEngine engine) {
		this.engine = engine;
	}

	@Reference
	public void setLocalizer(MudLocalizer localizer) {
		this.localizer = localizer;
	}

	@Reference
	public void setAuthenticator(MudAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
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
		try {
			MudUser user = authenticator.authenticate(this);
			if (null == user) {
				throw new Exception("Unable to authenticate user");
			}

			if (null == (locale = user.getLocale())) {
				locale = Locale.getDefault();
			}
			user.attachToSocket(this);
			engine.run(user);
		} catch (Exception e) {
			ExceptionEvent.postEvent(eventAdmin, e, this);
		}

		logger.debug("DefaultSocketHandler(" + Thread.currentThread().getName() + ")#run() OUT");
	}

	@Override
	public String readLine() throws IOException {
		StringBuilder buffer = new StringBuilder();
		int charRead = '\0';
		while ('\n' != charRead) {
			charRead = inputStream.read();

			switch (charRead) {
			case -1:
				return null;

			case '\r':
				break;

			case '\n':
				break;

			default:
				buffer.append((char) charRead);
				break;
			}
		}
		return buffer.toString();
	}

	@Override
	public void println(String message, Object... params) throws IOException {
		print(message, params);
		outputStream.write('\n');
		outputStream.write('\r');
		outputStream.flush();
	}

	@Override
	public void print(String message, Object... params) throws IOException {
		message = localizer.lookup(message, locale);
		String output = String.format(message, params);
		System.out.println(output);
		outputStream.write(output.getBytes());
		outputStream.flush();
	}

	@Deactivate
	public void deactivate() throws Exception {
		logger.debug("DefaultSocketHandler(" + Thread.currentThread().getName() + ")#deactivate() IN");

		Thread thread = this.thread;
		Socket socket = this.socket;

		this.socket = null;
		this.thread = null;

		thread.interrupt();
		socket.close();

		logger.debug("DefaultSocketHandler(" + Thread.currentThread().getName() + ")#deactivate() OUT");
	}

}
