package com.simukappu.coherence.distributed.sever;

import com.tangosol.net.DefaultCacheServer;

/**
 * Test class to start cache server.
 */
public class CacheServer {

	/**
	 * Test main method to call
	 * {@link com.tangosol.net.DefaultCacheServer#main(String[])} in Coherence.
	 * 
	 * @param args
	 *            Passed to
	 *            {@link com.tangosol.net.DefaultCacheServer#main(String[])}
	 */
	public static void main(String[] args) {
		System.setProperty("tangosol.coherence.distributed.localstorage",
				"true");
		DefaultCacheServer.main(args);
	}

}
