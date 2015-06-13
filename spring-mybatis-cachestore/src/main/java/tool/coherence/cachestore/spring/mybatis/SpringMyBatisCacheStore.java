package tool.coherence.cachestore.spring.mybatis;

import org.apache.ibatis.session.SqlSession;

import tool.coherence.cachestore.spring.AbstractSpringTransactionalCacheStore;

/**
 * CacheStore implementation class integrated with Spring and MyBatis framework.<br>
 * This CacheStore is able to insert, update, delete, load to/from database
 * through Coherence cache.<br>
 * 
 * @author Shota Yamazaki
 */
public class SpringMyBatisCacheStore extends
		AbstractSpringTransactionalCacheStore {

	// Spring injection variables
	// Setter method is necessary
	protected String selectSql;
	protected String insertSql;
	protected String updateSql;
	protected String deleteSql;
	protected SqlSession sqlSession;

	public SpringMyBatisCacheStore() {
		super();
	}

	@Override
	public Object load(Object oKey) {
		return sqlSession.selectOne(selectSql, oKey);
	}

	@Override
	protected void write(Object oKey, Object oValue) {
		if (sqlSession.selectOne(selectSql, oKey) == null) {
			sqlSession.insert(insertSql, oValue);
		} else {
			sqlSession.update(updateSql, oValue);
		}
	}

	@Override
	protected void delete(Object oKey) {
		sqlSession.delete(deleteSql, oKey);
	}

	/**
	 * Setter method for SQL in MyBatis mapper to select data from database.<br>
	 * This setter is used for injection by Spring.
	 * 
	 * @param selectSql
	 *            SQL in MyBatis mapper to select data from database
	 */
	public void setSelectSql(String selectSql) {
		this.selectSql = selectSql;
	}

	/**
	 * Setter method for SQL in MyBatis mapper to insert data into database.<br>
	 * This setter is used for injection by Spring.
	 * 
	 * @param insertSql
	 *            SQL in MyBatis mapper to insert data into database
	 */
	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	/**
	 * Setter method for SQL in MyBatis mapper to update data to database.<br>
	 * This setter is used for injection by Spring.
	 * 
	 * @param updateSql
	 *            SQL in MyBatis mapper to update data to database
	 */
	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}

	/**
	 * Setter method for SQL in MyBatis mapper to delete data from database.<br>
	 * This setter is used for injection by Spring.
	 * 
	 * @param deleteSql
	 *            SQL in MyBatis mapper to delete data from database
	 */
	public void setDeleteSql(String deleteSql) {
		this.deleteSql = deleteSql;
	}

	/**
	 * Setter method for SqlSession used in MyBatis.<br>
	 * This setter is used for injection by Spring.
	 * 
	 * @param sqlSession
	 *            SqlSession used in MyBatis
	 */
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

}
