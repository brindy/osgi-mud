package com.brindysoft.mud.core.internal;

import static com.brindysoft.mud.core.internal.DefaultMudSocketHandler.TerminalCodes.DO;
import static com.brindysoft.mud.core.internal.DefaultMudSocketHandler.TerminalCodes.DONT;
import static com.brindysoft.mud.core.internal.DefaultMudSocketHandler.TerminalCodes.IAC;
import static com.brindysoft.mud.core.internal.DefaultMudSocketHandler.TerminalCodes.SB;
import static com.brindysoft.mud.core.internal.DefaultMudSocketHandler.TerminalCodes.SE;
import static com.brindysoft.mud.core.internal.DefaultMudSocketHandler.TerminalCodes.TERMINAL_TYPE;
import static com.brindysoft.mud.core.internal.DefaultMudSocketHandler.TerminalCodes.WILL;
import static com.brindysoft.mud.core.internal.DefaultMudSocketHandler.TerminalCodes.WONT;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.osgi.service.event.EventAdmin;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.api.MudSocketHandler;
import com.brindysoft.mud.mpi.MudAuthenticator;
import com.brindysoft.mud.mpi.MudUser;

@Component(factory = DefaultMudSocketHandler.FACTORY, provide = { MudSocketHandler.class, Runnable.class }, properties = { "spawn=true" })
public class DefaultMudSocketHandler implements MudSocketHandler, Runnable {

	public static final String FACTORY = "com.brindysoft.mud.core.SocketHandler";
	public static final String SOCKET_PROPERTY = "socket";

	private MudEngine engine;
	private MudAuthenticator authenticator;
	private EventAdmin eventAdmin;
	private Logger logger;

	private Socket socket;
	private Thread thread;
	private InputStream inputStream;
	private OutputStream outputStream;

	private OutputStrategy outputStrategy = new PlainOutputStrategy();

	@Reference
	public void setEngine(MudEngine engine) {
		this.engine = engine;
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
		logger.debug("%s(%s)#activate() IN", getClass().getSimpleName(), Thread.currentThread().getName());
		try {
			socket = (Socket) properties.get(SOCKET_PROPERTY);
			inputStream = new BufferedInputStream(socket.getInputStream());
			outputStream = socket.getOutputStream();
			// thread = new Thread(this);
			// thread.start();
		} catch (IOException e) {
			ExceptionEvent.postEvent(eventAdmin, e, this);
		}
		logger.debug("%s(%s)#activate() OUT", getClass().getSimpleName(), Thread.currentThread().getName());
	}

	@Override
	public void run() {
		logger.debug("%s(%s)#run() IN", getClass().getSimpleName(), Thread.currentThread().getName());
		thread = Thread.currentThread();

		MudUser user = null;
		try {
			beginTerminalNegotiation();
			println("");

			user = authenticator.authenticate(this);
			if (null == user) {
				throw new Exception("Unable to authenticate user");
			}

			user.setSocketHandler(this);
			engine.run(user);
		} catch (Exception e) {
			ExceptionEvent.postEvent(eventAdmin, e, this);
		} finally {
			if (null != user) {
				user.setSocketHandler(null);
			}
		}

		logger.debug("%s(%s)#run() OUT", getClass().getSimpleName(), Thread.currentThread().getName());
	}

	@Override
	public boolean isAlive() {
		return socket != null;
	}

	@Override
	public void close() {
		if (thread == null) {
			return;
		}

		Thread thread = this.thread;
		Socket socket = this.socket;

		this.socket = null;
		this.thread = null;

		thread.interrupt();
		try {
			socket.close();
		} catch (IOException e) {
			// ignore
		}
	}

	@Override
	public String readLine() {
		try {
			return doReadLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void println(String message, Object... params) {
		print(message, params);
		try {
			outputStream.write('\n');
			outputStream.write('\r');
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void print(String message, Object... params) {
		String output = String.format(message, params);
		try {
			outputStrategy.print(output);
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Deactivate
	public void deactivate() throws Exception {
		logger.debug("%s(%s)#deactivate() IN", getClass().getSimpleName(), Thread.currentThread().getName());

		if (null != thread) {
			close();
		}

		logger.debug("%s(%s)#deactivate() OUT", getClass().getSimpleName(), Thread.currentThread().getName());
	}

	private void beginTerminalNegotiation() throws IOException {
		logger.debug("%s - IAC DO TERMINAL_TYPE", getClass().getName());
		outputStream.write(new byte[] { (byte) IAC, (byte) DO, TERMINAL_TYPE });
		if (attemptReadIAC()) {
			requestTerminalType();
		}
	}

	private boolean attemptReadIAC() throws IOException {
		inputStream.mark(1);
		if (IAC == inputStream.read()) {
			logger.debug("%s#attemptReadIAC() - IAC response", getClass().getName());
			interpretAsCommand();
			return true;
		} else {
			logger.debug("%s#attemptReadIAC() - normal response", getClass().getName());
			inputStream.reset();
			return false;
		}
	}

	private void requestTerminalType() throws IOException {
		logger.debug("%s#requestTerminalType() : IN", getClass().getSimpleName());
		outputStream.write(new byte[] { (byte) IAC, (byte) SB, TERMINAL_TYPE, 1, (byte) IAC, (byte) SE });
		attemptReadIAC();
		logger.debug("%s#requestTerminalType() : OUT", getClass().getSimpleName());
	}

	private void interpretAsCommand() throws IOException {
		if (!inputStream.markSupported()) {
			throw new IOException("mark not supported");
		}

		int command = inputStream.read();
		logger.debug("%s#interpretAsCommand() : command = %d", getClass().getSimpleName(), command);

		switch (command) {

		case DO:
			logger.debug("%s#interpretAsCommand() : DO option = %d", getClass().getSimpleName(), inputStream.read());
			break;

		case DONT:
			logger.debug("%s#interpretAsCommand() : DONT option = %d", getClass().getSimpleName(), inputStream.read());
			break;

		case WILL:
			logger.debug("%s#interpretAsCommand() : WILL option = %d", getClass().getSimpleName(), inputStream.read());
			break;

		case WONT:
			logger.debug("%s#interpretAsCommand() : WONT option = %d", getClass().getSimpleName(), inputStream.read());
			break;

		case SB:
			readSubOption();
			break;

		default:
			logger.debug("%s#interpretAsCommand() : option = %d", getClass().getSimpleName(), inputStream.read());
			break;
		}

	}

	private String doReadLine() throws IOException {
		StringBuilder buffer = new StringBuilder();
		int charRead = '\0';
		while ('\n' != charRead) {
			charRead = inputStream.read();

			switch (charRead) {
			case -1:
				throw new IOException("Unable to read from input stream");

			case 0:
				break;

			case '\r':
				break;

			case '\n':
				break;

			case '{':
				buffer.append("{").append('\0');
				break;

			case 7:
				buffer.append(' ');
				break;

			case 8:
				buffer.deleteCharAt(buffer.length() - 1);
				break;

			case IAC:
				// possibly a command
				logger.debug("%s#doReadLine() Command character received", getClass().getSimpleName());
				interpretAsCommand();
				break;

			default:
				buffer.append((char) charRead);
				break;
			}
		}
		return buffer.toString();
	}

	private void readSubOption() throws IOException {
		logger.debug("%s#readSubOption() : IN", getClass().getSimpleName());

		int option = inputStream.read();
		logger.debug("%s#readSubOption() : option = %d", getClass().getSimpleName(), option);

		switch (option) {
		case TERMINAL_TYPE:
			readTerminalType();
			break;

		default:
			logger.debug("%s#readSubOption() : OUT", getClass().getSimpleName());
			int read = 0;
			while (SE != (read = inputStream.read())) {
				logger.debug("%s#readSubOption() : read other %d", getClass().getSimpleName(), read);
			}

			break;
		}

		logger.debug("%s#readSubOption() : OUT", getClass().getSimpleName());
	}

	private void readTerminalType() throws IOException {
		logger.debug("%s#readTerminalType() : IN", getClass().getSimpleName());

		int valueSupplied = inputStream.read();
		if (valueSupplied != 0) {
			logger.debug("%s#readTerminalType() : OUT, no value", getClass().getSimpleName());
			return;
		}

		int theByte = -1;
		StringBuffer buffer = new StringBuffer();
		while (SE != (theByte = inputStream.read())) {
			if (IAC == theByte) {
				continue;
			}
			buffer.append((char) theByte);
		}
		outputStrategy = new TerminalOutputStrategy();
		logger.debug("%s#readTerminalType() : OUT %s", getClass().getSimpleName(), buffer.toString());
	}

	interface OutputStrategy {
		void print(String message) throws IOException;
	}

	class PlainOutputStrategy implements OutputStrategy {

		@Override
		public synchronized void print(String message) throws IOException {
			message = message.replaceAll("\\{text:?.*?}", "");
			outputStream.write(message.getBytes());
		}

	}

	class TerminalOutputStrategy implements OutputStrategy {

		public TerminalOutputStrategy() throws IOException {
		}

		@Override
		public synchronized void print(String message) throws IOException {
			logger.debug("%s#print(%s)", getClass().getSimpleName(), message);

			message = message.replaceAll("\\{text:black}", new String(new byte[] { 0x1B, '[', '3', '0', 'm' }));
			message = message.replaceAll("\\{text:red}", new String(new byte[] { 0x1B, '[', '3', '1', 'm' }));
			message = message.replaceAll("\\{text:green}", new String(new byte[] { 0x1B, '[', '3', '2', 'm' }));
			message = message.replaceAll("\\{text:yellow}", new String(new byte[] { 0x1B, '[', '3', '3', 'm' }));
			message = message.replaceAll("\\{text:blue}", new String(new byte[] { 0x1B, '[', '3', '4', 'm' }));
			message = message.replaceAll("\\{text:magenta}", new String(new byte[] { 0x1B, '[', '3', '5', 'm' }));
			message = message.replaceAll("\\{text:cyan}", new String(new byte[] { 0x1B, '[', '3', '6', 'm' }));
			message = message.replaceAll("\\{text:white}", new String(new byte[] { 0x1B, '[', '3', '7', 'm' }));
			message = message.replaceAll("\\{text}", new String(new byte[] { 0x1B, '[', 0, 'm' }));

			message = message.replaceAll("\\{text:?.*?}", "");

			outputStream.write(message.getBytes());
		}
	}

	static interface TerminalCodes {

		int TERMINAL_TYPE = 24;

		int SE = 240;
		int SB = 250;

		int WILL = 251;
		int WONT = 252;
		int DO = 253;
		int DONT = 254;

		int IAC = 255;

	}

}
