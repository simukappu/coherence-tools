package test.com.simukappu.coherence.writequeue;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import test.com.simukappu.coherence.writequeue.util.PutTestIntegerEntries;

import com.simukappu.coherence.writequeue.ClearWriteQueueProcessor;
import com.simukappu.coherence.writequeue.GetWriteQueueSizeProcessor;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.filter.AlwaysFilter;

/**
 * Integration test class for write behind tools.
 * 
 * @author Shota Yamazaki
 */
public class IntegrationTest {

	/**
	 * Integration test method for write behind tools.<br>
	 * This method runs following steps.<br>
	 * 1. Check if cache size and write behind queue size is empty in the
	 * cluster.<br>
	 * 2. Put test data entries.<br>
	 * 3. Check if cache size and write behind queue size is the number of test
	 * entries.<br>
	 * 4. Clear write behind queue using write behind tools.<br>
	 * 5. Check if cache size is not changed but write behind queue size is
	 * empty.<br>
	 * You can run this test as stand-alone or multi-processes cluster by
	 * running CacheServer before this test.
	 */
	@Test
	public void integrationTestForWriteQueueTools() {
		// Total number of test data entries
		int numTotalData = 10;
		int cacheSize = 0;
		int totalQueueSize = 0;

		// Get target named cache
		NamedCache<Object, Object> targetCache = CacheFactory
				.getCache("ThrowExceptionCacheStoreCache");

		// Truncate cache and check if current cache size (number of cached
		// entries) is 0
		targetCache.truncate();
		cacheSize = targetCache.size();
		System.out.println("Current cache size: " + cacheSize);
		assertEquals(0, cacheSize);

		// Check if current total write behind queue size is 0
		totalQueueSize = getTotalWriteQueueSize(targetCache);
		System.out.println("Current total write behind queue size: "
				+ totalQueueSize + "\n");
		assertEquals(0, totalQueueSize);

		// Put test data
		System.out.println("Put test data");
		putWriteBehindTestData(targetCache, numTotalData);

		// Check if current cache size (number of cached entries) is total
		// number of test data entries
		cacheSize = targetCache.size();
		System.out.println("\n" + "Current cache size: " + cacheSize);
		assertEquals(numTotalData, cacheSize);

		// Check if current total write behind queue size is total number of
		// test data entries
		totalQueueSize = getTotalWriteQueueSize(targetCache);
		System.out.println("Current total write behind queue size: "
				+ totalQueueSize + "\n");
		assertEquals(numTotalData, totalQueueSize);

		// Clear write behind queue
		clearWriteQueue(targetCache);
		System.out.println("Removed entries from write behind queue\n");

		// Check if current cache size (number of cached entries) is total
		// number of test data entries
		cacheSize = targetCache.size();
		System.out.println("Current cache size: " + cacheSize);
		assertEquals(numTotalData, cacheSize);

		// Check if current total write behind queue size is 0
		totalQueueSize = getTotalWriteQueueSize(targetCache);
		System.out.println("Current total write behind queue size: "
				+ totalQueueSize);
		assertEquals(0, totalQueueSize);
	}

	/**
	 * The method to get total write behind queue size by
	 * GetWriteQueueSizeProcessor.
	 * 
	 * @param targetCache
	 *            The target named cache
	 */
	private int getTotalWriteQueueSize(NamedCache<Object, Object> targetCache) {
		// Invoke GetWriteQueueSizeProcessor to all local-storage enabled nodes
		Map<Object, Map.Entry<Integer, Integer>> mapResults = targetCache
				.invokeAll(
						new AlwaysFilter<Object>(),
						new GetWriteQueueSizeProcessor(targetCache
								.getCacheName()));

		// Return total write behind queue size from the map results
		List<Map.Entry<Integer, Integer>> resultList = new ArrayList<Map.Entry<Integer, Integer>>(
				mapResults.values());
		int totalQueueSize = resultList.stream().mapToInt(Map.Entry::getValue)
				.sum();
		return totalQueueSize;
	}

	/**
	 * The method to clear write behind queue by ClearWriteQueueProcessor.
	 * 
	 * @param targetCache
	 *            The target named cache
	 */
	private void clearWriteQueue(NamedCache<Object, Object> targetCache) {
		// Invoke ClearWriteQueueProcessor to all local-storage enabled nodes
		targetCache.invokeAll(new AlwaysFilter<Object>(),
				new ClearWriteQueueProcessor(targetCache.getCacheName()));
	}

	/**
	 * The method to put test data for write behind.
	 * 
	 * @param targetCache
	 *            The target named cache
	 * @param numData
	 *            Number of test data entries
	 */
	private void putWriteBehindTestData(NamedCache<Object, Object> targetCache,
			int numData) {
		// Put test data to the target cache
		PutTestIntegerEntries.main(new String[] { targetCache.getCacheName(),
				String.valueOf(numData) });
	}
}
