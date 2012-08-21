package com.brindysoft.mud.core.internal;

import java.io.IOException;
import java.util.Arrays;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.mpi.MudCommand;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudWorld;

@Component
public class DefaultMudEngine implements MudEngine {

	private Logger logger;
	private MudWorld world;
	private MudCommandRegistry commandRegistry;
	private MudCommand lookCommand;

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

	@Reference(target = "(type=look)")
	public void setLookCommand(MudCommand lookCommand) {
		this.lookCommand = lookCommand;
	}

	@Activate
	public void start() {
		logger.debug("%s#start()", getClass().getSimpleName());
	}

	@Deactivate
	public void stop() {
		logger.debug("%s#stop()", getClass().getSimpleName());
	}

	@Override
	public void run(MudUser user) throws IOException {
		logger.debug("%s#run() - IN", getClass().getSimpleName());

		world.addUser(user);
		lookCommand.invoke(null, user);

		// start reading commands
		while (true) {
			user.println("");
			user.print("{text:blue}%s{text} > ", user.getName());
			String commandLine = user.readLine();
			if (null == commandLine) {
				break;
			}

			commandLine = commandLine.trim();
			if (commandLine.length() == 0) {
				continue;
			}

			String[] args = commandLine.split("\\s+");
			logger.debug("%s#run() args : %s", getClass().getSimpleName(), Arrays.asList(args));

			MudCommand command = commandRegistry.find(args[0]);
			if (null == command || !command.invoke(args, user)) {
				user.println("I don't know how to '%s'", commandLine);
				user.println("");
			}
		}

		logger.debug("%s#run() - OUT", getClass().getSimpleName());
	}

}
