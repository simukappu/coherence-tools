package com.simukappu.coherence.distributed.fifo;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.TypeAssertion;
import com.tangosol.util.filter.NotFilter;
import com.tangosol.util.filter.PresentFilter;
import com.tangosol.util.processor.ConditionalPut;

/**
 * Interface to distributed processing as First in, first out.<br>
 * This interface is to be extended by FunctionalInterface with one argument
 * like Consumer or Function.<br>
 * 
 * @author Shota Yamazaki
 */
public interface FifoDistributedProcessor<T> {

	/**
	 * Check if the entry exists in the cache (just check if the entry has been
	 * processed).
	 * 
	 * @param t
	 *            Key of the entry with the processing
	 * @return If the entry exists in the cache
	 */
	public default boolean check(T t) {
		// Return if the key entry exists in a target cache
		@SuppressWarnings("unchecked")
		NamedCache<T, Integer> tagetCache = (NamedCache<T, Integer>) CacheFactory
				.getTypedCache(this.getTargetCacheName(), TypeAssertion.withTypes(t.getClass(), Integer.class));
		return (tagetCache.get(t) != null);
	}

	/**
	 * Call putIfAbsent-like method to coherence cache and return if the entry
	 * is the first in or not
	 * 
	 * @param t
	 *            Key of the entry with the processing
	 * @return If the entry is the first in or not
	 */
	public default boolean invoke(T t) {
		// When the key is absent from target cache, put a static value and
		// return true.
		// When the key exists, return false
		@SuppressWarnings("unchecked")
		NamedCache<T, Integer> tagetCache = (NamedCache<T, Integer>) CacheFactory
				.getTypedCache(this.getTargetCacheName(), TypeAssertion.withTypes(t.getClass(), Integer.class));
		return (tagetCache.invoke(t,
				new ConditionalPut<T, Integer>(new NotFilter<Object>(PresentFilter.INSTANCE()), 1, true)) == null);
	}

	/**
	 * Get coherence cache name to use as the key set for FIFO distributed processing
	 * 
	 * @return Target cache name
	 */
	abstract public String getTargetCacheName();

}