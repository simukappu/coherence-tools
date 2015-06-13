package test.tool.coherence.util;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

/**
 * Test class to put test data entries.
 */
public class PutTestData {

	/**
	 * Test main method to put test data entries.
	 * 
	 * @param args
	 *            The first argument specify the target cache name
	 */
	public static void main(String[] args) {

		// Check targetCacheName argument was passed in
		if (args.length < 2) {
			System.err.println("Usage: java program targetCacheName numData");
			System.exit(1);
		}
		String targetCacheName = args[0];
		long numData = Long.parseLong(args[1]);
		NamedCache targetCache = CacheFactory.getCache(targetCacheName);

		System.out.println("Put " + numData + " test entries");
		for (long i = 0; i < numData; i++) {
			targetCache.put(i, i);
		}
	}
}
