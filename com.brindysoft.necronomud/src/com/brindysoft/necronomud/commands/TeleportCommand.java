package com.brindysoft.necronomud.commands;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.mpi.MudCommand;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudUserManager;
import com.brindysoft.mud.mpi.MudWorld;
import com.brindysoft.necronomud.World;

@Component
public class TeleportCommand implements MudCommand {

	private World world;

	private MudUserManager userManager;

	@Reference
	public void setUserManager(MudUserManager userManager) {
		this.userManager = userManager;
	}

	@Reference
	public void setWorld(MudWorld world) {
		this.world = (World) world;
	}

	@Override
	public String[] getVerbs() {
		return new String[] { "teleport", "goto" };
	}

	@Override
	public boolean invoke(String[] args, MudUser user) {

		user.println("{text:red}*** This command is for debugging only.  "
				+ "Future versions will not support this command. ***{text}");

		if (args.length == 1) {
			user.print("usage: %s <place tag>", args[0]);
			return true;
		}

		MudPlace sourcePlace = world.findPlaceContaining(user);

		sourcePlace.removeUser(user);
		sourcePlace.broadcast("{text:green}%s has teleported to %s{text}", user.getName(), args[1]);

		MudPlace destinationPlace = world.findPlaceByTag(args[1]);
		destinationPlace.addUser(user);
		destinationPlace.broadcastByUser(user, "{text:green}%s{text} appears!");
		world.save(sourcePlace, destinationPlace);
		userManager.save(user);
		return true;
	}

}
