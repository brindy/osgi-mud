package com.brindysoft.mud.mpi;

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
	 * @return the users
	 */
	Set<MudUser> getUsers();

	/**
	 * Get the place in the given direction.
	 * 
	 * @param direction
	 *            the direction
	 * @return the place or null if there isn't one
	 */
	MudPlace placeInDirection(String direction);

	/**
	 * Remove user from this place.
	 * 
	 * @param user
	 *            the user
	 */
	void removeUser(MudUser user);

	/**
	 * Broadcast a message to all users in the place.
	 * 
	 * @param message
	 *            the message to broadcast
	 * @param args
	 *            the args to use for formatting
	 */
	void broadcast(String message, Object... args);

	/**
	 * Same {@link #broadcast(String, Object...)} but is not sent to the given
	 * user
	 */
	void broadcastByUser(MudUser user, String message, Object... args);

	/**
	 * Return a set of exits from this location. Changes to this set will MUST
	 * NOT be reflected in the under lying data of the place.
	 * 
	 * @return the exits
	 */
	Set<String> getExits();

	/**
	 * Get opposite exit for a given direction.
	 * 
	 * @param direction
	 *            the direction to find the opposite of
	 * @return the name of the opposite direction
	 */
	String getOppositeExit(String direction);

	/**
	 * The user leaves this place, heading in the given direction.
	 * 
	 * @param user
	 *            the user
	 * @param direction
	 *            the direction the user went
	 */
	void userLeaves(MudUser user, String direction);

	/**
	 * The user arrives in this place <em>heading</em> in the given direction.
	 * 
	 * @param user
	 *            the user
	 * 
	 * @param direction
	 *            the direction the user was <em>heading</em> in when they
	 *            arrived here
	 */
	void userArrives(MudUser user, String direction);

	/**
	 * Get the exist in the given direction.
	 * 
	 * @param direction
	 *            the direction
	 * @return the place in the given direction
	 */
	MudPlace getExit(String direction);

	/**
	 * Add the given to this place.
	 * 
	 * @param object
	 *            the object
	 */
	public void addObject(MudObject object);

	/**
	 * Return set of objects at this location. Changes to this set will MUST NOT
	 * be reflected in the under lying data of the place.
	 * 
	 * @return the objects
	 */
	Set<MudObject> getObjects();

}
