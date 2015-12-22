package test.com.simukappu.coherence.writequeue.server;

import com.tangosol.net.DefaultCacheServer;

/**
 * Test class to start cache server.
 */
public class CacheServer {

	/**
	 * Test main method to call com.tangosol.net.DefaultCacheServer.main in
	 * Coherence.
	 */
	public static void main(String[] args) {
		System.setProperty("tangosol.coherence.distributed.localstorage",
				"true");
		System.setProperty("tangosol.coherence.rwbm.requeue.delay", "10000");
		DefaultCacheServer.main(args);
	}

}
