package tool.coherence.util.writequeue;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tangosol.net.BackingMapContext;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.ReadWriteBackingMap;
import com.tangosol.net.cache.ReadWriteBackingMap.WriteQueue;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.InvocableMap.EntryProcessor;
import com.tangosol.util.ListMap;
import com.tangosol.util.filter.AlwaysFilter;

/**
 * EntryProcessor class to clear write behind queue of read/write backing map
 * scheme cache.<br>
 * This processor should be invoked from invokeAll method since process method
 * should run only one time for each node.<br>
 * <br>
 * For example, invoke as follows:<br>
 * {@code Map mapResults = namedCache.invokeAll(new AlwaysFilter(), new
 *       ClearWriteQueueProcessor(targetCacheName));}
 * 
 * @author Shota Yamazaki
 */
public class ClearWriteQueueProcessor implements EntryProcessor {

	/**
	 * Serial version used in Serializable interface
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Interval to call remove requests to write behind queue
	 */
	private static long REMOVE_WRITE_QUEUE_REQUEST_INTERVAL = 100;

	/**
	 * Target cache name to clear write behind queue
	 */
	private String targetCacheName = null;

	/**
	 * Constructor with the target cache name
	 * 
	 * @param targetCacheName
	 *            Target cache name to clear write behind queue
	 */
	public ClearWriteQueueProcessor(String targetCacheName) {
		super();
		this.targetCacheName = targetCacheName;
	}

	/**
	 * Overridden process method to clear write behind queue.<br>
	 * Get WriteQueue through BackingMapContext and ReadWriteBackingMap to
	 * operate write behind queue.<br>
	 * 
	 * @param entry
	 *            One entry in target cache
	 * @return Previous size of write behind queue
	 * @see com.tangosol.util.InvocableMap.EntryProcessor#process(com.tangosol.util.InvocableMap.Entry)
	 */
	@Override
	public Object process(Entry entry) {

		// Get ReadWriteBackingMap
		BinaryEntry binEntry = (BinaryEntry) entry;
		BackingMapContext ctx = binEntry.getContext().getBackingMapContext(
				this.targetCacheName);
		// TODO Must be careful to use deprecated method as of Coherence 12.1.3
		@SuppressWarnings("deprecation")
		ReadWriteBackingMap rwBackingMap = (ReadWriteBackingMap) ctx
				.getBackingMap();
		// Get WriteQueue
		WriteQueue writeQueue = rwBackingMap.getWriteQueue();

		// Clear write behind queue
		int previousQueueSize = writeQueue.size();
		while (!writeQueue.isEmpty()) {
			writeQueue.removeNoWait();
			try {
				Thread.sleep(REMOVE_WRITE_QUEUE_REQUEST_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Return previous size of write behind queue
		return previousQueueSize;
	}

	/**
	 * Overridden processAll method to clear write behind queue in one node.<br>
	 * Call process method only one time even if setEntries has two or more
	 * entries.<br>
	 * 
	 * @param setEntries
	 *            Entry set in target cache
	 * @return Result map whose value is entry with Node ID and previous size of
	 *         write behind queue (not cached data)
	 * @see com.tangosol.util.InvocableMap.EntryProcessor#processAll(java.util.Set)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map processAll(Set setEntries) {

		Map mapResults = new ListMap();
		Iterator iter = (Iterator) setEntries.iterator();

		// Clear write behind queue unless setEntries has no data
		if (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			// Call process method for first entry
			int previousQueueSize = (Integer) this.process(entry);
			BinaryEntry binEntry = (BinaryEntry) entry;
			// Get running member ID
			int memberId = binEntry.getContext().getCacheService().getCluster()
					.getLocalMember().getId();
			// Set member ID and previous size of write behind queue in order to
			// return them
			mapResults.put(entry.getKey(),
					new AbstractMap.SimpleEntry<Integer, Integer>(memberId,
							previousQueueSize));
		}

		// Return result map whose value is map with Node ID and previous size
		// of write behind queue.
		// Result map in every node is merged by Coherence and returned to the
		// client.
		return mapResults;
	}

	/**
	 * Sample main method to clear write behind queue of specified cache.<br>
	 * Call processAll method of ClearWriteQueueProcessor by invokeAll method
	 * and AlwaysFilter.<br>
	 * 
	 * @param args
	 *            The first argument specify the target cache name
	 */
	public static void main(String[] args) {

		// Check targetCacheName argument was passed in
		if (args.length == 0) {
			System.err.println("Usage: java program targetCacheName");
			System.exit(1);
		}
		String targetCacheName = args[0];
		NamedCache targetCache = CacheFactory.getCache(targetCacheName);

		// Invoke ClearWriteQueueProcessor to all local-storage enabled nodes
		@SuppressWarnings("rawtypes")
		Map mapResults = targetCache.invokeAll(new AlwaysFilter(),
				new ClearWriteQueueProcessor(targetCacheName));

		// Sort result map set
		@SuppressWarnings("unchecked")
		List<Map.Entry<Integer, Integer>> resultList = new ArrayList<Map.Entry<Integer, Integer>>(
				mapResults.values());
		Collections.sort(resultList,
				new Comparator<Map.Entry<Integer, Integer>>() {
					// Comparator method (return -1, 0 or 1)
					@Override
					public int compare(Map.Entry<Integer, Integer> a,
							Map.Entry<Integer, Integer> b) {
						// Compare keys
						int aKey = a.getKey();
						int bKey = b.getKey();
						if (aKey > bKey)
							return 1;
						else if (aKey == bKey)
							return 0;
						else
							return -1;
					}
				});

		// Display results
		System.out.println("Removed entries from write behind queue:");
		for (Map.Entry<Integer, Integer> resultEntry : resultList) {
			System.out.println(" " + resultEntry.getValue()
					+ " entries from Node# " + resultEntry.getKey());
		}
	}
}