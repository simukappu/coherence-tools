package test.com.simukappu.coherence.mclusters.server;

import com.tangosol.net.DefaultCacheServer;

/**
 * Test class to start cache server in cluster B.
 */
public class CacheServerInClusterB {

	/**
	 * Test main method to call
	 * {@link com.tangosol.net.DefaultCacheServer#main(String[])} in Coherence
	 * joining to cluster B.
	 * 
	 * @param args
	 *            Passed to
	 *            {@link com.tangosol.net.DefaultCacheServer#main(String[])}
	 */
	public static void main(String[] args) {
		System.setProperty("tangosol.coherence.override",
				"clusterB-override.xml");
		System.setProperty("tangosol.coherence.cacheconfig",
				"clusterB-cache-config.xml");
		System.setProperty("tangosol.coherence.distributed.localstorage",
				"true");

		DefaultCacheServer.main(args);
	}

}
