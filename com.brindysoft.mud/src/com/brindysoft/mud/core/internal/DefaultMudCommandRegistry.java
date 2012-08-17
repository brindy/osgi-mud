package com.brindysoft.mud.core.internal;

import java.util.HashMap;
import java.util.Map;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component
public class DefaultMudCommandRegistry implements MudCommandRegistry {

	private Map<String, MudCommand> commands = new HashMap<String, MudCommand>();

	@Reference(optional = true, dynamic = true)
	public void addCommand(MudCommand command) {
		for (String verb : command.getVerbs()) {
			commands.put(verb, command);
		}
	}

	public void removeCommand(MudCommand command) {
		for (String verb : command.getVerbs()) {
			commands.remove(verb);
		}
	}

	@Override
	public MudCommand find(String string) {
		return null;
	}

}
