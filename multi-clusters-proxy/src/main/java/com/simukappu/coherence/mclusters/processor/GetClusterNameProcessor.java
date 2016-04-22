package com.simukappu.coherence.mclusters.processor;

import java.io.IOException;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.net.CacheFactory;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * EntryProcessor class to get cluster name.<br>
 * 
 * @author Shota Yamazaki
 */
public class GetClusterNameProcessor extends AbstractProcessor<Object, Object, String> implements PortableObject {


	/**
	 * Serial version used in Serializable interface
	 */
	private static final long serialVersionUID = -2305224177615714426L;

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

	@Override
	public void readExternal(PofReader reader) throws IOException {
	}

	@Override
	public void writeExternal(PofWriter writer) throws IOException {
	}

}
