package com.brindysoft.oodb.api;

public interface QueryPredicate<T> {

	public boolean match(T candiate);

}
