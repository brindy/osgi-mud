package com.brindysoft.mud.mpi;

public interface MudObject {

	/**
	 * A set of generic and specific names to match against user input, e.g.
	 * "sword", "sword of avalon", "avalon sword".
	 * 
	 * @return a list of strings to match against user input.
	 */
	String[] getAliases();

	/**
	 * Implement this interface to show your object in the list of objects in a
	 * location.
	 */
	static interface Listable extends MudObject {

		/**
		 * @return the name to display in a list of items, e.g. "A Balloon",
		 *         "The Sword of Avalon".
		 */
		String getListName();

	}

	static interface Examinable extends MudObject {

		/**
		 * Give the user more detail about the item.
		 * 
		 * @param user
		 *            the user examining the item
		 */
		void examine(MudUser user);

	}

}
