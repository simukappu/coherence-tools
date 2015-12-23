package test.com.simukappu.coherence.mclusters.proxy;

import com.tangosol.net.DefaultCacheServer;

/**
 * Test class to start extend proxy server in cluster A.
 */
public class ProxyServerInClusterA {

	/**
	 * Test main method to call com.tangosol.net.DefaultCacheServer.main in
	 * Coherence.
	 */
	public static void main(String[] args) {
		System.setProperty("tangosol.coherence.override",
				"clusterA-override.xml");
		System.setProperty("tangosol.coherence.cacheconfig",
				"clusterA-proxy-config.xml");
		// Set this property false if you run other cache servers in this
		// cluster
		System.setProperty("tangosol.coherence.distributed.localstorage",
				"true");

		DefaultCacheServer.main(args);
	}

}
