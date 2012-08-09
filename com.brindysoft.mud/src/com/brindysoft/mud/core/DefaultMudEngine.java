package com.brindysoft.mud.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;

@Component(factory = MudEngine.FACTORY)
public class DefaultMudEngine implements MudEngine {

	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public boolean run(InputStream inputStream, OutputStream outputStream) throws IOException {
		String input = readLine(inputStream);
		logger.debug("#run().readLine() : " + input);
		return input != null;
	}

	/**
	 * Why not use buffered reader? BufferedReader will even buffer a network
	 * problem.
	 */
	private String readLine(InputStream inputStream) throws IOException {
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
}
