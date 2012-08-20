package com.brindysoft.mud.core.internal.commands;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.mud.mpi.MudCommand;
import com.brindysoft.mud.mpi.MudUser;

@Component
public class ExitCommand implements MudCommand {

	@Override
	public String[] getVerbs() {
		return new String[] { "exit", "/q" };
	}

	@Override
	public boolean invoke(String[] args, MudUser user) {
		if ("/q".equals(args[0])) {
			throw new RuntimeException("User has quick exit");
		}

		user.println("Type 'EXIT' to confirm you wish to exit.");
		user.print("> ");

		String result = user.readLine();
		if ("EXIT".equals(result)) {
			throw new RuntimeException("User has exit");
		}

		return true;
	}

}
