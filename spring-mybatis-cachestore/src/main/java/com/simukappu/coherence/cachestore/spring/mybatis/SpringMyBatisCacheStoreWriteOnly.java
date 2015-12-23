package com.simukappu.coherence.cachestore.spring.mybatis;

/**
 * CacheStore implementation class integrated with Spring and MyBatis framework.<br>
 * This CacheStore is able to only write (insert or update) to database through
 * Coherence cache, not load or delete.<br>
 * 
 * @author Shota Yamazaki
 */
public class SpringMyBatisCacheStoreWriteOnly extends SpringMyBatisCacheStore {

	@Override
	public final Object load(Object oKey) {
		return null;
	}

	@Override
	protected final void delete(Object oKey) {
	}

}
