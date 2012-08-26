package com.brindysoft.oodb.api;

public interface DatabaseService {

	/**
	 * Get a database with the specified name. Implementations may support more
	 * sophisticated naming extensions such as URLs (e.g. file://, mem://). All
	 * databases MUST support basic naming, though the peristence of that
	 * database is implementation specific.
	 * 
	 * @param string
	 *            the name of the database to get
	 * @return the database
	 */
	Database getDatabase(String string);

}
