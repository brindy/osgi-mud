package com.brindysoft.mud.necronomicon;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.core.api.MudPlace;
import com.brindysoft.mud.core.api.MudUser;
import com.brindysoft.mud.core.api.MudWorld;

@Component
public class World implements MudWorld {

	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void addUser(MudUser user) {
	}

	@Override
	public MudPlace findPlace(Object object) {
		return null;
	}

	@Activate
	public void start() {
		logger.debug("ArkhamMudWorld#start()");
	}

	@Deactivate
	public void stop() {
		logger.debug("ArkhamMudWorld#stop()");
	}

}
