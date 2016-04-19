package com.simukappu.coherence.cachestore.spring;

import java.util.Collection;
import java.util.Map;

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
		AbstractCacheStore<Object, Object> {

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
	protected abstract void write(Object oKey, Object oValue);

	/**
	 * Abstract method deleting data from database.<br>
	 * This method is called from erase/eraseAll method.
	 * 
	 * @param oKey
	 *            Entry key to delete
	 */
	protected abstract void delete(Object oKey);

	/**
	 * Abstract overridden load method in AbstractCacheStore.<br>
	 * 
	 * @param oKey
	 *            Entry key to load
	 * @return Loaded object
	 * @see com.tangosol.net.cache.CacheLoader#load(java.lang.Object)
	 */
	@Override
	public abstract Object load(Object oKey);

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
	public void storeAll(Map<? extends Object, ? extends Object> mapEntries) {
		mapEntries.entrySet().forEach(entry -> {
			Object oKey = entry.getKey();
			Object oValue = entry.getValue();
			write(oKey, oValue);
		});
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
	public void eraseAll(Collection<? extends Object> colKeys) {
		colKeys.forEach(oKey -> {
			delete(oKey);
		});
	}
}
