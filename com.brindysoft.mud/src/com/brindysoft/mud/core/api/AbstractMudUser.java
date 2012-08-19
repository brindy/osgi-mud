package com.brindysoft.mud.core.api;

import java.util.Locale;

public abstract class AbstractMudUser implements MudUser {

	private transient MudSocketHandler socket;

	private Locale locale;

	@Override
	public void attachToSocket(MudSocketHandler socket) {
		
		if (this.socket != null) {
			this.socket.close();
			this.socket = null;
		}
		
		this.socket = socket;
	}

	@Override
	public boolean isAttached() {
		return socket != null && socket.isAlive();
	}
	
	@Override
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public void print(String message, Object... params) {
		socket.print(message, params);
	}

	@Override
	public void println(String message, Object... params) {
		socket.println(message, params);
	}

	@Override
	public String readLine() {
		return socket.readLine();
	}

}
