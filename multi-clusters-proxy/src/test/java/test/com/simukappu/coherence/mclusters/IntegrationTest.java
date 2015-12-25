package test.com.simukappu.coherence.mclusters;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.com.simukappu.coherence.mclusters.processor.SetClusterNameProcessor;

import com.simukappu.coherence.mclusters.SelectableCacheFactory;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.TypeAssertion;

/**
 * Integration test class for SelectableCacheFactory in multi clusters proxy
 * tools.<br>
 * Run extend proxy servers by
 * test.com.simukappu.coherence.mclusters.proxy.ProxyServerInClusterA and
 * test.com.simukappu.coherence.mclusters.proxy.ProxyServerInClusterB before
 * this test.
 * 
 * @author Shota Yamazaki
 */
public class IntegrationTest {

	/**
	 * Key of a entry used in tests
	 */
	public static final String CLUSTER_NAME_KEY = "ClusterName";
	/**
	 * Cluster name A
	 */
	public static final String CLUSTER_NAME_A = "MULTI_CLUSTER_A";
	/**
	 * Cluster name B
	 */
	public static final String CLUSTER_NAME_B = "MULTI_CLUSTER_B";
	/**
	 * Cache name in only Cluster A
	 */
	public static final String CACHE_IN_CLUSTER_A = "DataCacheInClusterA";
	/**
	 * Cache name in only Cluster B
	 */
	public static final String CACHE_IN_CLUSTER_B = "DataCacheInClusterB";
	/**
	 * Cache name in both Clusters A and B
	 */
	public static final String CACHE_IN_BOTH_CLUSTERS = "DataCacheInBothClusters";

	/**
	 * Initialization method.<br>
	 * This method runs following steps.<br>
	 * 1. Check if proxy servers are running.<br>
	 * 2. Configure SelectableCacheFactory.<br>
	 * 3. Get caches from each clusters.<br>
	 * 4. Put test data entries to each clusters.<br>
	 * You can run this test with only proxy servers or cluster including
	 * several cache servers by running CacheServer before this test.
	 */
	@BeforeClass
	public static void putEntries() {
		System.setProperty("tangosol.coherence.override", "client-override.xml");
		System.setProperty("tangosol.coherence.distributed.localstorage",
				"false");

		// Check if proxy servers are running
		NamedCache<String, String> cacheInClusterA = null;
		NamedCache<String, String> cacheInClusterB = null;
		try {
			cacheInClusterA = CacheFactory.getTypedCache(CACHE_IN_CLUSTER_A,
					TypeAssertion.withTypes(String.class, String.class));
		} catch (Exception e) {
			if (e.getMessage()
					.startsWith(
							"could not establish a connection to one of the following addresses:")) {
				fail("Run ProxyServerInClusterA first");
			}
		}
		try {
			cacheInClusterB = CacheFactory.getTypedCache(CACHE_IN_CLUSTER_B,
					TypeAssertion.withTypes(String.class, String.class));
		} catch (Exception e) {
			if (e.getMessage()
					.startsWith(
							"could not establish a connection to one of the following addresses:")) {
				fail("Run ProxyServerInClusterB first");
			}
		}

		// Configure SelectableCacheFactory
		String clusterNameA = SelectableCacheFactory.addCacheFactory(
				"clientA-cache-config.xml", ClassLoader.getSystemClassLoader(),
				CACHE_IN_BOTH_CLUSTERS);
		assertEquals(CLUSTER_NAME_A, clusterNameA);
		String clusterNameB = SelectableCacheFactory.addCacheFactory(
				"clientB-cache-config.xml", ClassLoader.getSystemClassLoader(),
				CACHE_IN_BOTH_CLUSTERS);
		assertEquals(CLUSTER_NAME_B, clusterNameB);

		// Get caches from each clusters
		NamedCache<String, String> multiCacheInClusterA = SelectableCacheFactory
				.getSelectableTypedCache(clusterNameA, CACHE_IN_BOTH_CLUSTERS,
						TypeAssertion.withTypes(String.class, String.class));
		NamedCache<String, String> multiCacheInClusterB = SelectableCacheFactory
				.getSelectableTypedCache(clusterNameB, CACHE_IN_BOTH_CLUSTERS,
						TypeAssertion.withTypes(String.class, String.class));

		// Put test data entries to each clusters
		String value;
		System.out.println("Put entries");
		value = cacheInClusterA.invoke(CLUSTER_NAME_KEY,
				new SetClusterNameProcessor());
		System.out.println(" ClusterName=" + CLUSTER_NAME_A + ", CacheName="
				+ cacheInClusterA.getCacheName() + ", key=" + CLUSTER_NAME_KEY
				+ ", value=" + value);
		value = cacheInClusterB.invoke(CLUSTER_NAME_KEY,
				new SetClusterNameProcessor());
		System.out.println(" ClusterName=" + CLUSTER_NAME_B + ", CacheName="
				+ cacheInClusterB.getCacheName() + ", key=" + CLUSTER_NAME_KEY
				+ ", value=" + value);
		value = multiCacheInClusterA.invoke(CLUSTER_NAME_KEY,
				new SetClusterNameProcessor());
		System.out.println(" ClusterName=" + CLUSTER_NAME_A + ", CacheName="
				+ multiCacheInClusterA.getCacheName() + ", key="
				+ CLUSTER_NAME_KEY + ", value=" + value);
		value = multiCacheInClusterB.invoke(CLUSTER_NAME_KEY,
				new SetClusterNameProcessor());
		System.out.println(" ClusterName=" + CLUSTER_NAME_B + ", CacheName="
				+ multiCacheInClusterB.getCacheName() + ", key="
				+ CLUSTER_NAME_KEY + ", value=" + value);
		System.out.println("Done\n");
	}

	/**
	 * Integration test method for SelectableCacheFactory#getCache.<br>
	 * This method tests to get named cache in only one cluster.
	 */
	@Test
	public void getCacheTest() {
		// Get caches from each clusters
		@SuppressWarnings("rawtypes")
		NamedCache cacheClusterA = SelectableCacheFactory
				.getCache(CACHE_IN_CLUSTER_A);
		@SuppressWarnings("rawtypes")
		NamedCache cacheClusterB = SelectableCacheFactory
				.getCache(CACHE_IN_CLUSTER_B);

		String value;
		System.out.println("Get entries");
		value = (String) cacheClusterA.get(CLUSTER_NAME_KEY);
		assertEquals(CLUSTER_NAME_A, value);
		System.out.println(" ClusterName=" + CLUSTER_NAME_A + ", CacheName="
				+ cacheClusterA.getCacheName() + ", key=" + CLUSTER_NAME_KEY
				+ ", value=" + value);
		value = (String) cacheClusterB.get(CLUSTER_NAME_KEY);
		assertEquals(CLUSTER_NAME_B, value);
		System.out.println(" ClusterName=" + CLUSTER_NAME_B + ", CacheName="
				+ cacheClusterB.getCacheName() + ", key=" + CLUSTER_NAME_KEY
				+ ", value=" + value);
		System.out.println("Done\n");
	}

	/**
	 * Integration test method for SelectableCacheFactory#getTypedCache.<br>
	 * This method tests to get typed named cache in only one cluster.
	 */
	@Test
	public void getTypedCacheTest() {
		// Get caches from each clusters
		NamedCache<String, String> cacheClusterA = SelectableCacheFactory
				.getTypedCache(CACHE_IN_CLUSTER_A,
						TypeAssertion.withTypes(String.class, String.class));
		NamedCache<String, String> cacheClusterB = SelectableCacheFactory
				.getTypedCache(CACHE_IN_CLUSTER_B,
						TypeAssertion.withTypes(String.class, String.class));

		String value;
		System.out.println("Get entries");
		value = cacheClusterA.get(CLUSTER_NAME_KEY);
		assertEquals(CLUSTER_NAME_A, value);
		System.out.println(" ClusterName=" + CLUSTER_NAME_A + ", CacheName="
				+ cacheClusterA.getCacheName() + ", key=" + CLUSTER_NAME_KEY
				+ ", value=" + value);
		value = cacheClusterB.get(CLUSTER_NAME_KEY);
		assertEquals(CLUSTER_NAME_B, value);
		System.out.println(" ClusterName=" + CLUSTER_NAME_B + ", CacheName="
				+ cacheClusterB.getCacheName() + ", key=" + CLUSTER_NAME_KEY
				+ ", value=" + value);
		System.out.println("Done\n");
	}

	/**
	 * Integration test method for SelectableCacheFactory#getCache<br>
	 * This method tests to get named cache in multiple clusters.
	 */
	@Test
	public void getSelectableCacheTest() {
		// Get caches from each clusters
		@SuppressWarnings("rawtypes")
		NamedCache cacheBothClusterInClusterA = SelectableCacheFactory
				.getSelectableCache(CLUSTER_NAME_A, CACHE_IN_BOTH_CLUSTERS);
		@SuppressWarnings("rawtypes")
		NamedCache cacheBothClusterInClusterB = SelectableCacheFactory
				.getSelectableCache(CLUSTER_NAME_B, CACHE_IN_BOTH_CLUSTERS);

		String value;
		System.out.println("Get entries");
		value = (String) cacheBothClusterInClusterA.get(CLUSTER_NAME_KEY);
		assertEquals(CLUSTER_NAME_A, value);
		System.out.println(" ClusterName=" + CLUSTER_NAME_A + ", CacheName="
				+ cacheBothClusterInClusterA.getCacheName() + ", key="
				+ CLUSTER_NAME_KEY + ", value=" + value);
		value = (String) cacheBothClusterInClusterB.get(CLUSTER_NAME_KEY);
		assertEquals(CLUSTER_NAME_B, value);
		System.out.println(" ClusterName=" + CLUSTER_NAME_B + ", CacheName="
				+ cacheBothClusterInClusterB.getCacheName() + ", key="
				+ CLUSTER_NAME_KEY + ", value=" + value);
		System.out.println("Done\n");
	}

	/**
	 * Integration test method for SelectableCacheFactory#getTypedCache<br>
	 * This method tests to get typed named cache in multiple clusters.
	 */
	@Test
	public void getSelectableTypedCacheTest() {
		// Get caches from each clusters
		NamedCache<String, String> cacheBothClusterInClusterA = SelectableCacheFactory
				.getSelectableTypedCache(CLUSTER_NAME_A,
						CACHE_IN_BOTH_CLUSTERS,
						TypeAssertion.withTypes(String.class, String.class));
		NamedCache<String, String> cacheBothClusterInClusterB = SelectableCacheFactory
				.getSelectableTypedCache(CLUSTER_NAME_B,
						CACHE_IN_BOTH_CLUSTERS,
						TypeAssertion.withTypes(String.class, String.class));

		String value;
		System.out.println("Get entries");
		value = cacheBothClusterInClusterA.get(CLUSTER_NAME_KEY);
		assertEquals(CLUSTER_NAME_A, value);
		System.out.println(" ClusterName=" + CLUSTER_NAME_A + ", CacheName="
				+ cacheBothClusterInClusterA.getCacheName() + ", key="
				+ CLUSTER_NAME_KEY + ", value=" + value);
		value = cacheBothClusterInClusterB.get(CLUSTER_NAME_KEY);
		assertEquals(CLUSTER_NAME_B, value);
		System.out.println(" ClusterName=" + CLUSTER_NAME_B + ", CacheName="
				+ cacheBothClusterInClusterB.getCacheName() + ", key="
				+ CLUSTER_NAME_KEY + ", value=" + value);
		System.out.println("Done\n");
	}

	/**
	 * Integration test method for SelectableCacheFactory#getClusterNameSet.
	 */
	@Test
	public void getClusterNameSetTest() {
		Set<String> clusterNameSet = SelectableCacheFactory.getClusterNameSet();
		assertEquals(2, clusterNameSet.size());
		assertTrue(clusterNameSet.contains(CLUSTER_NAME_A));
		assertTrue(clusterNameSet.contains(CLUSTER_NAME_B));
	}

	/**
	 * Integration test method for SelectableCacheFactory#removeCacheFactory.
	 */
	@AfterClass
	public static void removeCacheFactoryTest() {
		SelectableCacheFactory.removeCacheFactory(CLUSTER_NAME_B);
		NamedCache<String, String> cacheBothClusterInClusterB = SelectableCacheFactory
				.getSelectableTypedCache(CLUSTER_NAME_B,
						CACHE_IN_BOTH_CLUSTERS,
						TypeAssertion.withTypes(String.class, String.class));
		assertNull(cacheBothClusterInClusterB);
	}

}
