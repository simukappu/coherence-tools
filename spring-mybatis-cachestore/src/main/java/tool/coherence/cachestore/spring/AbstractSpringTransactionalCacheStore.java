package tool.coherence.cachestore.spring;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.transaction.annotation.Transactional;

import com.tangosol.net.cache.AbstractCacheStore;

/**
 * Abstract class of CacheStore integrated with Spring framework.<br>
 * This abstract CacheStore defines storeAll/eraceAll method by abstract
 * write/delete method.<br>
 * This implementation also defines transaction demarcation.<br>
 * 
 * @author Shota Yamazaki
 */
public abstract class AbstractSpringTransactionalCacheStore extends
		AbstractCacheStore {

	public AbstractSpringTransactionalCacheStore() {
		super();
	}

	/**
	 * Abstract method writing data to database.<br>
	 * This method is called from store/storeAll method.
	 * 
	 * @param oKey
	 *            Entry key to write
	 * @param oValue
	 *            Entry value to write
	 */
	abstract protected void write(Object oKey, Object oValue);

	/**
	 * Abstract method deleting data from database.<br>
	 * This method is called from erase/eraseAll method.
	 * 
	 * @param oKey
	 *            Entry key to delete
	 */
	abstract protected void delete(Object oKey);

	/**
	 * Abstract overridden load method in AbstractCacheStore.<br>
	 * 
	 * @param oKey
	 *            Entry key to load
	 * @return Loaded object
	 * @see com.tangosol.net.cache.CacheLoader#load(java.lang.Object)
	 */
	@Override
	abstract public Object load(Object oKey);

	/**
	 * Abstract overridden store method in AbstractCacheStore.<br>
	 * 
	 * @param oKey
	 *            Entry key to store
	 * @param oValue
	 *            Entry value to store
	 * @see com.tangosol.net.cache.CacheStore#store(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	@Transactional
	public void store(Object oKey, Object oValue) {
		write(oKey, oValue);
	}

	/**
	 * Abstract overridden storeAll method in AbstractCacheStore.<br>
	 * 
	 * @param mapEntries
	 *            Map containing entries to store
	 * @see com.tangosol.net.cache.CacheStore#storeAll(java.util.Map)
	 */
	@Override
	@Transactional
	public void storeAll(@SuppressWarnings("rawtypes") Map mapEntries) {
		for (Object oEntry : mapEntries.entrySet()) {
			@SuppressWarnings("rawtypes")
			Entry entry = (Entry) oEntry;
			Object oKey = entry.getKey();
			Object oValue = entry.getValue();
			write(oKey, oValue);
		}
	}

	/**
	 * Abstract overridden erase method in AbstractCacheStore.<br>
	 * 
	 * @param oKey
	 *            Entry key to erase
	 * @see com.tangosol.net.cache.CacheStore#erase(java.lang.Object)
	 */
	@Override
	@Transactional
	public void erase(Object oKey) {
		delete(oKey);
	}

	/**
	 * Abstract overridden eraseAll method in AbstractCacheStore.<br>
	 * 
	 * @param colKeys
	 *            Collection containing entry keys to erase
	 * @see com.tangosol.net.cache.CacheStore#eraseAll(java.util.Collection)
	 */
	@Override
	@Transactional
	public void eraseAll(@SuppressWarnings("rawtypes") Collection colKeys) {
		for (Object oKey : colKeys) {
			delete(oKey);
		}
	}
}
