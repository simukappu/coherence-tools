package test.com.simukappu.coherence.distributed.hash;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tangosol.net.NamedCache;
import com.tangosol.util.InvocableMap.EntryProcessor;

import test.com.simukappu.coherence.entity.TestEntity;

/**
 * Abstract test base class for hash distributed processing tools.
 * 
 * @author Shota Yamazaki
 */
public abstract class BaseHashDistributedProcessingTest {

	/*
	 * Test parameters
	 */
	protected static final int NUM_PROCESSES = 3;

	/*
	 * Cache names used in tests
	 */
	protected static final String TEST_ENTITY_POOL_CACHE_NAME = "TestEntityPoolCache";
	protected static final String TEST_ENTITY_PROCESSING_COUNT_CACHE_NAME = "TestEntityProcessingCountCache";

	/**
	 * Map of test data
	 */
	protected static final Map<TestEntity, TestEntity> TEST_ENTITIES_MAP = new HashMap<TestEntity, TestEntity>() {
		private static final long serialVersionUID = 1L;
		{
			put(new TestEntity(1, 1.0, "id-1"), new TestEntity(11, 11.0,
					"id-11"));
			put(new TestEntity(2, 2.0, "id-2"), new TestEntity(12, 12.0,
					"id-12"));
			put(new TestEntity(3, 3.0, "id-3"), new TestEntity(13, 13.0,
					"id-13"));
			put(new TestEntity(4, 4.0, "id-4"), new TestEntity(14, 14.0,
					"id-14"));
			put(new TestEntity(5, 5.0, "id-5"), new TestEntity(15, 15.0,
					"id-15"));
		}
	};

	/*
	 * NamedCache and EntryProcessor for processing count used in tests
	 */
	protected NamedCache<TestEntity, TestEntity> testEntityPoolCache = null;
	protected NamedCache<TestEntity, Integer> testEntityProcessingCountCache = null;
	protected EntryProcessor<TestEntity, Integer, Integer> countProcessor = e -> {
		if (e.getValue() == null) {
			e.setValue(1);
		} else {
			e.setValue(e.getValue() + 1);
		}
		return e.getValue();
	};

	/**
	 * Initial check for all tests
	 * 
	 * @param testName
	 *            Name of the test
	 */
	protected void startTest(String testName) {
		System.out.println(testName);
		System.out.println("  test data: " + TEST_ENTITIES_MAP);
		System.out.println("  size of " + TEST_ENTITY_POOL_CACHE_NAME + ": "
				+ testEntityPoolCache.size());
		assertEquals(TEST_ENTITIES_MAP.size(), testEntityPoolCache.size());
		System.out.println("  size of "
				+ TEST_ENTITY_PROCESSING_COUNT_CACHE_NAME + ": "
				+ testEntityProcessingCountCache.size());
	}

	/**
	 * Check filtered results
	 * 
	 * @param targetKeys
	 *            Filtered key set
	 * @param filteredDataMap
	 *            Filtered data map
	 */
	protected void checkFilteredResults(Set<TestEntity> targetKeys,
			Map<TestEntity, TestEntity> filteredDataMap) {
		System.out.println("  filtered keys: " + new HashSet<>(targetKeys));
		System.out.println("  filtered entries: "
				+ new ArrayList<>(filteredDataMap.values()));
		System.out.println("  number of filtered entries: "
				+ filteredDataMap.size());
		assertThat(filteredDataMap.size(),
				lessThanOrEqualTo(TEST_ENTITIES_MAP.size()));
	}

	/**
	 * Final check for all tests
	 * 
	 * @param resultDataMap
	 *            Result data map
	 * @param multiProcesses
	 *            Whether the test is running on multi processes
	 */
	protected void finishTest(Map<TestEntity, TestEntity> resultDataMap,
			boolean multiProcesses) {
		System.out.println(" combined result data map: " + resultDataMap);
		System.out.println("  size of combined result data map: "
				+ resultDataMap.size());
		if (multiProcesses) {
			assertThat(resultDataMap.size(),
					lessThanOrEqualTo(TEST_ENTITIES_MAP.size()));
			System.out.println("  -> processed by multi processes");
		} else {
			assertThat(resultDataMap.entrySet(),
					hasSize(TEST_ENTITIES_MAP.size()));
			System.out.println("  -> processed by single process");
		}
		resultDataMap.entrySet().forEach(e -> {
			assertEquals(TEST_ENTITIES_MAP.get(e.getKey()), e.getValue());
		});
		System.out.println(" processing count cache: "
				+ new HashMap<>(new HashMap<>(testEntityProcessingCountCache)));
		System.out.println("  size of processing count cache: "
				+ testEntityProcessingCountCache.size());
		testEntityProcessingCountCache.values().forEach(i -> {
			assertEquals(new Integer(1), i);
		});
		System.out.println("  -> all keys have processed only one time");
		System.out.println("end of the test");
		System.out.println();
	}

}
