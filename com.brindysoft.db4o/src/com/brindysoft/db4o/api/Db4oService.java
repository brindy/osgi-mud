package com.brindysoft.db4o.api;

import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.cs.config.ServerConfiguration;

public interface Db4oService {

	/**
	 * TODO configuration - expose a create configuration method. implementation
	 * should override any reflector set
	 * <p/>
	 * 
	 * TODO URL format:
	 * <ul>
	 * <li>type://path/name</li>
	 * </ul>
	 * 
	 * <p/>
	 * 
	 * Examples:
	 * <p/>
	 * 
	 * <ul>
	 * <li>file:///absolute/path/to/file</li>
	 * <li>file://relative/path/to/file</li>
	 * <li>mem://name</li>
	 * <li>server://??? - if port is 0 then create and return client??</li>
	 * </ul>
	 * 
	 * TODO Extra param {@link ConfigurationCallback}
	 * 
	 * @param dbName
	 * @return
	 */
	ObjectContainer getDatabase(String dbName);

	
	public static interface ConfigurationCallback {
		
		void configure(EmbeddedConfiguration config);
		
		void configure(ServerConfiguration config);
		
	}
	
}
