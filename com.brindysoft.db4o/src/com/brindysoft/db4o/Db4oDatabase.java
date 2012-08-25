package com.brindysoft.db4o;

import com.brindysoft.oodb.api.Database;
import com.brindysoft.oodb.api.QueryPredicate;
import com.brindysoft.oodb.api.QueryResult;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class Db4oDatabase implements Database {

	private final ObjectContainer db;

	public Db4oDatabase(ObjectContainer db) {
		this.db = db;
	}

	@Override
	public void store(Object object) {
		db.store(object);
	}

	@Override
	public <T> QueryResult<T> query(Class<T> type) {
		return new Db4oQueryResult<T>(db.query(type));
	}

	@Override
	public <T> QueryResult<T> query(QueryPredicate<T> predicate) {

		Db4oPredicate<T> db4oPredicate = new Db4oPredicate<T>(predicate);

		return new Db4oQueryResult<T>(db.query(db4oPredicate));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> QueryResult<T> queryByExample(T example) {
		ObjectSet<Object> result = db.queryByExample(example);
		return new Db4oQueryResult<T>((ObjectSet<T>) result);
	}

	@Override
	public void commit() {
		db.commit();
	}

	@Override
	public void rollback() {
		db.rollback();
	}

}
