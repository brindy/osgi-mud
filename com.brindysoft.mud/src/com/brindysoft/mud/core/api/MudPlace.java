package com.brindysoft.mud.core.api;

import java.util.Set;

public interface MudPlace {

	/**
	 * Get the description for the given user.
	 * 
	 * @param user
	 *            the user
	 * @return the describe
	 */
	String getDescription(MudUser user);

	/**
	 * The user appears in this place without specifying a direction.
	 * 
	 * @param user
	 *            the user
	 */
	void addUser(MudUser user);

	/**
	 * Does this place contain this object?
	 * 
	 * @param object
	 *            the object
	 * @return true if the place contains the object
	 */
	boolean contains(Object object);

	/**
	 * Return a set containing the users at this location. Changes to this set
	 * will MUST NOT be reflected in the under lying data of the place.
	 * 
	 * @return
	 */
	Set<MudUser> getUsers();

}
