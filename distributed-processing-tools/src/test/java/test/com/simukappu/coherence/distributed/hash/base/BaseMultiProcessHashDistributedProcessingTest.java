package test.com.simukappu.coherence.distributed.hash.base;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;

import com.simukappu.coherence.distributed.hash.DistributedMemberUtil;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.TypeAssertion;

import test.com.simukappu.coherence.entity.TestEntity;

/**
 * Abstract test base class for hash distributed processing tools running on
 * multi processes.
 * 
 * @author Shota Yamazaki
 */
public abstract class BaseMultiProcessHashDistributedProcessingTest extends BaseHashDistributedProcessingTest {

	/*
	 * Test parameters
	 */
	private static final long WAITING_PROCESS_INTERVAL = 1000L;

	/**
	 * Wait for other process tests and shutdown cluster to finish tests
	 */
	@AfterClass
	public static void shutdownCluster() {
		NamedCache<TestEntity, Integer> testEntityProcessingCountCache = CacheFactory.getTypedCache(
				TEST_ENTITY_PROCESSING_COUNT_CACHE_NAME, TypeAssertion.withTypes(TestEntity.class, Integer.class));
		while (testEntityProcessingCountCache.size() < TEST_ENTITIES_MAP.size()) {
			System.out.println("wait for other processes tests...");
			System.out.println(" size of processing count cache: " + testEntityProcessingCountCache.size());
			try {
				Thread.sleep(WAITING_PROCESS_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		assertEquals(TEST_ENTITIES_MAP.size(), testEntityProcessingCountCache.size());
		try {
			Thread.sleep(WAITING_PROCESS_INTERVAL);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize test environment.<br>
	 * Get named caches used in tests.<br>
	 * Wait for other processes until the number of same role member processes
	 * goes enough.
	 */
	@Before
	public void initializeTest() {
		testEntityPoolCache = CacheFactory.getTypedCache(TEST_ENTITY_POOL_CACHE_NAME,
				TypeAssertion.withTypes(TestEntity.class, TestEntity.class));
		TEST_ENTITIES_MAP.entrySet().forEach(e -> {
			TestEntity key = e.getKey().clone();
			TestEntity value = e.getValue().clone();
			testEntityPoolCache.put(key, value);
		});

		boolean processesReady = false;
		while (!processesReady) {
			int numSameRoleMember = DistributedMemberUtil.getSameRoleMembers().size();
			if (numSameRoleMember < NUM_PROCESSES) {
				System.out.println("wait for other processes (" + numSameRoleMember + "/" + NUM_PROCESSES + ")...");
			} else {
				System.out.println("processes ready (" + numSameRoleMember + "/" + NUM_PROCESSES + ")");
				processesReady = true;
			}
			try {
				Thread.sleep(WAITING_PROCESS_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		testEntityProcessingCountCache = CacheFactory.getTypedCache(TEST_ENTITY_PROCESSING_COUNT_CACHE_NAME,
				TypeAssertion.withTypes(TestEntity.class, Integer.class));
	}

}
