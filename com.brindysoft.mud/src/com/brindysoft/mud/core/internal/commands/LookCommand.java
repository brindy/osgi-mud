package com.brindysoft.mud.core.internal.commands;

import java.util.Set;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.core.api.MudCommand;
import com.brindysoft.mud.core.api.MudPlace;
import com.brindysoft.mud.core.api.MudUser;
import com.brindysoft.mud.core.api.MudWorld;

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

		return true;
	}

	@Override
	public String getDescription(MudUser user) {
		return "Take a cursory look at your current location.";
	}

}
