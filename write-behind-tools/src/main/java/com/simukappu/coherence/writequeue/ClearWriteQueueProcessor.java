package com.simukappu.coherence.writequeue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
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
 * {@code NamedCache<Object, Object> namedCache = CacheFactory.getCache("CacheName");}
 * {@code Map<Object, Map.Entry<Integer, Integer>> mapResults = namedCache.invokeAll(new AlwaysFilter
 * <Object>(), new
 *       ClearWriteQueueProcessor(targetCacheName));}
 * 
 * @author Shota Yamazaki
 */
public class ClearWriteQueueProcessor
		implements EntryProcessor<Object, Object, Map.Entry<Integer, Integer>>, PortableObject {

	/**
	 * Serial version used in Serializable interface
	 */
	private static final long serialVersionUID = 6279242053498148185L;

	/**
	 * Interval to call remove requests to write behind queue
	 */
	private static long REMOVE_WRITE_QUEUE_REQUEST_INTERVAL = 100;

	/**
	 * Target cache name to clear write behind queue
	 */
	private String targetCacheName = null;

	/**
	 * Default constructor
	 * 
	 */
	public ClearWriteQueueProcessor() {
		super();
	}

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
	 * @return Entry with Node ID and previous size of write behind queue (not
	 *         cached data)
	 * @see com.tangosol.util.InvocableMap.EntryProcessor#process(com.tangosol.util.InvocableMap.Entry)
	 */
	@Override
	public Map.Entry<Integer, Integer> process(Entry<Object, Object> entry) {

		// Get ReadWriteBackingMap
		BinaryEntry<Object, Object> binEntry = (BinaryEntry<Object, Object>) entry;
		BackingMapContext ctx = binEntry.getContext().getBackingMapContext(this.targetCacheName);
		// TODO Must be careful to use deprecated method as of Coherence 12.1.3
		@SuppressWarnings("deprecation")
		ReadWriteBackingMap rwBackingMap = (ReadWriteBackingMap) ctx.getBackingMap();
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

		// Get running member ID
		int memberId = binEntry.getContext().getCacheService().getCluster().getLocalMember().getId();

		// Set member ID and previous size of write behind queue and return it
		// Return previous size of write behind queue
		return new PortableEntry<Integer, Integer>(memberId, previousQueueSize);
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
	@Override
	public Map<Object, Map.Entry<Integer, Integer>> processAll(Set<? extends Entry<Object, Object>> setEntries) {

		Map<Object, Map.Entry<Integer, Integer>> mapResults = new ListMap<Object, Map.Entry<Integer, Integer>>();

		// Clear write behind queue unless setEntries has no data
		Iterator<? extends Entry<Object, Object>> iter = setEntries.iterator();
		if (iter.hasNext()) {
			// Call process method for first entry
			Entry<Object, Object> entry = iter.next();
			mapResults.put(entry.getKey(), this.process(entry));
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
		NamedCache<Object, Object> targetCache = CacheFactory.getCache(targetCacheName);

		// Invoke ClearWriteQueueProcessor to all local-storage enabled nodes
		Map<Object, Map.Entry<Integer, Integer>> mapResults = targetCache.invokeAll(new AlwaysFilter<Object>(),
				new ClearWriteQueueProcessor(targetCacheName));

		// Sort result map set by keys
		List<Map.Entry<Integer, Integer>> resultList = new ArrayList<>(mapResults.values());
		resultList.sort((a, b) -> a.getKey() - b.getKey());

		// Display results
		System.out.println("Removed entries from write behind queue:");
		resultList.forEach(resultEntry -> {
			System.out.println(" " + resultEntry.getValue() + " entries from Node# " + resultEntry.getKey());
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tangosol.io.pof.PortableObject#readExternal(com.tangosol.io.pof.
	 * PofReader)
	 */
	@Override
	public void readExternal(PofReader reader) throws IOException {
		targetCacheName = reader.readString(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tangosol.io.pof.PortableObject#writeExternal(com.tangosol.io.pof.
	 * PofWriter)
	 */
	@Override
	public void writeExternal(PofWriter writer) throws IOException {
		writer.writeString(0, targetCacheName);
	}

}