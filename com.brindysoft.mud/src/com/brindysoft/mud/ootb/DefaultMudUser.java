package com.brindysoft.mud.ootb;

import java.io.IOException;
import java.util.Locale;

import com.brindysoft.mud.core.api.MudSocketHandler;
import com.brindysoft.mud.core.api.MudUser;

public class DefaultMudUser implements MudUser {

	private transient MudSocketHandler socket;

	@Override
	public void attachToSocket(MudSocketHandler socket) {
		this.socket = socket;
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public void print(String message, Object... params) throws IOException {
		socket.print(message, params);
	}

	@Override
	public void println(String message, Object... params) throws IOException {
		socket.println(message, params);
	}

	@Override
	public String readLine() throws IOException {
		return socket.readLine();
	}

}
