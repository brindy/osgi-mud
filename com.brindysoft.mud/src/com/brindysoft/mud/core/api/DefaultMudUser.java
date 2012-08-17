package com.brindysoft.mud.core.api;

import java.util.Locale;


public class DefaultMudUser implements MudUser {

	private transient MudSocketHandler socket;

	private Locale locale;

	private String name;

	@Override
	public void attachToSocket(MudSocketHandler socket) {
		this.socket = socket;
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

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
