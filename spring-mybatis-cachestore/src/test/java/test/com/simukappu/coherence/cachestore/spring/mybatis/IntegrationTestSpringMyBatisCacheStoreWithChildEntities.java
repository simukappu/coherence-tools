package test.com.simukappu.coherence.cachestore.spring.mybatis;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import test.com.simukappu.coherence.entity.TestChildEntity;
import test.com.simukappu.coherence.entity.TestParentEntity;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.TypeAssertion;

/**
 * Integration test class for SpringMyBatisCacheStoreWithChildEntities.
 * 
 * @author Shota Yamazaki
 */
public class IntegrationTestSpringMyBatisCacheStoreWithChildEntities {

	Connection conn = null;
	NamedCache<Integer, TestParentEntity> targetWriteThroughCache = null;
	NamedCache<Integer, TestParentEntity> targetWriteBehindCache = null;

	@SuppressWarnings("serial")
	private static final Map<Integer, TestParentEntity> PARENT_ENTITIES_MAP = new HashMap<Integer, TestParentEntity>() {
		{
			put(0,
					new TestParentEntity(30, "Dell Curry", 50, Arrays.asList(
							new TestChildEntity(30, "Stephen Curry", 27),
							new TestChildEntity(30, "Seth Curry", 24),
							new TestChildEntity(30, "Sydel Curry", 20))));
			put(1,
					new TestParentEntity(43, "Mychal Thompson", 60, Arrays
							.asList(new TestChildEntity(43, "Mychel Thompson",
									27), new TestChildEntity(43,
									"Klay Thompson", 25), new TestChildEntity(
									43, "Trayce Thompson", 24))));
		}
	};

	@Before
	public void initializeTests() {
		// Get database connection
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/spring/datasource-context.xml");
		JdbcTemplate jdbcTemplate = (JdbcTemplate) context
				.getBean("jdbcTemplate");
		conn = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());

		// Get named cache
		targetWriteThroughCache = CacheFactory.getTypedCache(
				"SpringMyBatisCacheStoreWithChildEntitiesCache",
				TypeAssertion.withTypes(Integer.class, TestParentEntity.class));
		targetWriteBehindCache = CacheFactory.getTypedCache(
				"SpringMyBatisCacheStoreWithChildEntitiesWriteBehindCache",
				TypeAssertion.withTypes(Integer.class, TestParentEntity.class));
	}

	@After
	public void destroyTests() throws SQLException {
		// Close connection
		if (conn != null) {
			conn.close();
		}
		CacheFactory.releaseCache(targetWriteThroughCache);
		CacheFactory.releaseCache(targetWriteBehindCache);
	}

	/**
	 * Integration test method for SpringMyBatisCacheStoreWithChildEntities.<br>
	 * This method tests loading data from database to cache.<br>
	 * 
	 * You can run this test as stand-alone or multi-processes cluster by
	 * running CacheServer before this test.<br>
	 * 
	 * @throws SQLException
	 *             Exception in Database connection
	 */
	@Test
	public void integrationTestLoad() throws SQLException {
		System.out.println("Test loading data from database to cache ...");

		// Clear data
		clearData();

		// Test inserting
		System.out.println(" Store object");
		TestParentEntity insertingParent = PARENT_ENTITIES_MAP.get(0).clone();
		targetWriteThroughCache.put(insertingParent.getId(), insertingParent);
		testStoreFromWriteThrough(insertingParent);

		// Test loading
		System.out.println(" Load object from another empty cache");
		// Check if another cache is empty
		assertEquals(0, targetWriteBehindCache.size());
		TestParentEntity cachedParent = (TestParentEntity) targetWriteBehindCache
				.get(insertingParent.getId());
		testStoreFromWriteThrough(cachedParent);
		// Check if another cache is not empty
		assertEquals(1, targetWriteBehindCache.size());

		System.out.println("Successfully done\n");
	}

	/**
	 * Integration test method for SpringMyBatisCacheStoreWithChildEntities.<br>
	 * This method tests inserting data from write through cache.<br>
	 * 
	 * You can run this test as stand-alone or multi-processes cluster by
	 * running CacheServer before this test.<br>
	 * 
	 * @throws SQLException
	 *             Exception in Database connection
	 */
	@Test
	public void integrationTestInsertFromWriteThrough() throws SQLException {
		System.out.println("Test inserting data from write through cache ...");

		// Clear data
		clearData();

		// Test inserting
		System.out.println(" Store object to insert");
		TestParentEntity insertingParent = PARENT_ENTITIES_MAP.get(0).clone();
		targetWriteThroughCache.put(insertingParent.getId(), insertingParent);
		testStoreFromWriteThrough(insertingParent);

		System.out.println("Successfully done\n");
	}

	/**
	 * Integration test method for SpringMyBatisCacheStoreWithChildEntities.<br>
	 * This method tests updating data from write through cache.<br>
	 * 
	 * You can run this test as stand-alone or multi-processes cluster by
	 * running CacheServer before this test.<br>
	 * 
	 * @throws SQLException
	 *             Exception in Database connection
	 */
	@Test
	public void integrationTestUpdateFromWriteThrough() throws SQLException {
		System.out.println("Test updating data from write through cache ...");

		// Clear data
		clearData();

		// Test inserting
		System.out.println(" Store object to insert");
		TestParentEntity insertingParent = PARENT_ENTITIES_MAP.get(0).clone();
		targetWriteThroughCache.put(insertingParent.getId(), insertingParent);
		testStoreFromWriteThrough(insertingParent);

		// Test updating
		System.out.println(" Store object to update");
		TestParentEntity updatingParent = PARENT_ENTITIES_MAP.get(1).clone();
		updatingParent.setId(insertingParent.getId());
		targetWriteThroughCache.put(updatingParent.getId(), updatingParent);
		testStoreFromWriteThrough(updatingParent);

		System.out.println("Successfully done\n");
	}

	/**
	 * Integration test method for SpringMyBatisCacheStoreWithChildEntities.<br>
	 * This method tests deleting data from database through cache.<br>
	 * 
	 * You can run this test as stand-alone or multi-processes cluster by
	 * running CacheServer before this test.<br>
	 * 
	 * @throws SQLException
	 *             Exception in Database connection
	 */
	@Test
	public void integrationTestDelete() throws SQLException {
		System.out
				.println("Test deleting data from database through cache ...");

		// Clear data
		clearData();

		// Test inserting
		System.out.println(" Store object");
		TestParentEntity insertingParent = PARENT_ENTITIES_MAP.get(0).clone();
		targetWriteThroughCache.put(insertingParent.getId(), insertingParent);
		testStoreFromWriteThrough(insertingParent);

		// Test deleting
		System.out.println(" Delete object from cache");
		// Check if cache is not empty
		assertEquals(1, targetWriteThroughCache.size());
		// Remove data from cache
		targetWriteThroughCache.remove(insertingParent.getId());
		// Check if cache is empty
		assertEquals(0, targetWriteThroughCache.size());
		// Check if database is empty
		TestParentEntity storedParent = selectParentEntity(insertingParent
				.getId());
		System.out.println("  Object in cache    : null");
		System.out.println("  Object in database : " + storedParent);
		assertNull(storedParent);

		System.out.println("Successfully done\n");
	}

	/**
	 * Integration test method for SpringMyBatisCacheStoreWithChildEntities.<br>
	 * This method tests inserting data from write behind cache.<br>
	 * 
	 * You can run this test as stand-alone or multi-processes cluster by
	 * running CacheServer before this test.<br>
	 * 
	 * @throws SQLException
	 *             Exception in Database connection
	 */
	@Test
	public void integrationTestInsertFromWriteBehind() throws SQLException {
		System.out.println("Test inserting data from write behind cache ...");

		// Clear data
		clearData();

		// Test inserting
		System.out.println(" Store object to insert");
		TestParentEntity insertingParent1 = PARENT_ENTITIES_MAP.get(0).clone();
		TestParentEntity insertingParent2 = PARENT_ENTITIES_MAP.get(1).clone();
		targetWriteBehindCache.put(insertingParent1.getId(), insertingParent1);
		targetWriteBehindCache.put(insertingParent2.getId(), insertingParent2);
		TestParentEntity storedParent1 = selectParentEntity(insertingParent1
				.getId());
		TestParentEntity storedParent2 = selectParentEntity(insertingParent2
				.getId());
		System.out.println("  Object in cache    : " + insertingParent1);
		System.out.println("  Object in database : " + storedParent1);
		assertNull(storedParent1);
		System.out.println("  ---");
		System.out.println("  Object in cache    : " + insertingParent2);
		System.out.println("  Object in database : " + storedParent2);
		assertNull(storedParent2);
		// Wait for writing delay
		System.out.println(" Write delay");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		testStoreFromWriteThrough(insertingParent1);
		System.out.println("  ---");
		testStoreFromWriteThrough(insertingParent2);

		System.out.println("Successfully done\n");
	}

	/**
	 * Integration test method for SpringMyBatisCacheStoreWithChildEntities.<br>
	 * This method tests inserting data from write behind cache with updating
	 * data in write behind queue.<br>
	 * 
	 * You can run this test as stand-alone or multi-processes cluster by
	 * running CacheServer before this test.<br>
	 * 
	 * @throws SQLException
	 *             Exception in Database connection
	 */
	@Test
	public void integrationTestInsertFromWriteBehindTransactionUpdate()
			throws SQLException {
		System.out
				.println("Test inserting data from write behind cache with updating data in write behind queue ...");

		// Clear data
		clearData();

		// Test inserting with updating data in write behind queue
		System.out
				.println(" Store object to insert and update data in write behind queue");
		TestParentEntity insertingParent1 = PARENT_ENTITIES_MAP.get(0).clone();
		TestParentEntity insertingParent2 = PARENT_ENTITIES_MAP.get(1).clone();
		insertingParent2.setId(insertingParent1.getId());
		// Put two data with same key
		targetWriteBehindCache.put(insertingParent1.getId(), insertingParent1);
		targetWriteBehindCache.put(insertingParent1.getId(), insertingParent2);
		TestParentEntity storedParent1 = selectParentEntity(insertingParent1
				.getId());
		TestParentEntity storedParent2 = selectParentEntity(insertingParent2
				.getId());
		System.out.println("  Object in cache    : " + insertingParent1);
		System.out.println("  Object in database : " + storedParent1);
		assertNull(storedParent1);
		System.out.println("  ---");
		System.out.println("  Object in cache    : " + insertingParent2);
		System.out.println("  Object in database : " + storedParent2);
		assertNull(storedParent2);
		// Wait for writing delay
		System.out.println(" Write delay and later entry will stored");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		testStoreFromWriteThrough(insertingParent2);

		System.out.println("Successfully done\n");
	}

	/**
	 * Integration test method for SpringMyBatisCacheStoreWithChildEntities.<br>
	 * This method tests inserting data from write behind cache with exception
	 * in transaction.<br>
	 * 
	 * You can run this test as stand-alone or multi-processes cluster by
	 * running CacheServer before this test.<br>
	 * 
	 * @throws SQLException
	 *             Exception in Database connection
	 */
	@Test
	public void integrationTestInsertFromWriteBehindTransactionException()
			throws SQLException {
		System.out
				.println("Test inserting data from write behind cache with exception in transaction ...");

		// Clear data
		clearData();

		// Test inserting with exception in transaction
		System.out
				.println(" Store object to insert and exception will be thrown in transaction");
		TestParentEntity insertingParent1 = PARENT_ENTITIES_MAP.get(0).clone();
		TestParentEntity insertingParent2 = PARENT_ENTITIES_MAP.get(1).clone();
		insertingParent2.setId(insertingParent1.getId());
		// Put two data with different key and violate unique constraint
		targetWriteBehindCache.put(PARENT_ENTITIES_MAP.get(0).getId(),
				insertingParent1);
		targetWriteBehindCache.put(PARENT_ENTITIES_MAP.get(1).getId(),
				insertingParent2);
		TestParentEntity storedParent1 = selectParentEntity(insertingParent1
				.getId());
		TestParentEntity storedParent2 = selectParentEntity(insertingParent2
				.getId());
		System.out.println("  Object in cache    : " + insertingParent1);
		System.out.println("  Object in database : " + storedParent1);
		assertNull(storedParent1);
		System.out.println("  ---");
		System.out.println("  Object in cache    : " + insertingParent2);
		System.out.println("  Object in database : " + storedParent2);
		assertNull(storedParent2);
		// Wait for writing delay
		System.out
				.println(" Write delay and no data will stored since exception will be thrown");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		storedParent1 = selectParentEntity(insertingParent1.getId());
		storedParent2 = selectParentEntity(insertingParent2.getId());
		System.out.println("  Object in cache    : " + insertingParent1);
		System.out.println("  Object in database : " + storedParent1);
		assertNull(storedParent1);
		System.out.println("  ---");
		System.out.println("  Object in cache    : " + insertingParent2);
		System.out.println("  Object in database : " + storedParent2);
		assertNull(storedParent2);

		System.out.println("Successfully done\n");
	}

	private void testStoreFromWriteThrough(TestParentEntity parent)
			throws SQLException {
		// Get data from database
		TestParentEntity storedParent = selectParentEntity(parent.getId());
		System.out.println("  Object in cache    : " + parent);
		System.out.println("  Object in database : " + storedParent);

		// Check if the object stored in database equals the object in the cache
		assertEquals(parent, storedParent);
	}

	private void clearData() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("DELETE FROM CHILDREN");
		stmt.executeUpdate("DELETE FROM PARENTS");
		stmt.close();

		targetWriteThroughCache.truncate();
		targetWriteBehindCache.truncate();
	}

	private TestParentEntity selectParentEntity(int id) throws SQLException {
		PreparedStatement parentPstmt = conn
				.prepareStatement("SELECT * FROM PARENTS WHERE ID = ?");
		PreparedStatement childrenPstmt = conn
				.prepareStatement("SELECT * FROM CHILDREN WHERE PARENT_ID = ?");
		parentPstmt.setInt(1, id);
		childrenPstmt.setInt(1, id);

		// Select parent data
		ResultSet rs = parentPstmt.executeQuery();
		if (!rs.next()) {
			return null;
		}
		TestParentEntity parent = new TestParentEntity(rs.getInt(1),
				rs.getString(2), rs.getInt(3));

		// Select children data
		rs = childrenPstmt.executeQuery();
		while (rs.next()) {
			parent.addChild(new TestChildEntity(rs.getInt(1), rs.getString(2),
					rs.getInt(3)));
		}

		return parent;
	}

}
