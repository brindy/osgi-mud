package com.brindysoft.mud.necronomud.commands;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.mud.mpi.MudCommand;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.necronomud.User;

@Component
public class StatusCommand implements MudCommand {

	@Override
	public String[] getVerbs() {
		return new String[] { "status" };
	}

	@Override
	public boolean invoke(String[] args, MudUser mudUser) {
		User user = (User) mudUser;
		user.println("Your status:");
		user.println("");
		user.println("Insanity: %d (You are sane)", user.getInsanity());

		// TODO health
		// TODO if (user.hasMagic()) - show magic status

		user.println("");
		return true;
	}

}
