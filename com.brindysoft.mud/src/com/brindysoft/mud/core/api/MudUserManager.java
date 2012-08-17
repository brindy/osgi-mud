package com.brindysoft.mud.core.api;

public interface MudUserManager {

	MudUser find(String username);

	boolean checkPassword(MudUser user, String password);

	MudUser create(String username, String password);

}
