package test.com.simukappu.coherence.writequeue.util;

import java.util.stream.IntStream;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.TypeAssertion;

/**
 * Test class to put test data entries.
 */
public class PutTestIntegerEntries {

	/**
	 * Test main method to put test data entries.
	 * 
	 * @param args
	 *            The first argument specify the target cache name (default is
	 *            ThrowExceptionCacheStoreCache)<br>
	 *            The second argument specify the number of test entries
	 *            (default is 10)
	 */
	public static void main(String[] args) {

		// Check targetCacheName argument was passed in
		String targetCacheName = "ThrowExceptionCacheStoreCache";
		int numData = 10;
		if (args.length > 0) {
			targetCacheName = args[0];
		} else if (args.length > 1) {
			numData = Integer.parseInt(args[1]);
		}

		// Put test data
		putTestIntegerEntries(targetCacheName, numData);
	}

	public static void putTestIntegerEntries(String targetCacheName, int numData) {
		NamedCache<Integer, Integer> targetCache = CacheFactory.getTypedCache(
				targetCacheName,
				TypeAssertion.withTypes(Integer.class, Integer.class));

		System.out.println("Put " + numData + " test entries");
		IntStream.range(0, numData).forEach(i -> {
			targetCache.put(i, i);
		});
	}
}
