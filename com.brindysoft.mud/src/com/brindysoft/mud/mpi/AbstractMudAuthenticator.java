package com.brindysoft.mud.mpi;

import com.brindysoft.mud.api.MudSocketHandler;

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
				String password = promptForPassword(socket, username);
				if (userManager.checkPassword(user, password)) {
					return user;
				}
				socket.println("Sorry, that was the wrong password.");
			} else {
				String password = promptForNewPassword(socket, username);
				socket.println("");

				String confirm = promptForPasswordConfirmation(socket, username);
				if (!password.equals(confirm)) {
					socket.println("Sorry, those passwords did not match.");
					continue;
				}

				return userManager.create(username, password);
			}
		}
	}

	protected String promptForPasswordConfirmation(MudSocketHandler socket, String name) {
		socket.println("");
		socket.print("Please confirm your password: ", name);
		return socket.readLine();
	}

	protected String promptForNewPassword(MudSocketHandler socket, String name) {
		while (true) {
			socket.println("");
			socket.print("Welcome {text:blue}%s{text}, please choose a password: ", name);
			String password = socket.readLine();

			if (null == password || password.length() > 5) {
				return password;
			}

			socket.println("Your password must be at least 6 characters long.");
		}
	}

	protected String promptForPassword(MudSocketHandler socket, String name) {
		socket.println("");
		socket.print("Welcome back {text:blue}%s{text}, please enter your password: ", name);
		return socket.readLine();
	}

	protected String promptForUsername(MudSocketHandler socket) {
		while (true) {
			socket.println("");
			socket.print("Enter your desired username: ");
			String name = socket.readLine().trim();

			if (null == name || name.matches("([a-zA-Z]){3,}")) {
				return name;
			}

			socket.println("Your username has to be at least 3 characters long and contain only alphabetic characters.");
		}
	}

}