package com.brindysoft.mud.core.internal;

import java.io.IOException;
import java.util.Arrays;

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

	private MudCommandRegistry commandRegistry;

	@Reference
	public void setCommandRegistry(MudCommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

	@Reference
	public void setWorld(MudWorld world) {
		this.world = world;
	}

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Activate
	public void start() {
		logger.debug("DefaultMudEngine#start()");
	}

	@Deactivate
	public void stop() {
		logger.debug("DefaultMudEngine#stop()");
	}

	@Override
	public void run(MudUser user) throws IOException {
		logger.debug("DefaultMudEngine#run() - IN");

		// add the user to the world
		world.addUser(user);

		// start reading commands
		while (true) {
			user.print(user.getName() + " > ");
			String commandLine = user.readLine();
			if (null == commandLine) {
				break;
			}

			String[] args = commandLine.split("\\W+");
			logger.debug("DefaultMudEngine#run() args : " + Arrays.asList(args));

			MudCommand command = commandRegistry.find(args[0]);
			if (null == command || !command.invoke(args, user)) {
				user.println("I don't know how to '%s'", commandLine);
				user.println("");
			}
		}

		logger.debug("DefaultMudEngine#run() - OUT");
	}

}
