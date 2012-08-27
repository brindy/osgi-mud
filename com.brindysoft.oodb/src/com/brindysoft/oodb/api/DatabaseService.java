package com.brindysoft.oodb.api;

import java.util.Properties;

public interface DatabaseService {

	/**
	 * Get a database with the specified name. Implementations may support more
	 * sophisticated naming extensions such as URLs (e.g. file://, mem://). All
	 * databases MUST support basic naming, though the peristence of that
	 * database is implementation specific.
	 * 
	 * @param name
	 *            the name of the database to get
	 * @param configuration
	 *            an appropriate configuration for this database
	 * @return the database
	 */
	Database getDatabase(String name, Properties configuration);

	Database getDatabase(String name);

}
