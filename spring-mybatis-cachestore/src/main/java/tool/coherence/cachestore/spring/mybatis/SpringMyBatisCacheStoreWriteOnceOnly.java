package tool.coherence.cachestore.spring.mybatis;

/**
 * CacheStore implementation class integrated with Spring and MyBatis framework.<br>
 * This CacheStore is able to only write once (insert only) to database through
 * Coherence cache, not update, load or delete.<br>
 * This CacheStore has higher performance in insert only architecture than
 * SpringMyBatisCacheStoreWriteOnly.
 * 
 * @author Shota Yamazaki
 */
public class SpringMyBatisCacheStoreWriteOnceOnly extends
		SpringMyBatisCacheStoreWriteOnly {

	@Override
	protected final void write(Object oKey, Object oValue) {
		sqlSession.insert(insertSql, oValue);
	};

}
