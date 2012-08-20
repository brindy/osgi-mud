package com.brindysoft.example;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SampleWithHashSet {

	private Set<String> names;

	public void addName(String name) {
		if (null == names) {
			names = new HashSet<String>();
		}
		names.add(name);
	}

	public Set<String> getNames() {
		return null == names ? Collections.<String> emptySet() : Collections
				.unmodifiableSet(new HashSet<String>(names));
	}
	
}
