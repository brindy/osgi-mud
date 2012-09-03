package com.brindysoft.oodb.api;

public interface Database {

	void store(Object object);

	void remove(Object object);
	
	<T> QueryResult<T> query(Class<T> type);

	<T> QueryResult<T> query(QueryPredicate<T> predicate);

	<T> QueryResult<T> queryByExample(T example);

	void commit();

	void rollback();
	
	void activate(Object o, int depth);

}
