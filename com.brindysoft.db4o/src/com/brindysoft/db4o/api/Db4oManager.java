package com.brindysoft.db4o.api;

import com.db4o.ObjectContainer;
import com.db4o.osgi.Db4oService;

public interface Db4oManager {

	ObjectContainer getEmbeddedClient(Db4oService service, String dbName);

}
