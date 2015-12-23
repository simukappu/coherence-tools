package com.simukappu.coherence.cachestore.spring.mybatis;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

/**
 * CacheStore implementation class designed to be able to handle relational
 * objects integrated with Spring and MyBatis framework.<br>
 * This CacheStore is able to insert, update, delete, load to/from database
 * through Coherence cache.<br>
 * 
 * @author Shota Yamazaki
 */
public class SpringMyBatisCacheStoreWithChildEntities extends
		SpringMyBatisCacheStore implements InitializingBean {

	// Spring injection variables
	// Setter method is necessary
	protected String selectOneChildSql;
	protected String selectChildenSql;
	protected String insertChildSql;
	protected String deleteChildenSql;
	protected Class<?> entityClass;
	protected String childenField;

	// Protected member variables.
	// Getter and Setter method is not necessary for these variables.
	protected Method childenGetter;
	protected Method childenSetter;

	@Override
	public void afterPropertiesSet() throws Exception {
		PropertyDescriptor propertyDescriptor = new PropertyDescriptor(
				childenField, entityClass);
		childenGetter = propertyDescriptor.getReadMethod();
		childenSetter = propertyDescriptor.getWriteMethod();
	}

	@Override
	public Object load(Object oKey) {
		Object oParent = super.load(oKey);
		List<Object> oChildren = sqlSession.selectList(selectChildenSql, oKey);
		try {
			childenSetter.invoke(oParent, oChildren);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return oParent;
	}

	@Override
	protected void write(Object oKey, Object oValue) {
		super.write(oKey, oValue);
		List<Object> oChildren = sqlSession.selectList(selectChildenSql, oKey);
		if (!oChildren.isEmpty()) {
			// Delete all children
			sqlSession.delete(deleteChildenSql, oKey);
		}
		try {
			@SuppressWarnings("unchecked")
			List<Object> injectedChildren = (List<Object>) childenGetter
					.invoke(oValue);
			oChildren = injectedChildren;
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		oChildren.forEach(oChild -> {
			sqlSession.insert(insertChildSql, oChild);
		});
	}

	@Override
	public void delete(Object oKey) {
		List<Object> oChildren = sqlSession.selectList(selectChildenSql, oKey);
		if (oChildren.size() > 0) {
			// Delete all children
			sqlSession.delete(deleteChildenSql, oKey);
		}
		super.delete(oKey);
	}

	/**
	 * Setter method for SQL in MyBatis mapper to select one child data from
	 * database.<br>
	 * This setter is used for injection by Spring.
	 * 
	 * @param selectOneChildSql
	 *            SQL in MyBatis mapper to select one child data from database
	 */
	public void setSelectOneChildSql(String selectOneChildSql) {
		this.selectOneChildSql = selectOneChildSql;
	}

	/**
	 * Setter method for SQL in MyBatis mapper to select all children data from
	 * database.<br>
	 * This setter is used for injection by Spring.
	 * 
	 * @param selectChildenSql
	 *            SQL in MyBatis mapper to select all children data from
	 *            database
	 */
	public void setSelectChildenSql(String selectChildenSql) {
		this.selectChildenSql = selectChildenSql;
	}

	/**
	 * Setter method for SQL in MyBatis mapper to insert child data into
	 * database.<br>
	 * This setter is used for injection by Spring.
	 * 
	 * @param insertChildSql
	 *            SQL in MyBatis mapper to insert child data into database
	 */
	public void setInsertChildSql(String insertChildSql) {
		this.insertChildSql = insertChildSql;
	}

	/**
	 * Setter method for SQL in MyBatis mapper to delete all children data from
	 * database.<br>
	 * This setter is used for injection by Spring.
	 * 
	 * @param deleteChildenSql
	 *            SQL in MyBatis mapper to delete all children data from
	 *            database
	 */
	public void setDeleteChildenSql(String deleteChildenSql) {
		this.deleteChildenSql = deleteChildenSql;
	}

	/**
	 * Setter method for entry value class stored through this CacheStore.<br>
	 * This setter is used for injection by Spring.
	 * 
	 * @param entityClass
	 *            Entry value class stored through this CacheStore
	 */
	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * Setter method for field name containing children list data in the entry
	 * value class stored through this CacheStore.<br>
	 * This setter is used for injection by Spring.
	 * 
	 * @param childenField
	 *            Field name containing children list data in the entry value
	 *            class stored through this CacheStore
	 */
	public void setChildenField(String childenField) {
		this.childenField = childenField;
	}

}
