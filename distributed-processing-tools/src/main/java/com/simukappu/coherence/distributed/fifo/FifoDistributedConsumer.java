package com.simukappu.coherence.distributed.fifo;

import java.util.function.Consumer;

/**
 * Consumer implementation class to distributed processing as First in, first
 * out.<br>
 * This consumer calls putIfAbsent-like method to coherence cache with a key and
 * then call process method only when the entry is first in.<br>
 * 
 * @author Shota Yamazaki
 */
public abstract class FifoDistributedConsumer<T> implements FifoDistributedProcessor<T>, Consumer<T> {

	/**
	 * Target coherence cache name to use as the key set for FIFO distributed
	 * processing
	 */
	String targetCacheName = null;

	/**
	 * Constructor with the target cache name
	 * 
	 * @param targetCacheName
	 *            Target coherence cache name to use as the key set for FIFO
	 *            distributed processing
	 */
	public FifoDistributedConsumer(String targetCacheName) {
		this.targetCacheName = targetCacheName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.simukappu.coherence.distributed.fifo.FifoDistributedProcessor#
	 * getTargetCacheName()
	 */
	@Override
	public String getTargetCacheName() {
		return this.targetCacheName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.function.Consumer#accept(java.lang.Object)
	 */
	@Override
	public void accept(T t) {
		if (this.invoke(t)) {
			process(t);
		}
	}

	/**
	 * Processing function to be overridden.
	 * 
	 * @param t
	 *            The function argument
	 */
	abstract public void process(T t);

}