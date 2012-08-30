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

	/**
	 * Can the object be examined?
	 */
	static interface Examinable extends MudObject {

		/**
		 * Give the user more detail about the item.
		 * 
		 * @param user
		 *            the user examining the item
		 */
		void examine(MudUser user);

	}

	// TODO Killable?

	static interface HasCommandSynonyms extends MudObject {

		/**
		 * Return synonyms for the given string, if any.
		 * 
		 * @param string
		 *            the command string
		 * @return an array of synonyms or null
		 */
		String getSynonymFor(String string);

	}

	static interface Commandable extends MudObject {

		MudCommand findCommand(String string, MudUser user);

	}

}
