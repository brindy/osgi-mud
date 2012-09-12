package com.brindysoft.mud.mpi;

public interface MudWorld {

	/**
	 * Add the user to the world. If the user is already in this world, then
	 * they have just reconnected. Either way, appropriate messages should be
	 * displayed to other users in the same area.
	 * 
	 * @param user
	 *            the user that is being added to the world or has reconnected.
	 */
	void connectUser(MudUser user);

	/**
	 * Find the place where the user is currently at.
	 * 
	 * @param object
	 *            the object to find the place of
	 * @return the place the object resides
	 */
	MudPlace findPlaceContaining(MudUser user);

	/**
	 * Find a place by its tag.
	 * 
	 * @param tag
	 *            the tag
	 * @return the place
	 */
	MudPlace findPlaceByTag(String tag);

	/**
	 * Save the given object.
	 * 
	 * @param objects
	 *            the objects that changed
	 */
	void save(Object... objects);

	/**
	 * The user has disconnected from the world.
	 * 
	 * @param user
	 *            the user
	 */
	void disconnectUser(MudUser user);

}
