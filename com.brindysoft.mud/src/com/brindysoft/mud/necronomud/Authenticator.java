package com.brindysoft.mud.necronomud;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.api.MudSocketHandler;
import com.brindysoft.mud.mpi.AbstractMudAuthenticator;
import com.brindysoft.mud.mpi.MudAuthenticator;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudUserManager;

@Component
public class Authenticator extends AbstractMudAuthenticator implements MudAuthenticator {

	@Override
	@Reference
	public void setUserManager(MudUserManager userManager) {
		this.userManager = userManager;
	}

	@Override
	public MudUser authenticate(MudSocketHandler socket) {
		socket.println("Welcome Necronomud v2.");
		socket.println("");
		socket.println("Beware investigator!  "
				+ "What you find beyond may leave a permanent mark on your psyche!  You have been warned.");
		return super.authenticate(socket);
	}

}
