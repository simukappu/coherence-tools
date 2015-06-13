package test.tool.coherence.util.writequeue;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import test.tool.coherence.util.PutTestData;
import tool.coherence.util.writequeue.ClearWriteQueueProcessor;
import tool.coherence.util.writequeue.GetWriteQueueSizeProcessor;

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
		NamedCache targetCache = CacheFactory
				.getCache("ThrowExceptionCacheStoreCache");

		// Check if current cache size (number of cached entries) is 0
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
	private int getTotalWriteQueueSize(NamedCache targetCache) {
		// Invoke GetWriteQueueSizeProcessor to all local-storage enabled nodes
		@SuppressWarnings("rawtypes")
		Map mapResults = targetCache.invokeAll(new AlwaysFilter(),
				new GetWriteQueueSizeProcessor(targetCache.getCacheName()));

		// Return total write behind queue size from the map results
		@SuppressWarnings("unchecked")
		List<Map.Entry<Integer, Integer>> resultList = new ArrayList<Map.Entry<Integer, Integer>>(
				mapResults.values());
		int totalQueueSize = 0;
		for (Map.Entry<Integer, Integer> resultEntry : resultList) {
			totalQueueSize += resultEntry.getValue();
		}
		return totalQueueSize;
	}

	/**
	 * The method to clear write behind queue by ClearWriteQueueProcessor.
	 * 
	 * @param targetCache
	 *            The target named cache
	 */
	private void clearWriteQueue(NamedCache targetCache) {
		// Invoke ClearWriteQueueProcessor to all local-storage enabled nodes
		targetCache.invokeAll(new AlwaysFilter(), new ClearWriteQueueProcessor(
				targetCache.getCacheName()));
	}

	/**
	 * The method to put test data for write behind.
	 * 
	 * @param targetCache
	 *            The target named cache
	 */
	private void putWriteBehindTestData(NamedCache targetCache, int numData) {
		// Put test data to the target cache
		String args[] = { targetCache.getCacheName(), String.valueOf(numData) };
		PutTestData.main(args);
	}
}
