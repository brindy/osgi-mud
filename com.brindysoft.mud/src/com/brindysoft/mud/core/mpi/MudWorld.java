package com.brindysoft.mud.core.mpi;

public interface MudWorld {

	/**
	 * Add the user to the world. If the user is already in this world, then
	 * they have just reconnected. Either way, appropriate messages should be
	 * displayed to other users in the same area.
	 * 
	 * @param user
	 *            the user that is being added to the world or has reconnected.
	 */
	void addUser(MudUser user);

	/**
	 * Find the place where the object resides.
	 * 
	 * @param object
	 *            the object to find the place of
	 * @return the place the object resides
	 */
	MudPlace findPlaceContaining(Object object);

	/**
	 * Move the user in the given direction.
	 * 
	 * @param user
	 *            the user
	 * @param direction
	 *            the direction (the exit name)
	 */
	boolean move(MudUser user, String direction);

}
