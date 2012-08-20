package com.brindysoft.mud.core.internal.commands;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.core.mpi.MudCommand;
import com.brindysoft.mud.core.mpi.MudPlace;
import com.brindysoft.mud.core.mpi.MudUser;
import com.brindysoft.mud.core.mpi.MudWorld;

@Component
public class SpeakCommand implements MudCommand {

	private MudWorld world;

	@Reference
	public void setWorld(MudWorld world) {
		this.world = world;
	}

	@Override
	public String[] getVerbs() {
		return new String[] { "say", "'" };
	}

	@Override
	public boolean invoke(String[] args, MudUser user) {
		if (args.length == 1) {
			return false;
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(args[i]);
		}
		String sentence = builder.toString();

		MudPlace place = world.findPlaceContaining(user);
		place.broadcastByUser(user, "%s %s '%s'", user.getName(), sentence.endsWith("?") ? "asks" : "says", sentence);
		user.println("You %s '%s'", sentence.endsWith("?") ? "ask" : "say", sentence);

		return true;
	}
}
