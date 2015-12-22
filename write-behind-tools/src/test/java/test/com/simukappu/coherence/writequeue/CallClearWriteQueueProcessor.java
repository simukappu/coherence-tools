package test.com.simukappu.coherence.writequeue;

import com.simukappu.coherence.writequeue.ClearWriteQueueProcessor;

/**
 * Test class to start cache server.
 */
public class CallClearWriteQueueProcessor {

	/**
	 * Test main method to call
	 * com.simukappu.coherence.writequeue.ClearWriteQueueProcessor.main.
	 */
	public static void main(String[] args) {
		ClearWriteQueueProcessor
				.main(new String[] { "ThrowExceptionCacheStoreCache" });
	}

}
