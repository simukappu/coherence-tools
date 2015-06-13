package tool.coherence.cachestore.spring.mybatis;

/**
 * CacheStore implementation class integrated with Spring and MyBatis framework.<br>
 * This CacheStore is able to only load from database through Coherence cache,
 * not write or delete.<br>
 * 
 * @author Shota Yamazaki
 */
public class SpringMyBatisCacheStoreReadOnly extends SpringMyBatisCacheStore {

	@Override
	protected final void write(Object oKey, Object oValue) {
	}

	@Override
	protected final void delete(Object oKey) {
	}

}
