package com.brindysoft.db4o.api;

import com.db4o.ObjectContainer;

public interface Db4oService {

	ObjectContainer open(String dbName);

}
