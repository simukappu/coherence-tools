package test.tool.coherence.cachestore;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tangosol.net.cache.CacheStore;

/**
 * CacheStore implementation without connection to Database.
 */
public class NoConnectionCacheStore implements CacheStore {

	/**
	 * Generated logger from slf4j LoggerFactory
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(NoConnectionCacheStore.class);

	public NoConnectionCacheStore() {
	}

	// --- Write ---
	@Override
	public void store(Object oKey, Object oValue) {
		LOGGER.info(this.getClass().getName() + "#store is called");
		LOGGER.info(" key=" + oKey.toString() + ", value=" + oValue.toString());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void storeAll(Map oEntry) {
		LOGGER.info(this.getClass().getName() + "#storeAll is called");
		for (Object entry : oEntry.entrySet()) {
			Object oKey = ((Entry) entry).getKey();
			Object oValue = ((Entry) entry).getValue();
			LOGGER.info(" key=" + oKey.toString() + ", value="
					+ oValue.toString());
		}
	}

	// --- Read ---
	@Override
	public Object load(Object oKey) {
		LOGGER.info(this.getClass().getName() + "#load is called");
		LOGGER.info(" key=" + oKey.toString());
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map loadAll(Collection oKeys) {
		LOGGER.info(this.getClass().getName() + "#loadAll is called");
		for (Object oKey : oKeys) {
			LOGGER.info(" key=" + oKey.toString());
		}
		return null;
	}

	// --- Remove ---
	@Override
	public void erase(Object oKey) {
		LOGGER.info(this.getClass().getName() + "#erase is called");
		LOGGER.info(" key=" + oKey.toString());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void eraseAll(Collection oKeys) {
		LOGGER.info(this.getClass().getName() + "#eraseAll is called");
		for (Object oKey : oKeys) {
			LOGGER.info(" key=" + oKey.toString());
		}
	}

}
