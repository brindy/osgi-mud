package com.brindysoft.mud.necronomicon.commands;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.mud.mpi.MudCommand;
import com.brindysoft.mud.mpi.MudUser;

@Component
public class InventoryCommand implements MudCommand {

	@Override
	public String[] getVerbs() {
		return new String[] { "inventory", "i" };
	}

	@Override
	public boolean invoke(String[] args, MudUser user) {
		
		user.println("");
		user.println("You have nothing.");
		user.println("");
		
		return true;
	}

}
