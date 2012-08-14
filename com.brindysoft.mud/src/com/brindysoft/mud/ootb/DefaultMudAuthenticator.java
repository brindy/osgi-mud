package com.brindysoft.mud.ootb;

import java.io.IOException;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.mud.core.api.MudAuthenticator;
import com.brindysoft.mud.core.api.MudSocketHandler;
import com.brindysoft.mud.core.api.MudUser;

@Component
public class DefaultMudAuthenticator implements MudAuthenticator {

	@Override
	public MudUser authenticate(MudSocketHandler socket) throws IOException {
		socket.print("Enter your desired username: ");
		String name = socket.readLine();

		DefaultMudUser user = loadByName(name);
		if (null == user) {
			user = newUser(socket, name);
		} else {
			// check user's password
		}

		// update the user
		// save to the database

		return user;
	}

	private DefaultMudUser loadByName(String name) {
		return null;
	}

	private DefaultMudUser newUser(MudSocketHandler socket, String name) throws IOException {
		socket.print("You chose %s, is this OK? [y,N] ", name);
		String response = socket.readLine();
		return null;
	}

}
