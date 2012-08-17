package com.brindysoft.mud.core.internal.commands;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.core.api.MudPlace;
import com.brindysoft.mud.core.api.MudUser;
import com.brindysoft.mud.core.api.MudWorld;
import com.brindysoft.mud.core.internal.MudCommand;

@Component
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
		MudPlace place = world.findPlace(user);
		place.printDescription(user);
		return true;
	}
}
