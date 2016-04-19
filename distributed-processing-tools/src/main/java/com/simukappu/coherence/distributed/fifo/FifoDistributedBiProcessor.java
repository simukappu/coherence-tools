package com.simukappu.coherence.distributed.fifo;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.TypeAssertion;
import com.tangosol.util.filter.NotFilter;
import com.tangosol.util.filter.PresentFilter;
import com.tangosol.util.processor.ConditionalPut;

/**
 * Interface to distributed processing as First in, first out.<br>
 * This interface is to be extended by FunctionalInterface with two arguments
 * like BiConsumer or BiFunction.<br>
 * 
 * @author Shota Yamazaki
 *
 * @param <T>
 *            The function argument class and the key class of coherence cache
 *            for FIFO distributed processing
 */
public interface FifoDistributedBiProcessor<T> {

	/**
	 * Check if the entry exists in the cache (just check if the entry has been
	 * processed).
	 * 
	 * @param t
	 *            Key of the entry with the processing
	 * @param targetCacheName
	 *            Target cache name to use as the key set for distributed
	 *            processing
	 * @return If the entry exists in the cache
	 */
	public default boolean check(T t, String targetCacheName) {
		// Return if the key entry exists in a target cache
		@SuppressWarnings("unchecked")
		NamedCache<T, Integer> tagetCache = (NamedCache<T, Integer>) CacheFactory.getTypedCache(targetCacheName,
				TypeAssertion.withTypes(t.getClass(), Integer.class));
		return (tagetCache.get(t) != null);
	}

	/**
	 * Call putIfAbsent-like method to coherence cache and return if the entry
	 * is the first in or not
	 * 
	 * @param t
	 *            Key of the entry with the processing
	 * @param targetCacheName
	 *            Target cache name to use as the key set for distributed
	 *            processing
	 * @return If the entry is the first in or not
	 */
	public default boolean invoke(T t, String targetCacheName) {
		// When the key is absent from target cache, put a static value and
		// return true.
		// When the key exists, return false
		@SuppressWarnings("unchecked")
		NamedCache<T, Integer> tagetCache = (NamedCache<T, Integer>) CacheFactory.getTypedCache(targetCacheName,
				TypeAssertion.withTypes(t.getClass(), Integer.class));
		return (tagetCache.invoke(t,
				new ConditionalPut<T, Integer>(new NotFilter<Object>(PresentFilter.INSTANCE()), 1, true)) == null);
	}

}