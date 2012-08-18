package com.brindysoft.db4o;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.reflect.jdk.ClassLoaderJdkLoader;
import com.db4o.reflect.jdk.JdkLoader;
import com.db4o.reflect.jdk.JdkReflector;

@Component(provide = Db4oServerManager.class)
public class Db4oServerManager {

	private Map<String, ObjectServer> servers;
	private Map<String, BundleLoader> loaders;

	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Activate
	public void start(BundleContext ctx) {
		logger.debug("Db4oServerManager#start() - IN");
		servers = new HashMap<String, ObjectServer>();
		loaders = new HashMap<String, BundleLoader>();
		logger.debug("Db4oServerManager#start() - OUT");
	}

	public ObjectContainer getEmbeddedClient(Bundle bundle, String dbName) {
		logger.debug("Db4oServerManager#getEmbeddedClient() - IN (%s, %s)", bundle, dbName);

		ObjectServer server = servers.get(dbName);
		if (null == server) {
			ServerConfiguration config = Db4oClientServer.newServerConfiguration();
			BundleLoader loader = new BundleLoader(new HashSet<Bundle>(Arrays.asList(bundle)),
					new ClassLoaderJdkLoader(ObjectContainer.class.getClassLoader()));
			loaders.put(dbName, loader);
			JdkReflector reflector = new JdkReflector(loader);
			config.common().reflectWith(reflector);
			server = Db4oClientServer.openServer(config, dbName + ".db4o", 0);
			servers.put(dbName, server);
		} else {
			BundleLoader loader = loaders.get(dbName);
			loader.bundles.add(bundle);
		}
		logger.debug("Db4oServerManager#getEmbeddedClient() - OUT");
		return server.openClient();
	}

	public void unregister(Bundle usingBundle, String dbName) {
		loaders.get(dbName).bundles.remove(usingBundle);
	}

	@Deactivate
	public void stop() {
		logger.debug("Db4oServerManager#stop() - IN");
		for (ObjectServer server : servers.values()) {
			server.close();
		}
		servers.clear();
		logger.debug("Db4oServerManager#stop() - OUT");
	}

	class BundleLoader implements JdkLoader {

		public final Set<Bundle> bundles;

		public final JdkLoader loader;

		public BundleLoader(Set<Bundle> bundles, JdkLoader loader) {
			this.bundles = bundles;
			this.loader = loader;
		}

		@Override
		public Class<?> loadClass(String className) {
			logger.debug("BundleLoader#loadClass(%s)", className);

			Class<?> clazz = loader.loadClass(className);
			if (null != clazz) {
				return clazz;
			}

			for (Bundle bundle : bundles) {
				logger.debug("BundleLoader#loadClass(%s) from %s", className, bundle);
				try {
					return bundle.loadClass(className);
				} catch (ClassNotFoundException e) {
				}
			}

			logger.debug("BundleLoader#loadClass(%s) FAILED", className);
			return null;
		}

		@Override
		public Object deepClone(Object context) {
			return new BundleLoader(new HashSet<Bundle>(bundles), loader);
		}

	}

}
