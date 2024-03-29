package com.brindysoft.necronomud;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudUserManager;
import com.brindysoft.mud.utils.UsernameAndPasswordCredentials;
import com.brindysoft.oodb.api.Database;
import com.brindysoft.oodb.api.DatabaseService;
import com.brindysoft.oodb.api.QueryResult;

@Component
public class UserManager implements MudUserManager {

	public final static String USERS_DB = "file://databases/necro-users.db4o";
	private Logger logger;

	private Database db;

	private DatabaseService dbService;

	@Reference(target = "(type=db4o)")
	public void setDatabaseService(DatabaseService dbService) {
		this.dbService = dbService;
	}

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Activate
	public void start() {
		logger.debug("UserManager#start() - IN");
		db = dbService.getDatabase(USERS_DB);
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
		UsernameAndPasswordCredentials creds = new UsernameAndPasswordCredentials();
		creds.setUsername(user.getName());
		creds.setPassword(password);
		return !db.queryByExample(creds).isEmpty();
	}

	@Override
	public MudUser create(String username, String password) {
		User user = new User();
		user.setName(username);

		UsernameAndPasswordCredentials creds = new UsernameAndPasswordCredentials();
		creds.setUsername(username);
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
		if (results.size() > 1) {
			logger.error("%s#find(%s) Too many users %s", getClass().getName(), username, results.size());
			throw new RuntimeException("Too many users called " + username);
		} else if (results.size() == 0) {
			return null;
		}

		MudUser result = results.next();
		db.activate(result, Integer.MAX_VALUE);
		return result;
	}

	@Override
	public void save(MudUser user) {
		logger.debug("Saving user %s", user.getName());
		db.store(user);
		db.commit();
	}

}
