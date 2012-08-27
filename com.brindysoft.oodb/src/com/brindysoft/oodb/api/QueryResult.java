package com.brindysoft.oodb.api;

import java.util.Iterator;

public interface QueryResult<T> extends Iterable<T> {

	boolean isEmpty();

	T next();

	T get(int index);

	@Override
	public Iterator<T> iterator();

}
