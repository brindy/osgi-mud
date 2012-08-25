package com.brindysoft.db4o;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.oodb.api.Database;
import com.brindysoft.oodb.api.DatabaseService;

@Component(provide = DatabaseService.class, servicefactory = true)
public class DatabaseServiceFactory implements DatabaseService {

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
		logger.debug("%s#start() - IN, for %s", getClass().getName(), ctx.getUsingBundle());
		this.ctx = ctx;
		logger.debug("%s#start() - OUT", getClass().getName());
	}

	public Database getDatabase(String dbName) {
		this.dbName = dbName;
		return new Db4oDatabase(serverManager.getObjectContainer(ctx.getUsingBundle(), dbName));
	}

	@Deactivate
	public void stop() {
		serverManager.unregister(ctx.getUsingBundle(), dbName);
	}

}
