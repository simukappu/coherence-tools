package test.com.simukappu.coherence.distributed.hash;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simukappu.coherence.distributed.hash.HashModExtractor;
import com.simukappu.coherence.distributed.hash.ReflectionHashModExtractor;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.cache.TypeAssertion;
import com.tangosol.util.Filter;
import com.tangosol.util.extractor.AbstractExtractor;
import com.tangosol.util.filter.EqualsFilter;

import test.com.simukappu.coherence.distributed.hash.base.BaseHashDistributedProcessingTest;
import test.com.simukappu.coherence.entity.TestEntity;

/**
 * Test class for hash distributed processing tools running on single process.
 * 
 * @author Shota Yamazaki
 */
public class TestSingleProcessHashDistributedProcessing extends BaseHashDistributedProcessingTest {

	/**
	 * Ensure cluster to initialize tests
	 */
	@BeforeClass
	public static void ensureCluster() {
		CacheFactory.ensureCluster();
	}

	/**
	 * Initialize test environment<br>
	 * Get named caches used in tests
	 */
	@Before
	public void initializeTest() {
		testEntityPoolCache = CacheFactory.getTypedCache(TEST_ENTITY_POOL_CACHE_NAME,
				TypeAssertion.withTypes(TestEntity.class, TestEntity.class));
		testEntityPoolCache.truncate();
		TEST_ENTITIES_MAP.entrySet().forEach(e -> {
			TestEntity key = e.getKey().clone();
			TestEntity value = e.getValue().clone();
			testEntityPoolCache.put(key, value);
		});
		testEntityProcessingCountCache = CacheFactory.getTypedCache(TEST_ENTITY_PROCESSING_COUNT_CACHE_NAME,
				TypeAssertion.withTypes(TestEntity.class, Integer.class));
		testEntityProcessingCountCache.truncate();
	}

	/**
	 * Test method for HashModExtractor as key extractor.<br>
	 * This method works on single process.
	 */
	@Test
	public void testHashModExtractorForKey() {
		startTest("testHashModExtractorForKey");

		Map<TestEntity, TestEntity> resultDataMap = new HashMap<>();
		IntStream.range(0, NUM_PROCESSES).forEach(targetModulo -> {
			System.out.println(" filtering: targetModulo=" + targetModulo + ", numProcesses=" + NUM_PROCESSES);

			Filter<TestEntity> hashModFilter = new EqualsFilter<TestEntity, Integer>(
					new HashModExtractor<TestEntity>(AbstractExtractor.KEY, NUM_PROCESSES), targetModulo);
			Set<TestEntity> targetKeys = testEntityPoolCache.keySet(hashModFilter);
			Map<TestEntity, TestEntity> filteredDataMap = testEntityPoolCache.getAll(targetKeys);
			resultDataMap.putAll(filteredDataMap);
			targetKeys.forEach(key -> testEntityProcessingCountCache.invoke(key, countProcessor));

			checkFilteredResults(targetKeys, filteredDataMap);
		});

		finishTest(resultDataMap, false);
	}

	/**
	 * Test method for ReflectionHashModExtractor as key extractor.<br>
	 * This method works on single process.
	 */
	@Test
	public void testReflectionHashModExtractorForKey() {
		startTest("testReflectionHashModExtractorForKey");

		Map<TestEntity, TestEntity> resultDataMap = new HashMap<>();
		IntStream.range(0, NUM_PROCESSES).forEach(targetModulo -> {
			System.out.println(" filtering: targetModulo=" + targetModulo + ", numProcesses=" + NUM_PROCESSES);

			Filter<TestEntity> reflectionHashModFilter = new EqualsFilter<TestEntity, Integer>(
					new ReflectionHashModExtractor<TestEntity>("getIntId", null, AbstractExtractor.KEY, NUM_PROCESSES),
					targetModulo);
			Set<TestEntity> targetKeys = testEntityPoolCache.keySet(reflectionHashModFilter);
			Map<TestEntity, TestEntity> filteredDataMap = testEntityPoolCache.getAll(targetKeys);
			resultDataMap.putAll(filteredDataMap);
			targetKeys.forEach(key -> testEntityProcessingCountCache.invoke(key, countProcessor));

			checkFilteredResults(targetKeys, filteredDataMap);
		});

		finishTest(resultDataMap, false);
	}

	/**
	 * Test method for HashModExtractor as value extractor.<br>
	 * This method works on single process.
	 */
	@Test
	public void testHashModExtractorForValue() {
		startTest("testHashModExtractorForValue");

		Map<TestEntity, TestEntity> resultDataMap = new HashMap<>();
		IntStream.range(0, NUM_PROCESSES).forEach(targetModulo -> {
			System.out.println(" filtering: targetModulo=" + targetModulo + ", numProcesses=" + NUM_PROCESSES);

			Filter<TestEntity> hashModFilter = new EqualsFilter<TestEntity, Integer>(
					new HashModExtractor<TestEntity>(AbstractExtractor.VALUE, NUM_PROCESSES), targetModulo);
			Set<TestEntity> targetKeys = testEntityPoolCache.keySet(hashModFilter);
			Map<TestEntity, TestEntity> filteredDataMap = testEntityPoolCache.getAll(targetKeys);
			resultDataMap.putAll(filteredDataMap);
			targetKeys.forEach(key -> testEntityProcessingCountCache.invoke(key, countProcessor));

			checkFilteredResults(targetKeys, filteredDataMap);
		});

		finishTest(resultDataMap, false);
	}

	/**
	 * Test method for ReflectionHashModExtractor as value extractor.<br>
	 * This method works on single process.
	 */
	@Test
	public void testReflectionHashModExtractorForValue() {
		startTest("testReflectionHashModExtractorForValue");

		Map<TestEntity, TestEntity> resultDataMap = new HashMap<>();
		IntStream.range(0, NUM_PROCESSES).forEach(targetModulo -> {
			System.out.println(" filtering: targetModulo=" + targetModulo + ", numProcesses=" + NUM_PROCESSES);

			Filter<TestEntity> reflectionHashModFilter = new EqualsFilter<TestEntity, Integer>(
					new ReflectionHashModExtractor<TestEntity>("getIntId", null, AbstractExtractor.VALUE,
							NUM_PROCESSES),
					targetModulo);
			Set<TestEntity> targetKeys = testEntityPoolCache.keySet(reflectionHashModFilter);
			Map<TestEntity, TestEntity> filteredDataMap = testEntityPoolCache.getAll(targetKeys);
			resultDataMap.putAll(filteredDataMap);
			targetKeys.forEach(key -> testEntityProcessingCountCache.invoke(key, countProcessor));

			checkFilteredResults(targetKeys, filteredDataMap);
		});

		finishTest(resultDataMap, false);
	}

}
