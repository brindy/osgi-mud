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
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.io.MemoryStorage;
import com.db4o.reflect.jdk.ClassLoaderJdkLoader;
import com.db4o.reflect.jdk.JdkLoader;
import com.db4o.reflect.jdk.JdkReflector;
import com.db4o.ta.DeactivatingRollbackStrategy;
import com.db4o.ta.TransparentPersistenceSupport;

@Component(provide = Db4oContainerManager.class)
public class Db4oContainerManager {

	private Map<String, BundleLoader> loaders;
	private Map<String, ObjectContainer> containers;

	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Activate
	public void start(BundleContext ctx) {
		logger.debug("Db4oServerManager#start() - IN");
		loaders = new HashMap<String, BundleLoader>();
		containers = new HashMap<String, ObjectContainer>();
		logger.debug("Db4oServerManager#start() - OUT");
	}

	public ObjectContainer getObjectContainer(Bundle bundle, String dbName) {
		logger.debug("%s#getObjectContainer() - IN (%s, %s)", getClass().getSimpleName(), bundle, dbName);

		ObjectContainer container = containers.get(dbName);
		if (null == container) {
			BundleLoader loader = new BundleLoader(new HashSet<Bundle>(Arrays.asList(bundle)),
					new ClassLoaderJdkLoader(ObjectContainer.class.getClassLoader()));
			loaders.put(dbName, loader);
			
			EmbeddedConfiguration config = createConfiguration(loader);
			
			container = Db4oEmbedded.openFile(config, dbName + ".db4o");
			containers.put(dbName, container);
		} else {
			BundleLoader loader = loaders.get(dbName);
			loader.bundles.add(bundle);
		}
		logger.debug("%s#getObjectContainer() - OUT", getClass().getSimpleName());
		return container;
	}

	private EmbeddedConfiguration createConfiguration(BundleLoader loader) {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		
		JdkReflector reflector = new JdkReflector(loader);
		config.file().storage(new MemoryStorage());
		config.common().reflectWith(reflector);
		config.common().add(new TransparentPersistenceSupport(new DeactivatingRollbackStrategy()));
		config.common().activationDepth(Integer.MAX_VALUE);
		config.common().updateDepth(Integer.MAX_VALUE);
		return config;
	}

	public synchronized void unregister(Bundle usingBundle, String dbName) {
		if (usingBundle.getState() == Bundle.STOPPING) {
			loaders.get(dbName).bundles.remove(usingBundle);
			if (loaders.get(dbName).bundles.isEmpty()) {
				loaders.remove(dbName);
				containers.remove(dbName).close();
			}
		}
	}

	@Deactivate
	public void stop() {
		logger.debug("Db4oServerManager#stop() - IN");
		for (ObjectContainer container : containers.values()) {
			container.close();
		}
		containers.clear();
		containers = null;
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
