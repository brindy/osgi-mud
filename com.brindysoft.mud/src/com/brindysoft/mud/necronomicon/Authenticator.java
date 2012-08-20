package com.brindysoft.mud.necronomicon;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.core.mpi.AbstractMudAuthenticator;
import com.brindysoft.mud.core.mpi.MudAuthenticator;
import com.brindysoft.mud.core.mpi.MudUserManager;

@Component
public class Authenticator extends AbstractMudAuthenticator implements MudAuthenticator {

	@Override
	@Reference
	public void setUserManager(MudUserManager userManager) {
		this.userManager = userManager;
	}

}
