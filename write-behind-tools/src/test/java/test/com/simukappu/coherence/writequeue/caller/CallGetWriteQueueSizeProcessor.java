package test.com.simukappu.coherence.writequeue.caller;

import com.simukappu.coherence.writequeue.GetWriteQueueSizeProcessor;

/**
 * Test class to start cache server.
 */
public class CallGetWriteQueueSizeProcessor {

	/**
	 * Test main method to call
	 * {@link com.simukappu.coherence.writequeue.GetWriteQueueSizeProcessor#main(String[])}
	 * .
	 * 
	 * @param args
	 *            No use
	 */
	public static void main(String[] args) {
		GetWriteQueueSizeProcessor
				.main(new String[] { "ThrowExceptionCacheStoreCache" });
	}

}
