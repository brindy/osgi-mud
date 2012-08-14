package com.brindysoft.mud.arkham;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

import com.brindysoft.mud.core.api.MudUser;
import com.brindysoft.mud.core.api.MudWorld;

@Component
public class ArkhamMudWorld implements MudWorld {

	@Override
	public void addUser(MudUser user) {
		user.println("Welcome to Miskatonic University.");
	}

	@Activate
	public void start() {
	}

	@Deactivate
	public void stop() {
	}

}
