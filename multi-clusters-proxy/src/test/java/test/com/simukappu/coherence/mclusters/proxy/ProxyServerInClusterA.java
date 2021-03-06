package test.com.simukappu.coherence.mclusters.proxy;

import com.tangosol.net.DefaultCacheServer;

/**
 * Test class to start extend proxy server in cluster A.
 */
public class ProxyServerInClusterA {

	/**
	 * Test main method to call
	 * {@link com.tangosol.net.DefaultCacheServer#main(String[])} in Coherence
	 * joining to cluster A as extend proxy server.
	 * 
	 * @param args
	 *            Passed to
	 *            {@link com.tangosol.net.DefaultCacheServer#main(String[])}
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
