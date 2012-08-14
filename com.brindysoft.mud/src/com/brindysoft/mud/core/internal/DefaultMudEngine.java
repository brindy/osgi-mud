package com.brindysoft.mud.core.internal;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.core.api.MudEngine;
import com.brindysoft.mud.core.api.MudUser;

@Component
public class DefaultMudEngine implements MudEngine {

	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Activate
	public void activate() {
		logger.debug("DefaultMudEngine#activate()");
	}

	@Deactivate
	public void deactivate() {
		logger.debug("DefaultMudEngine#deactivate()");
	}

	@Override
	public void run(MudUser user) {

		// add the user to the world
		
		// start reading commands
		
	}

}
