package test.com.simukappu.coherence.cachestore;

import java.util.Map;

/**
 * CacheStore implementation which always throw exception in store/storeAll method.<br>
 * This CacheStore class can be used to make cache entries stay in write behind queue.
 */
public class ThrowExceptionCacheStore extends NoConnectionCacheStore {

	public ThrowExceptionCacheStore() {
	}

	// --- Write ---
	@Override
	public void store(Object oKey, Object oValue) {
		super.store(oKey, oValue);
		throw new RuntimeException("Intentionally thrown exception in "
				+ this.getClass().getName());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void storeAll(Map oEntry) {
		super.storeAll(oEntry);
		throw new RuntimeException("Intentionally thrown exception in "
				+ this.getClass().getName());
	}

}
