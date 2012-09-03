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
	 * A unique id for this place.
	 * 
	 * @return a unique id for this place. MUST NOT be null
	 */
	String getTag();

	/**
	 * The user appears in this place without specifying a direction.
	 * 
	 * @param user
	 *            the user
	 */
	void addUser(MudUser user);

	/**
	 * Does this place contain this user?
	 * 
	 * @param user
	 *            the user
	 * @return true if the place contains the user
	 */
	boolean containsUser(MudUser user);

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
	 * The user has disconnected.
	 * 
	 * @param user
	 *            the user
	 */
	void disconnectUser(MudUser user);

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
	 * Remove the object from this place.
	 * 
	 * @param object
	 *            the object
	 */
	void removeObject(MudObject object);

	/**
	 * Does this place contain this object?
	 * 
	 * @param object
	 *            the object
	 * @return true if the object is in this place
	 */
	boolean containsObject(MudObject object);

	/**
	 * Return set of objects at this location. Changes to this set will MUST NOT
	 * be reflected in the under lying data of the place.
	 * 
	 * @return the objects
	 */
	<T extends MudObject> Set<T> getObjects(Class<T> type);

	/**
	 * Add a listener to this place.
	 * 
	 * @param listener
	 *            a listener
	 */
	void addListener(Listener listener);

	/**
	 * Remove a listener from this place
	 * 
	 * @param listener
	 *            a listener
	 */
	void removeListener(Listener listener);

	public static interface Listener {
		void onUserArrives(MudPlace place, MudUser user, String heading, String from);

		void onUserAdded(MudPlace place, MudUser user);

		void onUserLeaves(MudPlace place, MudUser user, String toDirection);

		void onUserRemoved(MudPlace place, MudUser user);

		void onUserDisconnected(MudPlace place, MudUser user);
	}

	public static abstract class AbstractListener implements Listener {

		@Override
		public void onUserArrives(MudPlace place, MudUser user, String heading, String from) {
		}

		@Override
		public void onUserAdded(MudPlace place, MudUser user) {
		}

		@Override
		public void onUserLeaves(MudPlace place, MudUser user, String toDirection) {
		}

		@Override
		public void onUserRemoved(MudPlace place, MudUser user) {
		}

		@Override
		public void onUserDisconnected(MudPlace place, MudUser user) {
		}

	}

}
