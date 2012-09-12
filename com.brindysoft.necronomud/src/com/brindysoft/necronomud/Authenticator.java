package com.brindysoft.necronomud;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.api.MudSocketHandler;
import com.brindysoft.mud.mpi.AbstractMudAuthenticator;
import com.brindysoft.mud.mpi.MudAuthenticator;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudUserManager;

@Component
public class Authenticator extends AbstractMudAuthenticator implements MudAuthenticator {

	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	@Reference
	public void setUserManager(MudUserManager userManager) {
		this.userManager = userManager;
	}

	@Override
	public MudUser authenticate(MudSocketHandler socket) {
		logger.debug("%s#authenticate() - IN", getClass().getName());
		socket.println("Welcome to {text:red}Necronomud{text} v2.");
		socket.println("");
		socket.println("{text:bold}{text:red}Beware investigator!{text}  "
				+ "What you find beyond may leave a permanent mark on your psyche!  You have been warned.");
		logger.debug("%s#authenticate() - OUT", getClass().getName());
		return super.authenticate(socket);
	}

}
