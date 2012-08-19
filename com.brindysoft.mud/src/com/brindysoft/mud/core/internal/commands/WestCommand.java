package com.brindysoft.mud.core.internal.commands;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.core.api.MudCommand;
import com.brindysoft.mud.core.api.MudDirectionCommand;
import com.brindysoft.mud.core.api.MudUser;
import com.brindysoft.mud.core.api.MudWorld;

@Component(provide = { MudCommand.class, MudDirectionCommand.class })
public class WestCommand implements MudDirectionCommand {

	public static final String DIRECTION = "west";

	private MudWorld world;

	@Reference
	public void setWorld(MudWorld world) {
		this.world = world;
	}

	@Override
	public String getDescription(MudUser user) {
		return "Moves you in an westerly direction.";
	}

	@Override
	public String[] getVerbs() {
		return new String[] { "west", "w" };
	}

	@Override
	public boolean invoke(String[] args, MudUser user) {
		return world.move(user, DIRECTION);
	}

	@Override
	public String getOpposite() {
		return EastCommand.DIRECTION;
	}

}
