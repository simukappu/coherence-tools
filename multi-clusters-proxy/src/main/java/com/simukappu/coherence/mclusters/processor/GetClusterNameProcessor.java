package com.simukappu.coherence.mclusters.processor;

import com.tangosol.net.CacheFactory;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * EntryProcessor class to get cluster name.<br>
 * 
 * @author Shota Yamazaki
 */
public class GetClusterNameProcessor extends
		AbstractProcessor<Object, Object, String> {

	/**
	 * Serial version used in Serializable interface
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Overridden process method to get cluster name.
	 * 
	 * @param entry
	 *            Any entry in target cache
	 * @return Cluster name
	 * @see com.tangosol.util.InvocableMap.EntryProcessor#process(com.tangosol.util.InvocableMap.Entry)
	 */
	@Override
	public String process(Entry<Object, Object> entry) {
		return CacheFactory.getCluster().getClusterName();
	}

}
