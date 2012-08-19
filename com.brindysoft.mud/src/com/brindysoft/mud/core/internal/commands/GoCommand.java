package com.brindysoft.mud.core.internal.commands;

import java.util.HashMap;
import java.util.Map;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.core.api.MudCommand;
import com.brindysoft.mud.core.api.MudDirectionCommand;
import com.brindysoft.mud.core.api.MudUser;

@Component
public class GoCommand implements MudCommand {

	private Map<String, MudDirectionCommand> commands = new HashMap<String, MudDirectionCommand>();

	@Reference(multiple = true, dynamic = true)
	public void addDirection(MudDirectionCommand directionCommand) {
		for (String command : directionCommand.getVerbs()) {
			commands.put(command, directionCommand);
		}
	}

	public void removeDirection(MudDirectionCommand directionCommand) {
		for (String command : directionCommand.getVerbs()) {
			commands.remove(command);
		}
	}

	@Override
	public String getDescription(MudUser user) {
		return "Go in the specified direction.";
	}

	@Override
	public String[] getVerbs() {
		return new String[] { "go" };
	}

	@Override
	public boolean invoke(String[] args, MudUser user) {
		if (args.length < 2) {
			return false;
		}

		MudCommand direction = commands.get(args[1]);
		if (null == direction) {
			return false;
		}

		return direction.invoke(new String[] { args[1] }, user);
	}

}
