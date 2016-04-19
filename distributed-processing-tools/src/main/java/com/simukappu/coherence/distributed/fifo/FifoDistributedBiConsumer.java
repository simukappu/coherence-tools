package com.simukappu.coherence.distributed.fifo;

import java.util.function.BiConsumer;

/**
 * FunctionalInterface extends BiConsumer to distributed processing as First in,
 * first out.<br>
 * This consumer calls putIfAbsent-like method to coherence cache with a key and
 * then call process method only when the entry is first in.<br>
 * We have to call this interface with a stream including coherence cache name
 * to use as the key set for distributed processing<br>
 * 
 * @author Shota Yamazaki
 *
 * @param <T>
 *            The function argument class and the key class of coherence cache
 *            for FIFO distributed processing
 */
@FunctionalInterface
public interface FifoDistributedBiConsumer<T> extends FifoDistributedBiProcessor<T>, BiConsumer<T, String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.function.BiConsumer#accept(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public default void accept(T t, String targetCacheName) {
		if (this.invoke(t, targetCacheName)) {
			process(t);
		}
	}

	/**
	 * Processing function to be overridden.
	 * 
	 * @param t
	 *            the function argument
	 */
	public abstract void process(T t);

}