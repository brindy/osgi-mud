package com.brindysoft.oodb.api;

public interface QueryResult<T> {

	boolean isEmpty();

	T next();

	T get(int index);

}
