package test.com.simukappu.coherence.mclusters.processor;

import com.tangosol.net.CacheFactory;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * EntryProcessor class to set cluster name as a value.<br>
 */
public class SetClusterNameProcessor extends
		AbstractProcessor<String, String, String> {

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

}
