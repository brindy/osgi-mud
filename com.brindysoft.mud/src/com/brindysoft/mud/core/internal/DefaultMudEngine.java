package com.brindysoft.mud.core.internal;

import java.io.IOException;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.core.api.MudEngine;
import com.brindysoft.mud.core.api.MudUser;
import com.brindysoft.mud.core.api.MudWorld;

@Component
public class DefaultMudEngine implements MudEngine {

	private Logger logger;
	private MudWorld world;

	@Reference
	public void setWorld(MudWorld world) {
		this.world = world;
	}

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Activate
	public void activate() {
		logger.debug("DefaultMudEngine#activate()");
	}

	@Deactivate
	public void deactivate() {
		logger.debug("DefaultMudEngine#deactivate()");
	}

	@Override
	public void run(MudUser user) throws IOException {

		// add the user to the world
		world.addUser(user);

		// start reading commands
		while (true) {
			user.print(user.getName() + " > ");
			String commandLine = user.readLine();
			
			
			
			user.println("I don't know how to '%s'", commandLine);
			user.println("");
		}

	}

}
