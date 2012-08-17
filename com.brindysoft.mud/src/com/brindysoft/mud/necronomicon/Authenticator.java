package com.brindysoft.mud.necronomicon;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.core.api.MudAuthenticator;
import com.brindysoft.mud.core.api.MudUserManager;
import com.brindysoft.mud.core.spi.AbstractMudAuthenticator;

@Component
public class Authenticator extends AbstractMudAuthenticator implements MudAuthenticator {

	@Override
	@Reference
	public void setUserManager(MudUserManager userManager) {
		this.userManager = userManager;
	}

}
