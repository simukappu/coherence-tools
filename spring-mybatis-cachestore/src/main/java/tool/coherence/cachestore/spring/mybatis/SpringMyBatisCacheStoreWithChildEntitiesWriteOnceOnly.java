package tool.coherence.cachestore.spring.mybatis;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * CacheStore implementation class designed to be able to handle relational
 * objects integrated with Spring and MyBatis framework.<br>
 * This CacheStore is able to only write once (insert only) to database through
 * Coherence cache, not update, load or delete.<br>
 * This CacheStore has higher performance in insert only architecture than
 * SpringMyBatisCacheStoreWithChildEntities.
 * 
 * @author Shota Yamazaki
 */
public class SpringMyBatisCacheStoreWithChildEntitiesWriteOnceOnly extends
		SpringMyBatisCacheStoreWithChildEntities {

	@Override
	public final Object load(Object oKey) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void write(Object oKey, Object oValue) {
		sqlSession.insert(insertSql, oValue);
		List<Object> oChildren = null;
		try {
			oChildren = (List<Object>) childenGetter.invoke(oValue);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		for (Object oChild : oChildren) {
			sqlSession.insert(insertChildSql, oChild);
		}
	}

	@Override
	public final void delete(Object oKey) {
	}

}
