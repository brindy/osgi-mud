package com.brindysoft.mud.necronomicon;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.db4o.api.Db4oService;
import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.core.api.MudUser;
import com.brindysoft.mud.core.api.MudUserManager;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

@Component
public class UserManager implements MudUserManager {

	private ObjectContainer db;

	private Db4oService service;

	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference
	public void setDb4oService(Db4oService service) {
		this.service = service;
	}

	@Activate
	public void start() {
		logger.debug("UserManager#start() - IN");
		db = service.open("necronomicon");
		logger.debug("UserManager#start() - OUT");
	}

	@Deactivate
	public void stop() {
		logger.debug("UserManager#stop() - IN");
		db.close();
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

		return user;
	}

	@Override
	public MudUser find(String username) {
		User user = new User();
		user.setName(username);
		ObjectSet<User> results = db.queryByExample(user);
		if (!results.isEmpty()) {
			return results.next();
		}
		return null;
	}

}
