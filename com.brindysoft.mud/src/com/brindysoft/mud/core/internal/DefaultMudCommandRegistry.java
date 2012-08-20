package com.brindysoft.mud.core.internal;

import java.util.HashMap;
import java.util.Map;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.mpi.MudCommand;

@Component
public class DefaultMudCommandRegistry implements MudCommandRegistry {

	private Map<String, MudCommand> commands = new HashMap<String, MudCommand>();
	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference(optional = true, multiple = true)
	public void addCommand(MudCommand command) {
		if (null != logger) {
			logger.debug("DefaultMudCommandRegistry#addCommand(%s)", command);
		}
		for (String verb : command.getVerbs()) {
			if (null != commands.put(verb, command)) {
				throw new RuntimeException("Command already registered for " + verb);
			}
		}
	}

	public void removeCommand(MudCommand command) {
		for (String verb : command.getVerbs()) {
			commands.remove(verb);
		}
	}

	@Override
	public MudCommand find(String string) {
		return commands.get(string);
	}

}
