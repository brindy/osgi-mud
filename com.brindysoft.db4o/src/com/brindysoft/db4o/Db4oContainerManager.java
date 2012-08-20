package com.brindysoft.db4o;

import java.io.IOException;
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
import com.db4o.diagnostic.Diagnostic;
import com.db4o.diagnostic.DiagnosticListener;
import com.db4o.io.MemoryStorage;
import com.db4o.reflect.jdk.ClassLoaderJdkLoader;
import com.db4o.reflect.jdk.JdkLoader;
import com.db4o.reflect.jdk.JdkReflector;

@Component(provide = Db4oContainerManager.class)
public class Db4oContainerManager implements DiagnosticListener {

	public final JdkLoader loader = new ClassLoaderJdkLoader(ObjectContainer.class.getClassLoader());

	private Map<String, BundleLoader> loaders;
	private Map<String, ObjectContainer> containers;

	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Activate
	public void start(BundleContext ctx) throws IOException {
		logger.debug("Db4oServerManager#start() - IN");
		loaders = new HashMap<String, BundleLoader>();
		containers = new HashMap<String, ObjectContainer>();
		logger.debug("Db4oServerManager#start() - OUT");
	}

	public ObjectContainer getObjectContainer(Bundle bundle, String dbName) {
		logger.debug("%s#getObjectContainer() - IN (%s, %s)", getClass().getSimpleName(), bundle, dbName);

		ObjectContainer container = containers.get(dbName);
		if (null == container) {
			container = openContainer(bundle, dbName);
			containers.put(dbName, container);
		} else {
			BundleLoader loader = loaders.get(dbName);
			loader.bundles.add(bundle);
		}
		logger.debug("%s#getObjectContainer() - OUT", getClass().getSimpleName());
		return container;
	}

	private ObjectContainer openContainer(Bundle bundle, String dbName) {
		ObjectContainer container;
		BundleLoader loader = new BundleLoader();
		loader.bundles.add(bundle);
		loaders.put(dbName, loader);

		EmbeddedConfiguration config = createConfiguration(loader);

		container = Db4oEmbedded.openFile(config, dbName + ".db4o");
		return container;
	}

	public synchronized void unregister(Bundle usingBundle, String dbName) {
		logger.debug("%s#unregister(%s, %s) - IN", getClass().getSimpleName(), usingBundle, dbName);

		BundleLoader loader = loaders.get(dbName);
		if (null == loader) {
			logger.debug("%s#unregister(%s, %s) - OUT, no BundleLoader found", getClass().getSimpleName(), usingBundle,
					dbName);
			return;
		}

		loader.bundles.remove(usingBundle);
		if (0 == loader.bundles.size()) {
			logger.debug("%s#unregister() - closing and removing database %s", getClass().getName(), dbName);
			containers.remove(dbName).close();
			loaders.remove(dbName);
		}
		logger.debug("%s#unregister(%s, %s) - OUT", getClass().getSimpleName(), usingBundle, dbName);
	}

	@Deactivate
	public void stop() {
		logger.debug("%s#stop() - IN", getClass().getSimpleName());
		for (ObjectContainer container : containers.values()) {
			container.close();
		}
		containers.clear();
		containers = null;
		logger.debug("%s#stop() - OUT", getClass().getSimpleName());
	}

	@Override
	public void onDiagnostic(Diagnostic diag) {
		logger.debug("%s#onDiagnostic(%s)", getClass().getSimpleName(), diag);
	}

	private EmbeddedConfiguration createConfiguration(BundleLoader loader) {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		JdkReflector reflector = new JdkReflector(loader);

		config.file().storage(new MemoryStorage());
		config.common().diagnostic().addListener(this);
		config.common().exceptionsOnNotStorable(true);
		config.common().reflectWith(reflector);
		return config;
	}

	class BundleLoader implements JdkLoader {

		public final Set<Bundle> bundles = new HashSet<Bundle>();

		public BundleLoader() {
		}

		@Override
		public Class<?> loadClass(String className) {
			Class<?> clazz = loader.loadClass(className);
			if (null != clazz) {
				return clazz;
			}

			for (Bundle bundle : bundles) {
				try {
					clazz = bundle.loadClass(className);
					if (clazz != null) {
						logger.debug("BundleLoader#loadClass(%s) from %s", className, bundle);
						return clazz;
					}
				} catch (ClassNotFoundException e) {
				}
			}

			logger.debug("BundleLoader#loadClass(%s) FAILED", className);
			return null;
		}

		@Override
		public Object deepClone(Object context) {
			// return new BundleLoader(bundles, loader);
			return null;
		}

	}

}
