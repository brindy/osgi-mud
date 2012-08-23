package com.brindysoft.mud.core.internal.commands;

import java.util.HashSet;
import java.util.Set;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.mpi.MudCommand;
import com.brindysoft.mud.mpi.MudObject;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudWorld;

@Component
public class ExamineCommand implements MudCommand {

	private MudWorld world;

	@Reference
	public void setWorld(MudWorld world) {
		this.world = world;
	}

	@Override
	public String[] getVerbs() {
		return new String[] { "examine", "scrutinize" };
	}

	@Override
	public boolean invoke(String[] args, MudUser user) {

		if (args.length == 1) {
			user.println("%s what?", args[0]);
			return true;
		}

		int start = 1;
		if (args[1].equals("the")) {
			start++;

			if (args.length == 2) {
				user.println("%s the what?", args[0]);
				return true;
			}

		}

		String inputAlias = toAlias(args, start);

		Set<MudObject> actualMatches = new HashSet<MudObject>();
		MudPlace place = world.findPlaceContaining(user);
		items: for (MudObject object : place.getObjects()) {
			for (String alias : object.getAliases()) {
				if (alias.equals(inputAlias)) {
					actualMatches.add(object);
					continue items;
				}
			}
		}

		if (actualMatches.size() == 0) {
			user.println("You don't see a %s.", inputAlias);
		} else if (actualMatches.size() > 1) {
			user.println("Which %s do you mean?", inputAlias);
		} else {
			actualMatches.iterator().next().examine(user);
		}

		return true;
	}

	private String toAlias(String[] args, int start) {
		StringBuilder builder = new StringBuilder();

		for (int i = start; i < args.length; i++) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(args[i]);
		}

		return builder.toString();
	}
}
