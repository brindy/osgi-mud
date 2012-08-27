package com.brindysoft.mud.core.internal.commands;

import java.util.HashMap;
import java.util.Map;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.mpi.MudCommand;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudUserManager;
import com.brindysoft.mud.mpi.MudWorld;

@Component
public class MoveCommand implements MudCommand {

	private static final Map<String, String> DIRECTIONS = new HashMap<String, String>();
	static {

		DIRECTIONS.put("north", "north");
		DIRECTIONS.put("n", "north");

		DIRECTIONS.put("east", "east");
		DIRECTIONS.put("e", "east");

		DIRECTIONS.put("south", "south");
		DIRECTIONS.put("s", "south");

		DIRECTIONS.put("west", "west");
		DIRECTIONS.put("w", "west");

	}

	private MudWorld world;
	private MudUserManager userManager;
	private MudCommand lookCommand;

	@Reference(target = "(type=look)")
	public void setLookCommand(MudCommand lookCommand) {
		this.lookCommand = lookCommand;
	}

	@Reference
	public void setWorld(MudWorld world) {
		this.world = world;
	}

	@Reference
	public void setUserManager(MudUserManager userManager) {
		this.userManager = userManager;
	}

	@Override
	public String[] getVerbs() {
		return new String[] { "go", "north", "n", "east", "e", "south", "s", "west", "w" };
	}

	@Override
	public boolean invoke(String[] args, MudUser user) {

		String direction = "";
		if ("go".equals(args[0]) && args.length < 2) {
			direction = args[1];
		} else {
			direction = args[0];
		}
		direction = DIRECTIONS.get(direction);

		MudPlace source = world.findPlaceContaining(user);
		if (source.getExits().contains(direction)) {
			source.userLeaves(user, direction);
			source.getExit(direction).userArrives(user, direction);
			world.save(source, source.getExit(direction));
			userManager.save(user);
			return lookCommand.invoke(new String[] { lookCommand.getVerbs()[0] }, user);
		}

		user.println("You can't go %s from here.", direction);

		return true;
	}

}
