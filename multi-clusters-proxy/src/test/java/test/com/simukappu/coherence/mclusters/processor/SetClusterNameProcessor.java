package test.com.simukappu.coherence.mclusters.processor;

import java.io.IOException;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.net.CacheFactory;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * EntryProcessor class to set cluster name as a value.<br>
 */
public class SetClusterNameProcessor extends AbstractProcessor<String, String, String> implements PortableObject {

	/**
	 * Serial version used in Serializable interface
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Overridden process method to set cluster name as a value.
	 * 
	 * @param entry
	 *            One entry in target cache
	 * @return Cluster name set as a value
	 * @see com.tangosol.util.InvocableMap.EntryProcessor#process(com.tangosol.util.InvocableMap.Entry)
	 */
	@Override
	public String process(Entry<String, String> entry) {
		String clusterName = CacheFactory.getCluster().getClusterName();
		entry.setValue(clusterName);
		return clusterName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tangosol.io.pof.PortableObject#readExternal(com.tangosol.io.pof.
	 * PofReader)
	 */
	@Override
	public void readExternal(PofReader reader) throws IOException {
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
	}

}
