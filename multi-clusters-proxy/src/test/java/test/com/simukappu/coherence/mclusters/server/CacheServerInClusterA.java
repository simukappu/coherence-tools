package test.com.simukappu.coherence.mclusters.server;

import com.tangosol.net.DefaultCacheServer;

/**
 * Test class to start cache server in cluster A.
 */
public class CacheServerInClusterA {

	/**
	 * Test main method to call
	 * {@link com.tangosol.net.DefaultCacheServer#main(String[])} in Coherence
	 * joining to cluster A.
	 * 
	 * @param args
	 *            Passed to
	 *            {@link com.tangosol.net.DefaultCacheServer#main(String[])}
	 */
	public static void main(String[] args) {
		System.setProperty("tangosol.coherence.override",
				"clusterA-override.xml");
		System.setProperty("tangosol.coherence.cacheconfig",
				"clusterA-cache-config.xml");
		System.setProperty("tangosol.coherence.distributed.localstorage",
				"true");

		DefaultCacheServer.main(args);
	}

}
