package com.brindysoft.mud.core.api;

public interface MudPlace {

	/**
	 * Get the description for the given user.
	 * 
	 * @param user
	 *            the user
	 * @return the describe
	 */
	String getDescription(MudUser user);

}
