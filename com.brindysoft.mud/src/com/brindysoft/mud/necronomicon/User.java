package com.brindysoft.mud.necronomicon;

import com.brindysoft.mud.core.mpi.AbstractMudUser;

public class User extends AbstractMudUser {

	private String name;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("User %s", name);
	}
}
