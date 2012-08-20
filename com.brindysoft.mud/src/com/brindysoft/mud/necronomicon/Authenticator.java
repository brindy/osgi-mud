package com.brindysoft.mud.necronomicon;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.mpi.AbstractMudAuthenticator;
import com.brindysoft.mud.mpi.MudAuthenticator;
import com.brindysoft.mud.mpi.MudUserManager;

@Component
public class Authenticator extends AbstractMudAuthenticator implements MudAuthenticator {

	@Override
	@Reference
	public void setUserManager(MudUserManager userManager) {
		this.userManager = userManager;
	}

}
