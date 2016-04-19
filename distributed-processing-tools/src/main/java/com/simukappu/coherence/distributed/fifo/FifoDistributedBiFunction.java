package com.simukappu.coherence.distributed.fifo;

import java.util.function.BiFunction;

/**
 * FunctionalInterface extends BiFunction to distributed processing as First in,
 * first out.<br>
 * This function calls putIfAbsent-like method to coherence cache with a key and
 * then call process method only when the entry is first in.<br>
 * We have to call this interface with a stream including coherence cache name
 * to use as the key set for distributed processing<br>
 * 
 * @author Shota Yamazaki
 *
 * @param <T>
 *            The function argument class and the key class of coherence cache
 *            for FIFO distributed processing
 * @param <R>
 *            The function return class
 */
@FunctionalInterface
public interface FifoDistributedBiFunction<T, R> extends FifoDistributedBiProcessor<T>, BiFunction<T, String, R> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.function.BiFunction#apply(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public default R apply(T t, String targetCacheName) {
		if (this.invoke(t, targetCacheName)) {
			return process(t);
		} else {
			return null;
		}
	}

	/**
	 * Processing function to be overridden.
	 * 
	 * @param t
	 *            the function argument
	 */
	public abstract R process(T t);

}