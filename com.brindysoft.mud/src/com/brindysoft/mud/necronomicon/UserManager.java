package com.brindysoft.mud.necronomicon;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.db4o.api.Db4oManager;
import com.brindysoft.mud.core.api.MudUser;
import com.brindysoft.mud.core.api.MudUserManager;
import com.db4o.ObjectContainer;
import com.db4o.osgi.Db4oService;

@Component
public class UserManager implements MudUserManager {

	private ObjectContainer db;
	private Db4oService database;
	private Db4oManager manager;

	@Reference
	public void setDatabaseManager(Db4oManager manager) {
		this.manager = manager;
	}

	@Reference
	public void setDatabase(Db4oService database) {
		this.database = database;
	}

	@Activate
	public void start() {
		db = manager.getEmbeddedClient(database, "necronomicon");
	}

	@Deactivate
	public void stop() {
		db.close();
	}

	@Override
	public boolean checkPassword(MudUser user, String password) {
		UserCredentials creds = new UserCredentials();
		creds.setUser(user);
		creds.setPassword(password);
		return null != db.queryByExample(creds);
	}

	@Override
	public MudUser create(String username, String password) {
		User user = new User();
		user.setUsername(username);

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
		user.setUsername(username);
		return (User) db.queryByExample(user);
	}

}
