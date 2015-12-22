package com.simukappu.coherence.writequeue;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
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
 * EntryProcessor class to get the size of write behind queue in read/write
 * backing map scheme cache.<br>
 * This processor should be invoked from invokeAll method since process method
 * should run only one time for each node.<br>
 * <br>
 * For example, invoke as follows:<br>
 * {@code NamedCache<Object, Object> namedCache = CacheFactory.getCache("CacheName");}
 * {@code Map<Object, Map.Entry<Integer, Integer>> mapResults = namedCache.invokeAll(new AlwaysFilter<Object>(), new
 * GetWriteQueueSizeProcessor(targetCacheName));}
 * 
 * @author Shota Yamazaki
 */
public class GetWriteQueueSizeProcessor implements
		EntryProcessor<Object, Object, Map.Entry<Integer, Integer>> {

	/**
	 * Serial version used in Serializable interface
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Target cache name to get the size of write behind queue
	 */
	private String targetCacheName = null;

	/**
	 * Constructor with the target cache name
	 * 
	 * @param targetCacheName
	 *            Target cache name to get the size of write behind queue
	 */
	public GetWriteQueueSizeProcessor(String targetCacheName) {
		super();
		this.targetCacheName = targetCacheName;
	}

	/**
	 * Overridden process method to get the size of write behind queue.<br>
	 * Get WriteQueue through BackingMapContext and ReadWriteBackingMap to
	 * operate write behind queue.<br>
	 * 
	 * @param entry
	 *            One entry in target cache
	 * @return Entry with Node ID and the size of write behind queue (not cached
	 *         data)
	 * @see com.tangosol.util.InvocableMap.EntryProcessor#process(com.tangosol.util.InvocableMap.Entry)
	 */
	@Override
	public Map.Entry<Integer, Integer> process(Entry<Object, Object> entry) {

		// Get ReadWriteBackingMap
		BinaryEntry<Object, Object> binEntry = (BinaryEntry<Object, Object>) entry;
		BackingMapContext ctx = binEntry.getContext().getBackingMapContext(
				this.targetCacheName);
		// TODO Must be careful to use deprecated method as of Coherence 12.1.3
		@SuppressWarnings("deprecation")
		ReadWriteBackingMap rwBackingMap = (ReadWriteBackingMap) ctx
				.getBackingMap();
		// Get WriteQueue
		WriteQueue writeQueue = rwBackingMap.getWriteQueue();

		// Get the size of write behind queue
		int queueSize = writeQueue.size();

		// Get running member ID
		int memberId = binEntry.getContext().getCacheService().getCluster()
				.getLocalMember().getId();

		// Set member ID and previous size of write behind queue and return it
		// Return the size of write behind queue
		return new SimpleEntry<Integer, Integer>(memberId, queueSize);
	}

	/**
	 * Overridden processAll method to get the size of write behind queue in one
	 * node.<br>
	 * Call process method only one time even if setEntries has two or more
	 * entries.<br>
	 * 
	 * @param setEntries
	 *            Entry set in target cache
	 * @return Result map whose value is entry with Node ID and the size of
	 *         write behind queue (not cached data)
	 * @see com.tangosol.util.InvocableMap.EntryProcessor#processAll(java.util.Set)
	 */
	@Override
	public Map<Object, Map.Entry<Integer, Integer>> processAll(
			Set<? extends Entry<Object, Object>> setEntries) {

		Map<Object, Map.Entry<Integer, Integer>> mapResults = new ListMap<Object, Map.Entry<Integer, Integer>>();

		// Get the size of write behind queue unless setEntries has no data
		Iterator<? extends Entry<Object, Object>> iter = setEntries.iterator();
		if (iter.hasNext()) {
			// Call process method for first entry
			Entry<Object, Object> entry = iter.next();
			mapResults.put(entry.getKey(), this.process(entry));
		}

		// Return result map whose value is map with Node ID and the size
		// of write behind queue.
		// Result map in every node is merged by Coherence and returned to the
		// client.
		return mapResults;
	}

	/**
	 * Sample main method to get the size of write behind queue in specified
	 * cache.<br>
	 * Call processAll method of GetWriteQueueSizeProcessor by invokeAll method
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
		NamedCache<Object, Object> targetCache = CacheFactory
				.getCache(targetCacheName);

		// Invoke GetWriteQueueSizeProcessor to all local-storage enabled nodes
		Map<Object, Map.Entry<Integer, Integer>> mapResults = targetCache
				.invokeAll(new AlwaysFilter<Object>(),
						new GetWriteQueueSizeProcessor(targetCacheName));

		// Sort result map set by keys
		List<Map.Entry<Integer, Integer>> resultList = new ArrayList<>(
				mapResults.values());
		resultList.sort((a, b) -> {
			return a.getKey() - b.getKey();
		});

		// Display results
		System.out.println("Size of write behind queue:");
		resultList.forEach(resultEntry -> {
			System.out.println(" " + resultEntry.getValue()
					+ " entries in Node# " + resultEntry.getKey());
		});
	}
}