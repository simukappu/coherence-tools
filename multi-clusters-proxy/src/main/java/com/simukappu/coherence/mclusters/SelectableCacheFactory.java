package com.simukappu.coherence.mclusters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.simukappu.coherence.mclusters.processor.GetClusterNameProcessor;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.Service;
import com.tangosol.net.cache.TypeAssertion;

/**
 * Selectable cache factory class to operate multiple named caches in several
 * clusters.<br>
 * SelectableCacheFactory can be used as singleton.
 * 
 * @author Shota Yamazaki
 */
public class SelectableCacheFactory extends CacheFactory {

	/**
	 * Map contains class loaders for each configurable cache factory
	 */
	private static Map<String, ClassLoader> classLoaderMap = new HashMap<String, ClassLoader>();

	/**
	 * Map contains each configurable cache factories
	 */
	private static Map<String, ConfigurableCacheFactory> cacheFactoryMap = new HashMap<String, ConfigurableCacheFactory>();

	/**
	 * Private constructor<br>
	 * SelectableCacheFactory can be used as singleton
	 */
	private SelectableCacheFactory() {
	}

	/**
	 * Method to add new cache factory to SelectableCacheFactory
	 * 
	 * @param sConfigURI
	 *            Cache configuration file name
	 * @param classLoader
	 *            Class loader
	 * @param testCacheName
	 *            Test cache name to get cluster name by entry processor
	 *            (GetClusterNameProcessor)
	 * @return Cluster name
	 */
	public static synchronized String addCacheFactory(String sConfigURI,
			ClassLoader classLoader, String testCacheName) {
		try {
			// Initialize ConfigurableCacheFactory and get NamedCache for
			// testCacheName
			ConfigurableCacheFactory factory = CacheFactory
					.getCacheFactoryBuilder().getConfigurableCacheFactory(
							sConfigURI, classLoader);
			NamedCache<Object, Object> testCache = factory.ensureCache(
					testCacheName, classLoader);

			// Get cluster name by GetClusterNameProcessor
			String clusterName = testCache.invoke(null,
					new GetClusterNameProcessor());

			// Save ConfigurableCacheFactory and ClassLoader belonging to
			// cluster name
			if (clusterName != null) {
				if (!cacheFactoryMap.containsKey(clusterName)) {
					cacheFactoryMap.put(clusterName, factory);
					classLoaderMap.put(clusterName, classLoader);
					// Return cluster name
					return clusterName;
				} else {
					throw new IllegalArgumentException(
							"Specified configuration with test cache has connection to duplicated name cluster");
				}
			} else {
				throw new IllegalArgumentException(
						"Cloud not get cluster name through specified test cache");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"Exception occured in addCacheFactory@MultiClustersProxyCacheFactory: "
							+ e.getMessage());
		}
	}

	/**
	 * Method to remove specified cache factory from SelectableCacheFactory
	 * 
	 * @param clusterName
	 *            Cluster name to remove cache factory
	 */
	public static synchronized void removeCacheFactory(String clusterName) {
		// Check if ConfigurableCacheFactory is saved belonging specified
		// cluster name
		ConfigurableCacheFactory factory = cacheFactoryMap.get(clusterName);
		if (factory == null) {
			return;
		}

		// Dispose a cache factory and remove these references
		factory.dispose();
		cacheFactoryMap.remove(clusterName);
		classLoaderMap.remove(clusterName);
	}

	/**
	 * Method to get cluster name set
	 * 
	 * @return Set of cluster names with saved cache factories
	 */
	public static Set<String> getClusterNameSet() {
		return cacheFactoryMap.keySet();
	}

	/**
	 * Method to get {@literal NamedCache<Object, Object>} from
	 * SelectableCacheFactory
	 * 
	 * @param clusterName
	 *            Cluster name to get cache
	 * @param sCacheName
	 *            Cache name to get
	 * @return {@literal NamedCache<Object, Object>}
	 * @see com.tangosol.net.CacheFactory#getCache(com.tangosol.net.CacheFactory)
	 * @see com.tangosol.net.ConfigurableCacheFactory#ensureCache(com.tangosol.net.ConfigurableCacheFactory)
	 */
	public static NamedCache<Object, Object> getSelectableCache(
			String clusterName, String sCacheName) {
		ConfigurableCacheFactory factory = cacheFactoryMap.get(clusterName);
		if (factory == null) {
			return null;
		}
		ClassLoader classLoader = classLoaderMap.get(clusterName);
		if (classLoader == null) {
			throw new RuntimeException("Class loader is not set");
		}
		return factory.ensureCache(sCacheName, classLoader);
	}

	/**
	 * Method to get typed {@literal NamedCache<?, ?>} from
	 * SelectableCacheFactory
	 * 
	 * @param clusterName
	 *            Cluster name to get cache
	 * @param sCacheName
	 *            Cache name to get
	 * @param typeAssertion
	 *            Type assertion with typed named cache
	 * @return Typed {@literal NamedCache<?, ?>}
	 * @see com.tangosol.net.CacheFactory#getTypedCache(com.tangosol.net.CacheFactory)
	 * @see com.tangosol.net.ConfigurableCacheFactory#ensureTypedCache(com.tangosol.net.ConfigurableCacheFactory)
	 */
	public static <T> NamedCache<T, T> getSelectableTypedCache(
			String clusterName, String sCacheName,
			TypeAssertion<T, T> typeAssertion) {
		ConfigurableCacheFactory factory = cacheFactoryMap.get(clusterName);
		if (factory == null) {
			return null;
		}
		ClassLoader classLoader = classLoaderMap.get(clusterName);
		if (classLoader == null) {
			throw new RuntimeException("Class loader is not set");
		}
		return factory.ensureTypedCache(sCacheName, classLoader, typeAssertion);
	}

	/**
	 * Method to virtually call CacheFactory#getService through
	 * SelectableCacheFactory
	 * 
	 * @param clusterName
	 *            Cluster name to get service
	 * @param sServiceName
	 *            Service name to get
	 * @return com.tangosol.net.Service
	 * @see com.tangosol.net.CacheFactory#getService(com.tangosol.net.CacheFactory)
	 * @see com.tangosol.net.ConfigurableCacheFactory#ensureService(com.tangosol.net.ConfigurableCacheFactory)
	 */
	public static Service getSelectableService(String clusterName,
			String sServiceName) {
		ConfigurableCacheFactory factory = cacheFactoryMap.get(clusterName);
		if (factory == null) {
			return null;
		}
		return factory.ensureService(sServiceName);
	}

	/**
	 * Method to virtually call CacheFactory#destroyCache through
	 * SelectableCacheFactory
	 * 
	 * @param clusterName
	 *            Cluster name to destroy cache
	 * @param cache
	 *            Named cache to destroy
	 * @see com.tangosol.net.CacheFactory#destroyCache(com.tangosol.net.CacheFactory)
	 * @see com.tangosol.net.ConfigurableCacheFactory#destroyCache(com.tangosol.net.ConfigurableCacheFactory)
	 */
	public static void destroySelectableCache(String clusterName,
			NamedCache<?, ?> cache) {
		ConfigurableCacheFactory factory = cacheFactoryMap.get(clusterName);
		if (factory == null) {
			return;
		}
		factory.destroyCache(cache);
	}

	/**
	 * Method to virtually call CacheFactory#releaseCache through
	 * SelectableCacheFactory
	 * 
	 * @param clusterName
	 *            Cluster name to release cache
	 * @param cache
	 *            Named cache to release
	 * @see com.tangosol.net.CacheFactory#releaseCache(com.tangosol.net.CacheFactory)
	 * @see com.tangosol.net.ConfigurableCacheFactory#releaseCache(com.tangosol.net.ConfigurableCacheFactory)
	 */
	public static void releaseSelectableCache(String clusterName,
			NamedCache<?, ?> cache) {
		ConfigurableCacheFactory factory = cacheFactoryMap.get(clusterName);
		if (factory == null) {
			return;
		}
		factory.releaseCache(cache);
	}

}
