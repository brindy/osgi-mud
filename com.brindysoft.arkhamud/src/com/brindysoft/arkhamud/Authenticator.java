package com.brindysoft.arkhamud;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

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
	private ComponentFactory creationFactory;

	@Reference(target = "(component.factory=" + CharacterCreator.FACTORY_NAME + ")")
	public void setCharacterCreationFactory(ComponentFactory creationFactory) {
		this.creationFactory = creationFactory;
	}

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

		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(CharacterCreator.CHARACTER_PROPERTY, character);

		ComponentInstance component = creationFactory.newInstance(properties);
		try {
			CharacterCreator characterCreation = (CharacterCreator) component.getInstance();
			characterCreation.run();
		} finally {
			component.dispose();
		}

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
