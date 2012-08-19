package com.brindysoft.db4o;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.db4o.api.Db4oService;
import com.brindysoft.logging.api.Logger;
import com.db4o.ObjectContainer;

@Component(provide = Db4oService.class, servicefactory = true)
public class Db4oServiceFactory implements Db4oService {

	private Logger logger;
	private Db4oContainerManager serverManager;
	private ComponentContext ctx;
	private String dbName;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference
	public void setServerManager(Db4oContainerManager serverManager) {
		this.serverManager = serverManager;
	}

	@Activate
	public void start(ComponentContext ctx) {
		logger.debug("Db4oServiceFactory#start() - IN, for %s", ctx.getUsingBundle());
		this.ctx = ctx;
		logger.debug("Db4oServiceFactory#start() - OUT");
	}

	public ObjectContainer getDatabase(String dbName) {
		this.dbName = dbName;
		return serverManager.getObjectContainer(ctx.getUsingBundle(), dbName);
	}

	@Deactivate
	public void stop() {
		serverManager.unregister(ctx.getUsingBundle(), dbName);
	}

}
