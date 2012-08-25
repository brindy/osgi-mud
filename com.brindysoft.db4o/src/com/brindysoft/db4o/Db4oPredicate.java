package com.brindysoft.db4o;

import com.brindysoft.oodb.api.QueryPredicate;
import com.db4o.query.Predicate;

public class Db4oPredicate<T> extends Predicate<T> {

	private static final long serialVersionUID = 1L;

	private final QueryPredicate<T> predicate;

	public Db4oPredicate(QueryPredicate<T> predicate) {
		this.predicate = predicate;
	}

	public boolean match(T candiate) {
		return predicate.match(candiate);
	}

}
