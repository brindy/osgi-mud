package com.brindysoft.mud.core.spi;

import com.brindysoft.mud.core.api.MudAuthenticator;
import com.brindysoft.mud.core.api.MudSocketHandler;
import com.brindysoft.mud.core.api.MudUser;
import com.brindysoft.mud.core.api.MudUserManager;

public abstract class AbstractMudAuthenticator implements MudAuthenticator {

	protected MudUserManager userManager;

	public abstract void setUserManager(MudUserManager userManager);

	public AbstractMudAuthenticator() {
		super();
	}

	@Override
	public MudUser authenticate(MudSocketHandler socket) {
		while (true) {
			String username = promptForUsername(socket);
			MudUser user = userManager.find(username);
			if (null != user) {
				String password = promptForNewPassword(socket, username);
				if (userManager.checkPassword(user, password)) {
					return user;
				}
				socket.println("Sorry, that was the wrong password.");
			} else {
				String password = promptForPassword(socket, username);
				return userManager.create(username, password);
			}
		}
	}

	private String promptForNewPassword(MudSocketHandler socket, String name) {
		while (true) {
			socket.print("Welcome %s, please choose a password: ", name);
			String password = socket.readLine();
	
			if (null == password || password.length() > 5) {
				return password;
			}
	
			socket.println("Your password must be at least 6 characters long.");
		}
	}

	private String promptForPassword(MudSocketHandler socket, String name) {
		socket.print("Welcome %s, please enter your password: ", name);
		return socket.readLine();
	}

	private String promptForUsername(MudSocketHandler socket) {
		while (true) {
			socket.print("Enter your desired username: ");
			String name = socket.readLine().trim();
	
			if (null == name || name.matches("([a-zA-Z]){3,}")) {
				return name;
			}
	
			socket.println("Your username has to be at least 3 characters long.");
		}
	}

}