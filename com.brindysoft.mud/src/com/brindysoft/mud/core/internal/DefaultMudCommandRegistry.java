package com.brindysoft.mud.core.internal;

import java.util.HashMap;
import java.util.Map;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.mpi.MudCommand;
import com.brindysoft.mud.mpi.MudObject;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudWorld;

@Component
public class DefaultMudCommandRegistry implements MudCommandRegistry {

	private Map<String, MudCommand> commands = new HashMap<String, MudCommand>();
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

	@Reference(optional = true, multiple = true)
	public void addCommand(MudCommand command) {
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

	@Activate
	public void start() {
		logger.debug("%s#start()", getClass().getName());
	}

	@Override
	public MudCommand find(String string, MudUser user) {
		MudPlace place = world.findPlaceContaining(user);

		for (MudObject.HasCommandSynonyms object : place.getObjects(MudObject.HasCommandSynonyms.class)) {
			String synonym = object.getSynonymFor(string);
			MudCommand command = commands.get(synonym);
			if (null != command) {
				return command;
			}
		}

		for (MudObject.Commandable object : place.getObjects(MudObject.Commandable.class)) {
			MudCommand command = object.findCommand(string, user);
			if (null != command) {
				return command;
			}
		}

		return commands.get(string);
	}
}
