package com.brindysoft.mud.necronomud.objects;

import com.brindysoft.mud.mpi.MudObject;
import com.brindysoft.mud.mpi.MudUser;

public class BusStop implements MudObject {

	@Override
	public String getListName() {
		return "A bus stop";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "bus stop", "busstop" };
	}

	@Override
	public void examine(MudUser user) {
		user.print("You see a tall wooden pole with a sign that says BUS STOP protruding from the top.");
	}

}
