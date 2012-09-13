package com.brindysoft.arkhamud;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudUserManager;

@Component
public class UserManager implements MudUserManager {

	@Override
	public boolean checkPassword(MudUser user, String password) {
		return false;
	}

	@Override
	public MudUser create(String username, String password) {
		return null;
	}

	@Override
	public MudUser find(String username) {
		return null;
	}

	@Override
	public void save(MudUser user) {
	}

}
