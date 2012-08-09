package com.brindysoft.mud.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.event.EventAdmin;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;

@Component(factory = MudIo.FACTORY)
public class DefaultMudIo implements MudIo, UncaughtExceptionHandler {

	// --- Injected ---
	private Logger logger;
	private MudAuthenticator authenticator;
	private MudLocalizer localizer;
	private EventAdmin eventAdmin;

	// -- Set during initialisation --
	private InputStream inputStream;
	private OutputStream outputStream;
	private MudSocketHandler handler;

	// -- Instance variables --
	private Locale locale = Locale.getDefault();

	@Reference
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference
	public void setAuthenticator(MudAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

	@Reference
	public void setLocalizer(MudLocalizer localizer) {
		this.localizer = localizer;
	}

	@Activate
	public void init(Map<String, Object> properties) {
		inputStream = (InputStream) properties.get(INPUT_PROPERTY);
		outputStream = (OutputStream) properties.get(OUTPUT_PROPERTY);
		handler = (MudSocketHandler) properties.get(SOCKET_HANDLER_PROPERTY);
	}

	@Override
	public void run() throws IOException {
		logger.debug("DefaultMuleIo#run() - IN");

		Thread.currentThread().setUncaughtExceptionHandler(this);

		MudUser user = authenticator.authenticate(this);
		if (null == (locale = user.getLocale())) {
			locale = Locale.getDefault();
		}
		user.attach(this);

		// TODO interpreter.begin(this)
		logger.debug("DefaultMuleIo#run() - OUT");
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
		outputStream.write(output.getBytes());
		outputStream.flush();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		logger.debug("DefaultMuleIo#uncaughtException() - IN");
		ExceptionEvent.postEvent(eventAdmin, throwable, handler);
		logger.debug("DefaultMuleIo#uncaughtException() - OUT");
	}

}
