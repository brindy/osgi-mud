package com.brindysoft.db4o;

import java.util.HashMap;
import java.util.Map;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.db4o.api.Db4oManager;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.osgi.Db4oService;

@Component
public class Db4oManagerImpl implements Db4oManager {

	private Map<String, ObjectServer> servers = new HashMap<String, ObjectServer>();

	/**
	 * Get a client for an embedded server. This method also creates the server
	 * if it doesn't already exist.
	 * 
	 * @param service
	 *            the service used MUST be injected in to your bundle so that
	 *            db4o can find your classes
	 * @param dbName
	 *            the name of the database, e.g. "myDb" this will be converted
	 *            to a filename internally
	 * @return a client for the given database
	 */
	public ObjectContainer getEmbeddedClient(Db4oService service, String dbName) {
		ObjectServer server = servers.get(dbName);
		if (null == server) {
			server = service.openServer(dbName, 0);
			servers.put(dbName, server);
		}

		// TODO track the clients and all them to be "closed"

		return server.openClient();
	}

}
