package com.brindysoft.mud.necronomud;

import com.brindysoft.mud.mpi.AbstractMudUser;

public class User extends AbstractMudUser {

	private String name;

	private int insanity;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getInsanity() {
		return insanity;
	}

	public void setInsanity(int insanity) {
		this.insanity = insanity;
	}

}
