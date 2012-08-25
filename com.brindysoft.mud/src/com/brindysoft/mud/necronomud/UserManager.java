package com.brindysoft.mud.necronomud;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudUserManager;
import com.brindysoft.oodb.api.Database;
import com.brindysoft.oodb.api.DatabaseService;
import com.brindysoft.oodb.api.QueryResult;

@Component
public class UserManager implements MudUserManager {

	private Logger logger;

	private Database db;

	private DatabaseService dbService;

	@Reference
	public void setDb4oService(DatabaseService dbService) {
		this.dbService = dbService;
	}

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Activate
	public void start() {
		logger.debug("UserManager#start() - IN");
		db = dbService.getDatabase("necronomicon");
		logger.debug("UserManager#start() - OUT");
	}

	@Deactivate
	public void stop() {
		logger.debug("UserManager#stop() - IN");
		db = null;
		logger.debug("UserManager#stop() - OUT");
	}

	@Override
	public boolean checkPassword(MudUser user, String password) {
		UserCredentials creds = new UserCredentials();
		creds.setUser(user);
		creds.setPassword(password);
		return !db.queryByExample(creds).isEmpty();
	}

	@Override
	public MudUser create(String username, String password) {
		User user = new User();
		user.setName(username);

		UserCredentials creds = new UserCredentials();
		creds.setUser(user);
		creds.setPassword(password);

		db.store(user);
		db.store(creds);
		db.commit();

		return user;
	}

	@Override
	public MudUser find(String username) {
		User user = new User();
		user.setName(username);
		QueryResult<User> results = db.queryByExample(user);
		if (!results.isEmpty()) {
			return results.next();
		}
		return null;
	}

}
