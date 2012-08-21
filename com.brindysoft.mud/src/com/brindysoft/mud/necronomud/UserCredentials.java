package com.brindysoft.mud.necronomud;

import com.brindysoft.mud.mpi.MudUser;

public class UserCredentials {

	private MudUser user;

	private String password;

	public MudUser getUser() {
		return user;
	}

	public void setUser(MudUser user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
