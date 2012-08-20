package com.brindysoft.mud.mpi;

import com.brindysoft.mud.api.MudSocketHandler;

public abstract class AbstractMudUser implements MudUser {

	private transient MudSocketHandler socket;

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
