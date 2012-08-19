package com.brindysoft.db4o.api;

import com.db4o.ObjectContainer;

public interface Db4oService {

	ObjectContainer getDatabase(String dbName);

}
