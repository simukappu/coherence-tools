package test.com.simukappu.coherence.distributed.fifo;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simukappu.coherence.distributed.fifo.FifoDistributedBiConsumer;
import com.simukappu.coherence.distributed.fifo.FifoDistributedBiFunction;
import com.simukappu.coherence.distributed.fifo.FifoDistributedConsumer;
import com.simukappu.coherence.distributed.fifo.FifoDistributedFunction;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.TypeAssertion;
import com.tangosol.util.InvocableMap.EntryProcessor;

/**
 * Test class for FIFO distributed processing tools.
 * 
 * @author Shota Yamazaki
 */
public class TestFifoDistributedProcessor {

	/*
	 * Test parameters
	 */
	private static final int INIT_TEST_DATA = 1;
	private static final int NUM_TEST_DATA = 20;
	private static final int PROCESSING_COUNT_UP_VALUE = 20;
	private static final int NUM_THREAD = 10;

	/*
	 * Test data
	 */
	private static final Integer EXPECTED_RESULT_DATA = INIT_TEST_DATA + PROCESSING_COUNT_UP_VALUE;
	private static final List<Integer> TEST_DATA_LIST = IntStream.range(INIT_TEST_DATA, INIT_TEST_DATA + NUM_TEST_DATA)
			.boxed().collect(Collectors.toList());
	private static final List<Integer> EXPECTED_RESULT_DATA_LIST = IntStream
			.range(INIT_TEST_DATA + PROCESSING_COUNT_UP_VALUE,
					INIT_TEST_DATA + NUM_TEST_DATA + PROCESSING_COUNT_UP_VALUE)
			.boxed().collect(Collectors.toList());

	/*
	 * Cache names used in tests
	 */
	private static final String DISTRIBUTED_PROCESSING_CACHE_NAME = "DistributedProcessingCache";
	private static final String PROCESSING_COUNT_CACHE_NAME = "ProcessingCountCache";
	private static final String PROCESSING_THREAD_CACHE_NAME = "ProcessingThreadCache";

	/*
	 * NamedCache and EntryProcessor for processing count used in tests
	 */
	private NamedCache<Integer, Integer> distributedProcessingCache = null;
	private NamedCache<Integer, Integer> processingCountCache = null;
	private NamedCache<Integer, String> processingThreadCache = null;
	private EntryProcessor<Integer, Integer, Integer> countProcessor = e -> {
		if (e.getValue() == null) {
			e.setValue(1);
		} else {
			e.setValue(e.getValue() + 1);
		}
		return e.getValue();
	};

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
		distributedProcessingCache = CacheFactory.getTypedCache(DISTRIBUTED_PROCESSING_CACHE_NAME,
				TypeAssertion.withTypes(Integer.class, Integer.class));
		distributedProcessingCache.truncate();
		processingCountCache = CacheFactory.getTypedCache(PROCESSING_COUNT_CACHE_NAME,
				TypeAssertion.withTypes(Integer.class, Integer.class));
		processingCountCache.truncate();
		processingThreadCache = CacheFactory.getTypedCache(PROCESSING_THREAD_CACHE_NAME,
				TypeAssertion.withTypes(Integer.class, String.class));
		processingThreadCache.truncate();
	}

	/**
	 * Test method for EntryProcessor to count called processing which is used
	 * in tests.
	 */
	@Test
	public void testCountProcessor() {
		startTest("testCountProcessor", TEST_DATA_LIST);

		System.out.println(" first processing...");
		TEST_DATA_LIST.forEach(i -> processingCountCache.invoke(i, countProcessor));

		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": " + processingCountCache.size());
		assertEquals(TEST_DATA_LIST.size(), processingCountCache.size());
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": " + new HashMap<>(processingCountCache));

		System.out.println(" second processing...");
		TEST_DATA_LIST.forEach(i -> processingCountCache.invoke(i, countProcessor));

		checkProcessingCount(TEST_DATA_LIST.size(), 2);

		System.out.println("end of the test");
		System.out.println();
	}

	/**
	 * Test method for FifoDistributedConsumer.<br>
	 * This method calls accept method only one time.
	 */
	@Test
	public void testFifoDistributedConsumer() {
		startTest("testFifoDistributedConsumer", INIT_TEST_DATA);

		System.out.println(" processing...");
		FifoDistributedConsumer<Integer> fifoDistributedConsumer = new FifoDistributedConsumer<Integer>(
				"DistributedProcessingCache") {
			@Override
			public void process(Integer i) {
				processingCountCache.invoke(i, countProcessor);
				processingThreadCache.put(i, Thread.currentThread().getName());
			}
		};
		fifoDistributedConsumer.accept(INIT_TEST_DATA);

		checkProcessingCount(1, 1);
		finishTest(1, false);
	}

	/**
	 * Test method for FifoDistributedBiConsumer.<br>
	 * This method calls accept method only one time.
	 */
	@Test
	public void testFifoDistributedBiConsumer() {
		startTest("testFifoDistributedBiConsumer", INIT_TEST_DATA);

		System.out.println(" processing...");
		FifoDistributedBiConsumer<Integer> fifoDistributedConsumer = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
		};
		fifoDistributedConsumer.accept(INIT_TEST_DATA, "DistributedProcessingCache");

		checkProcessingCount(1, 1);
		finishTest(1, false);
	}

	/**
	 * Test method for FifoDistributedFunction.<br>
	 * This method calls apply method only one time.
	 */
	@Test
	public void testFifoDistributedFunction() {
		startTest("testFifoDistributedFunction", INIT_TEST_DATA);

		System.out.println(" processing...");
		FifoDistributedFunction<Integer, Integer> fifoDistributedFunction = new FifoDistributedFunction<Integer, Integer>(
				"DistributedProcessingCache") {
			@Override
			public Integer process(Integer i) {
				processingCountCache.invoke(i, countProcessor);
				processingThreadCache.put(i, Thread.currentThread().getName());
				return i + PROCESSING_COUNT_UP_VALUE;
			}
		};
		Integer resultData = fifoDistributedFunction.apply(INIT_TEST_DATA);
		System.out.println("  processed result data: " + resultData);
		assertEquals(EXPECTED_RESULT_DATA, resultData);

		checkProcessingCount(1, 1);
		finishTest(1, false);
	}

	/**
	 * Test method for FifoDistributedBiFunction.<br>
	 * This method calls apply method only one time.
	 */
	@Test
	public void testFifoDistributedBiFunction() {
		startTest("testFifoDistributedBiFunction", INIT_TEST_DATA);

		System.out.println(" processing...");
		FifoDistributedBiFunction<Integer, Integer> fifoDistributedBiFunction = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
			return i + PROCESSING_COUNT_UP_VALUE;
		};
		Integer resultData = fifoDistributedBiFunction.apply(INIT_TEST_DATA, "DistributedProcessingCache");
		System.out.println("  processed result data: " + resultData);
		assertEquals(EXPECTED_RESULT_DATA, resultData);

		checkProcessingCount(1, 1);
		finishTest(1, false);
	}

	/**
	 * Test method for FifoDistributedConsumer in single processing.<br>
	 * This method calls FifoDistributedConsumer from stream API and processes
	 * them in single thread.
	 */
	@Test
	public void singleThreadTestFifoDistributedConsumer() {
		startTest("singleThreadTestFifoDistributedConsumer", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		TEST_DATA_LIST.forEach(new FifoDistributedConsumer<Integer>("DistributedProcessingCache") {
			@Override
			public void process(Integer i) {
				processingCountCache.invoke(i, countProcessor);
				processingThreadCache.put(i, Thread.currentThread().getName());
			}
		});

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), false);
	}

	/**
	 * Test method for FifoDistributedConsumer in parallel processing.<br>
	 * This method calls FifoDistributedConsumer from parallel stream API and
	 * processes them in multi thread.
	 */
	@Test
	public void parallelTestFifoDistributedConsumer() {
		startTest("parallelTestFifoDistributedConsumer", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		TEST_DATA_LIST.parallelStream().forEach(new FifoDistributedConsumer<Integer>("DistributedProcessingCache") {
			@Override
			public void process(Integer i) {
				processingCountCache.invoke(i, countProcessor);
				processingThreadCache.put(i, Thread.currentThread().getName());
			}
		});

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), true);
	}

	/**
	 * Test method for FifoDistributedConsumer as distributed processing in
	 * multi processes.<br>
	 * This method calls FifoDistributedConsumer for all test data set from
	 * several threads and processes them independently.
	 */
	@Test
	public void multiThreadTestFifoDistributedConsumer() {
		startTest("multiThreadTestFifoDistributedConsumer", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		ExecutorService exec = Executors.newFixedThreadPool(NUM_THREAD);
		List<Future<Integer>> futureList = IntStream.range(0, NUM_THREAD).mapToObj(n -> {
			return exec.submit(() -> {
				TEST_DATA_LIST.stream().forEach(new FifoDistributedConsumer<Integer>("DistributedProcessingCache") {
					@Override
					public void process(Integer i) {
						processingCountCache.invoke(i, countProcessor);
						processingThreadCache.put(i, Thread.currentThread().getName());
					}
				});
				return n;
			});
		}).collect(Collectors.toList());
		futureList.forEach(f -> {
			try {
				f.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), true);
	}

	/**
	 * Test method for FifoDistributedBiConsumer in single processing.<br>
	 * This method calls FifoDistributedBiConsumer from stream API and processes
	 * them in single thread.
	 */
	@Test
	public void singleThreadTestFifoDistributedBiConsumer() {
		startTest("singleThreadTestFifoDistributedBiConsumer", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		FifoDistributedBiConsumer<Integer> fifoDistributedBiConsumer = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
		};
		TEST_DATA_LIST.stream().collect(Collectors.toMap(Function.identity(), i -> "DistributedProcessingCache"))
				.forEach(fifoDistributedBiConsumer);

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), false);
	}

	/**
	 * Test method for FifoDistributedBiConsumer in parallel processing.<br>
	 * This method calls FifoDistributedBiConsumer from parallel stream API and
	 * processes them in multi thread.
	 */
	@Test
	public void parallelTestFifoDistributedBiConsumer() {
		startTest("parallelTestFifoDistributedBiConsumer", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		FifoDistributedBiConsumer<Integer> fifoDistributedBiConsumer = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
		};
		TEST_DATA_LIST.stream()
				.collect(Collectors.toConcurrentMap(Function.identity(), i -> "DistributedProcessingCache")).entrySet()
				.parallelStream().forEach(e -> {
					fifoDistributedBiConsumer.accept(e.getKey(), e.getValue());
				});

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), true);
	}

	/**
	 * Test method for FifoDistributedBiConsumer as distributed processing in
	 * multi processes.<br>
	 * This method calls FifoDistributedBiConsumer for all test data set from
	 * several threads and processes them independently.
	 */
	@Test
	public void multiThreadTestFifoDistributedBiConsumer() {
		startTest("multiThreadTestFifoDistributedBiConsumer", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		ExecutorService exec = Executors.newFixedThreadPool(NUM_THREAD);
		List<Future<Integer>> futureList = IntStream.range(0, NUM_THREAD).mapToObj(n -> {
			return exec.submit(() -> {
				FifoDistributedBiConsumer<Integer> fifoDistributedBiConsumer = i -> {
					processingCountCache.invoke(i, countProcessor);
					processingThreadCache.put(i, Thread.currentThread().getName());
				};
				TEST_DATA_LIST.stream()
						.collect(Collectors.toConcurrentMap(Function.identity(), i -> "DistributedProcessingCache"))
						.forEach(fifoDistributedBiConsumer);
				return n;
			});
		}).collect(Collectors.toList());
		futureList.forEach(f -> {
			try {
				f.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), true);
	}

	/**
	 * Test method for FifoDistributedFunction in single processing.<br>
	 * This method calls FifoDistributedFunction from stream API and processes
	 * them in single thread.
	 */
	@Test
	public void singleThreadTestFifoDistributedFunction() {
		startTest("singleThreadTestFifoDistributedFunction", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		List<Integer> resultDataList = TEST_DATA_LIST.stream()
				.map(new FifoDistributedFunction<Integer, Integer>("DistributedProcessingCache") {
					@Override
					public Integer process(Integer i) {
						processingCountCache.invoke(i, countProcessor);
						processingThreadCache.put(i, Thread.currentThread().getName());
						return i + PROCESSING_COUNT_UP_VALUE;
					}
				}).collect(Collectors.toList());
		System.out.println("  processed result data: " + resultDataList);
		assertEquals(EXPECTED_RESULT_DATA_LIST, resultDataList);

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), false);
	}

	/**
	 * Test method for FifoDistributedFunction in parallel processing.<br>
	 * This method calls FifoDistributedFunction from parallel stream API and
	 * processes them in multi thread.
	 */
	@Test
	public void parallelTestFifoDistributedFunction() {
		startTest("parallelTestFifoDistributedFunction", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		List<Integer> resultDataList = TEST_DATA_LIST.parallelStream()
				.map(new FifoDistributedFunction<Integer, Integer>("DistributedProcessingCache") {
					@Override
					public Integer process(Integer i) {
						processingCountCache.invoke(i, countProcessor);
						processingThreadCache.put(i, Thread.currentThread().getName());
						return i + PROCESSING_COUNT_UP_VALUE;
					}
				}).collect(Collectors.toList());
		System.out.println("  processed result data: " + resultDataList);
		assertEquals(EXPECTED_RESULT_DATA_LIST, resultDataList);

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), true);
	}

	/**
	 * Test method for FifoDistributedFunction as distributed processing in
	 * multi processes.<br>
	 * This method calls FifoDistributedFunction for all test data set from
	 * several threads and processes them independently.
	 */
	@Test
	public void multiThreadTestFifoDistributedFunction() {
		startTest("multiThreadTestFifoDistributedFunction", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		ExecutorService exec = Executors.newFixedThreadPool(NUM_THREAD);
		Map<Integer, String> execThreadNameMap = new ConcurrentHashMap<>();
		List<Future<List<Integer>>> futureList = IntStream.range(0, NUM_THREAD).mapToObj(n -> {
			return exec.submit(() -> {
				execThreadNameMap.put(n, Thread.currentThread().getName());
				return TEST_DATA_LIST.stream()
						.map(new FifoDistributedFunction<Integer, Integer>("DistributedProcessingCache") {
							@Override
							public Integer process(Integer i) {
								processingCountCache.invoke(i, countProcessor);
								processingThreadCache.put(i, Thread.currentThread().getName());
								return i + PROCESSING_COUNT_UP_VALUE;
							}
						}).collect(Collectors.toList());
			});
		}).collect(Collectors.toList());
		IntStream.range(0, NUM_THREAD).forEach(i -> {
			try {
				List<Integer> execResultDataList = futureList.get(i).get();
				System.out.println(
						"  processed result data list (" + execThreadNameMap.get(i) + "): " + execResultDataList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// Combine results
		List<Integer> resultDataList = new ArrayList<>();
		IntStream.range(0, NUM_TEST_DATA).forEach(i -> {
			futureList.forEach(f -> {
				try {
					Integer result = f.get().get(i);
					if (result != null) {
						resultDataList.add(result);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
		System.out.println("  processed result data list (all): " + resultDataList);
		assertEquals(EXPECTED_RESULT_DATA_LIST, resultDataList);

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), true);
	}

	/**
	 * Test method for FifoDistributedBiFunction in single processing.<br>
	 * This method calls FifoDistributedBiFunction from stream API and processes
	 * them in single thread.
	 */
	@Test
	public void singleThreadTestFifoDistributedBiFunction() {
		startTest("singleThreadTestFifoDistributedBiFunction", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		FifoDistributedBiFunction<Integer, Integer> fifoDistributedBiFunction = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
			return i + PROCESSING_COUNT_UP_VALUE;
		};
		List<Integer> resultDataList = TEST_DATA_LIST.stream()
				.collect(Collectors.toConcurrentMap(Function.identity(), i -> "DistributedProcessingCache")).entrySet()
				.stream().map(e -> {
					return fifoDistributedBiFunction.apply(e.getKey(), e.getValue());
				}).collect(Collectors.toList());
		System.out.println("  processed result data: " + resultDataList);
		assertEquals(EXPECTED_RESULT_DATA_LIST, resultDataList);

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), false);
	}

	/**
	 * Test method for FifoDistributedBiFunction in parallel processing.<br>
	 * This method calls FifoDistributedBiFunction from parallel stream API and
	 * processes them in multi thread.
	 */
	@Test
	public void parallelTestFifoDistributedBiFunction() {
		startTest("parallelTestFifoDistributedBiFunction", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		FifoDistributedBiFunction<Integer, Integer> fifoDistributedBiFunction = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
			return i + PROCESSING_COUNT_UP_VALUE;
		};
		List<Integer> resultDataList = TEST_DATA_LIST.stream()
				.collect(Collectors.toConcurrentMap(Function.identity(), i -> "DistributedProcessingCache")).entrySet()
				.parallelStream().map(e -> {
					return fifoDistributedBiFunction.apply(e.getKey(), e.getValue());
					// Sort result list since parallelStream was called from
					// converted entry set
				}).sorted().collect(Collectors.toList());
		System.out.println("  processed result data: " + resultDataList);
		assertEquals(EXPECTED_RESULT_DATA_LIST, resultDataList);

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), true);
	}

	/**
	 * Test method for FifoDistributedBiFunction as distributed processing in
	 * multi processes.<br>
	 * This method calls FifoDistributedBiFunction for all test data set from
	 * several threads and processes them independently.
	 */
	@Test
	public void multiThreadTestFifoDistributedBiFunction() {
		startTest("multiThreadTestFifoDistributedBiFunction", TEST_DATA_LIST);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		ExecutorService exec = Executors.newFixedThreadPool(NUM_THREAD);
		Map<Integer, String> execThreadNameMap = new ConcurrentHashMap<>();
		FifoDistributedBiFunction<Integer, Integer> fifoDistributedBiFunction = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
			return i + PROCESSING_COUNT_UP_VALUE;
		};
		List<Future<List<Integer>>> futureList = IntStream.range(0, NUM_THREAD).mapToObj(n -> {
			return exec.submit(() -> {
				execThreadNameMap.put(n, Thread.currentThread().getName());
				return TEST_DATA_LIST.stream()
						.collect(Collectors.toConcurrentMap(Function.identity(), i -> "DistributedProcessingCache"))
						.entrySet().stream().map(e -> {
							return fifoDistributedBiFunction.apply(e.getKey(), e.getValue());
						}).collect(Collectors.toList());
			});
		}).collect(Collectors.toList());
		IntStream.range(0, NUM_THREAD).forEach(i -> {
			try {
				List<Integer> execResultDataList = futureList.get(i).get();
				System.out.println(
						"  processed result data list (" + execThreadNameMap.get(i) + "): " + execResultDataList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// Combine results
		List<Integer> resultDataList = new ArrayList<>();
		IntStream.range(0, NUM_TEST_DATA).forEach(i -> {
			futureList.forEach(f -> {
				try {
					Integer result = f.get().get(i);
					if (result != null) {
						resultDataList.add(result);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
		System.out.println("  processed result data list (all): " + resultDataList);
		assertEquals(EXPECTED_RESULT_DATA_LIST, resultDataList);

		checkProcessingCount(TEST_DATA_LIST.size(), 1);
		finishTest(TEST_DATA_LIST.size(), true);
	}

	/**
	 * Initial check for all tests
	 * 
	 * @param testName
	 * @param testData
	 */
	private void startTest(String testName, Object testData) {
		System.out.println(testName);
		System.out.println("  test data: " + testData);
		System.out.println(" before test");
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME + ": " + distributedProcessingCache.size());
		assertEquals(0, distributedProcessingCache.size());
	}

	/**
	 * Check size of processing count count
	 * 
	 * @param expectedProcessingCountCacheSize
	 */
	private void checkProcessingCountCacheSize(int expectedProcessingCountCacheSize) {
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": " + processingCountCache.size());
		assertEquals(expectedProcessingCountCacheSize, processingCountCache.size());
	}

	/**
	 * Check processing count
	 * 
	 * @param expectedProcessingCountCacheSize
	 * @param expectedProcessingCount
	 */
	private void checkProcessingCount(int expectedProcessingCountCacheSize, int expectedProcessingCount) {
		checkProcessingCountCacheSize(expectedProcessingCountCacheSize);
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": " + new HashMap<>(processingCountCache));
		processingCountCache.values().forEach(i -> assertEquals(new Integer(expectedProcessingCount), i));
	}

	/**
	 * Final check for all tests
	 * 
	 * @param expectedDistributedProcessingCacheSize
	 * @param multiTreading
	 */
	private void finishTest(int expectedDistributedProcessingCacheSize, boolean multiTreading) {
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME + ": " + distributedProcessingCache.size());
		assertEquals(expectedDistributedProcessingCacheSize, distributedProcessingCache.size());
		System.out.println("  processing thread names in " + PROCESSING_THREAD_CACHE_NAME + ": "
				+ new HashMap<>(processingThreadCache));
		if (multiTreading) {
			assertThat(new HashSet<String>(processingThreadCache.values()).size(), greaterThanOrEqualTo(1));
			System.out.println("  -> processed by multi thread");
		} else {
			assertThat(new HashSet<String>(processingThreadCache.values()), hasSize(1));
			System.out.println("  -> processed by single thread");
		}
		System.out.println("end of the test");
		System.out.println();
	}
}
