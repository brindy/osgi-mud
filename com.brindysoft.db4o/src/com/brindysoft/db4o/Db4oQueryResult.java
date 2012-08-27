package com.brindysoft.db4o;

import java.util.Iterator;

import com.brindysoft.oodb.api.QueryResult;
import com.db4o.ObjectSet;

public class Db4oQueryResult<T> implements QueryResult<T> {

	private final ObjectSet<T> result;

	public Db4oQueryResult(ObjectSet<T> result) {
		this.result = result;
	}

	@Override
	public T get(int index) {
		return result.get(index);
	}

	@Override
	public boolean isEmpty() {
		return result.isEmpty();
	}

	@Override
	public T next() {
		return result.next();
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return result.hasNext();
			}

			@Override
			public T next() {
				return result.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
