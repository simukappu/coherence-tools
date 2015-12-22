package test.com.simukappu.coherence.cachestore;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tangosol.net.cache.CacheStore;

/**
 * CacheStore implementation without connection to Database.
 */
public class NoConnectionCacheStore implements CacheStore<Object, Object> {

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
	public void storeAll(Map<?, ?> oEntry) {
		LOGGER.info(this.getClass().getName() + "#storeAll is called");
		oEntry.entrySet().forEach(
				entry -> {
					Object oKey = entry.getKey();
					Object oValue = entry.getValue();
					LOGGER.info(" key=" + oKey.toString() + ", value="
							+ oValue.toString());
				});
	}

	// --- Read ---
	@Override
	public Object load(Object oKey) {
		LOGGER.info(this.getClass().getName() + "#load is called");
		LOGGER.info(" key=" + oKey.toString());
		return null;
	}

	@Override
	public Map<Object, Object> loadAll(Collection<?> oKeys) {
		LOGGER.info(this.getClass().getName() + "#loadAll is called");
		oKeys.forEach(oKey -> {
			LOGGER.info(" key=" + oKey.toString());
		});
		return null;
	}

	// --- Remove ---
	@Override
	public void erase(Object oKey) {
		LOGGER.info(this.getClass().getName() + "#erase is called");
		LOGGER.info(" key=" + oKey.toString());
	}

	@Override
	public void eraseAll(Collection<?> oKeys) {
		LOGGER.info(this.getClass().getName() + "#eraseAll is called");
		oKeys.forEach(oKey -> {
			LOGGER.info(" key=" + oKey.toString());
		});
	}

}
