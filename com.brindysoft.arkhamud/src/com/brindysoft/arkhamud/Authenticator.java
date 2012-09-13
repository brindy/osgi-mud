package com.brindysoft.arkhamud;

import java.util.HashSet;
import java.util.Set;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.api.MudSocketHandler;
import com.brindysoft.mud.mpi.MudAuthenticator;
import com.brindysoft.mud.mpi.MudUser;

@Component
public class Authenticator implements MudAuthenticator {

	private Logger logger;
	private Set<String> usernames = new HashSet<String>();

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public MudUser authenticate(MudSocketHandler socket) {
		logger.debug("%s#authenticate() - IN", getClass().getName());
		socket.println("Welcome to {text:red}Arkhamud{text} v1.");
		socket.println("");
		// TODO show high scores
		socket.println("The old ones are awakening.  Help defeat the monsters and close the portals before this world is lost!");
		socket.println("");

		// get a name
		String name = promptForName(socket);

		ArkhamCharacter character = new ArkhamCharacter(name);
		character.setSocketHandler(socket);
		
		new CharacterCreator(character).run();

		logger.debug("%s#authenticate() - OUT", getClass().getName());
		return character;
	}

	private String promptForName(MudSocketHandler socket) {
		socket.print("Please enter a name for your investigator: ");
		while (true) {
			String name = socket.readLine();
			synchronized (usernames) {
				if (!usernames.add(name)) {
					socket.println("Sorry, an investigator with that name is already in Arkham.");
					socket.println("");
				} else {
					return name;
				}
			}
		}
	}

}
