package com.brindysoft.db4o;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
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

	private Bundle bundle;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Activate
	public synchronized void start(BundleContext ctx) throws IOException {
		logger.debug("%s#start() - IN", getClass().getName());
		loaders = new HashMap<String, BundleLoader>();
		containers = new HashMap<String, ObjectContainer>();
		bundle = ctx.getBundle();
		logger.debug("%s#start() - OUT", getClass().getName());
	}

	public ObjectContainer getObjectContainer(Bundle bundle, String dbName, Properties properties) {
		logger.debug("%s#getObjectContainer(%s, %s, %s)", getClass().getSimpleName(), bundle, dbName, properties);

		try {
			URI uri = new URI(dbName);
			return openContainer(bundle, uri, properties);
		} catch (URISyntaxException e) {
			logger.error(e, "Invalid URI %s", dbName);
			throw new RuntimeException(e);
		}

	}

	public ObjectContainer getObjectContainer(Bundle bundle, String dbName) {
		return getObjectContainer(bundle, dbName, new Properties());
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
		if (1 == loader.bundles.size()) {
			logger.debug("%s#unregister() - closing and removing database %s", getClass().getName(), dbName);
			containers.remove(dbName).close();
			loaders.remove(dbName);
		}
		logger.debug("%s#unregister(%s, %s) - OUT", getClass().getSimpleName(), usingBundle, dbName);
	}

	@Deactivate
	public synchronized void stop() {
		logger.debug("%s#stop() - IN", getClass().getSimpleName());
		for (ObjectContainer container : containers.values()) {
			container.close();
		}
		containers.clear();
		containers = null;
		loaders.clear();
		loaders = null;
		logger.debug("%s#stop() - OUT", getClass().getSimpleName());
	}

	@Override
	public void onDiagnostic(Diagnostic diag) {
		// logger.debug("%s#onDiagnostic(%s)", getClass().getSimpleName(),
		// diag);
	}

	private ObjectContainer openContainer(Bundle bundle, URI uri, Properties properties) {
		logger.debug("%s#openContainer() - IN (%s, %s)", getClass().getSimpleName(), bundle, uri);

		String scheme = null == uri.getScheme() ? "mem" : uri.getScheme();
		String dbName = uri.getPath() == null ? uri.getHost() : uri.getHost() + uri.getPath();

		logger.debug("%s#openContainer(), scheme [%s], name [%s]", getClass().getSimpleName(), scheme, dbName);

		ObjectContainer container = containers.get(dbName);
		if (null == container) {

			if (scheme == null || "mem".equals(scheme)) {
				container = openInMemoryContainer(bundle, dbName, properties);
			} else if ("file".equals(scheme)) {
				container = openFileContainer(bundle, dbName, properties);
			} else {
				logger.error("%s is not a supported database scheme", scheme);
				throw new UnsupportedOperationException(scheme + " is not a supported database scheme");
			}

			containers.put(dbName, container);
		} else {
			BundleLoader loader = loaders.get(dbName);
			loader.bundles.add(bundle);
		}

		logger.debug("%s#openContainer() - OUT (%s, %s)", getClass().getSimpleName(), bundle, uri);
		return container;
	}

	private ObjectContainer openFileContainer(Bundle bundle, String dbName, Properties properties) {
		logger.debug("%s#openFileContainer(%s, %s) - IN", getClass().getSimpleName(), bundle, dbName);

		File f = new File(dbName);
		if (null != f.getParent()) {
			File parent = new File(f.getParent());
			if (!parent.exists() && !parent.mkdirs()) {
				logger.error("new File(%s).mkdirs() failed", f.getParent());
				throw new RuntimeException("new File(" + f.getParent() + ").mkdirs() failed");
			}
		}

		EmbeddedConfiguration config = createCommonConfiguration(bundle, dbName, properties);
		logger.debug("%s#openFileContainer(%s, %s) - OUT", getClass().getSimpleName(), bundle, dbName);
		return Db4oEmbedded.openFile(config, dbName);
	}

	private ObjectContainer openInMemoryContainer(Bundle bundle, String dbName, Properties properties) {
		EmbeddedConfiguration config = createCommonConfiguration(bundle, dbName, properties);
		config.file().storage(new MemoryStorage());
		return Db4oEmbedded.openFile(config, dbName);
	}

	private EmbeddedConfiguration createCommonConfiguration(Bundle bundle, String dbName, Properties properties) {
		BundleLoader loader = new BundleLoader();
		loader.bundles.add(this.bundle);
		loader.bundles.add(bundle);
		loaders.put(dbName, loader);

		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		JdkReflector reflector = new JdkReflector(loader);

		config.common().diagnostic().addListener(this);
		config.common().exceptionsOnNotStorable(true);
		config.common().reflectWith(reflector);
		config.common().updateDepth(Integer.MAX_VALUE);
		config.common().activationDepth(Integer.MAX_VALUE);
		return config;
	}

	class BundleLoader implements JdkLoader {

		public Set<Bundle> bundles = new HashSet<Bundle>();

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
						logger.debug("%s#loadClass(%s) from %s", getClass().getName(), className, bundle);
						return clazz;
					}
				} catch (ClassNotFoundException e) {
				}
			}

			logger.debug("%s#loadClass(%s) FAILED", getClass().getName(), className);
			return null;
		}

		@Override
		public Object deepClone(Object context) {
			logger.debug("%s#deepClone()", getClass().getName());
			BundleLoader loader = new BundleLoader();
			loader.bundles = new HashSet<Bundle>(bundles);
			return loader;
		}

	}

}
