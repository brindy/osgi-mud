package com.brindysoft.mud.core.internal.commands;

import java.util.Set;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.mpi.MudCommand;
import com.brindysoft.mud.mpi.MudObject;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudWorld;

@Component(properties = "type=look")
public class LookCommand implements MudCommand {

	private MudWorld world;

	@Reference
	public void setWorld(MudWorld world) {
		this.world = world;
	}

	@Override
	public String[] getVerbs() {
		return new String[] { "look", "l" };
	}

	@Override
	public boolean invoke(String[] args, MudUser user) {
		MudPlace place = world.findPlaceContaining(user);

		user.println(place.getDescription(user));

		Set<String> exits = place.getExits();
		if (!exits.isEmpty()) {
			user.println("");
			user.print("Exits: %s", exits);
		}

		Set<MudUser> users = place.getUsers();
		if (!users.isEmpty()) {
			user.println("");
			for (MudUser otherUser : users) {
				if (otherUser == user || !otherUser.isAttached()) {
					continue;
				}
				user.println("{text:green}%s{text} is also here.", otherUser.getName());
			}
		}

		Set<MudObject> objects = place.getObjects();
		if (!objects.isEmpty()) {
			user.println("");
			user.println("You see:");
			for (MudObject object : objects) {
				user.println(object.getListName());
			}
		}

		return true;
	}

}
