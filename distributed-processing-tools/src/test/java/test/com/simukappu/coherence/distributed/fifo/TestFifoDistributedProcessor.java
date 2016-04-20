package test.com.simukappu.coherence.distributed.fifo;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
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

import org.junit.AfterClass;
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

	private static final String DISTRIBUTED_PROCESSING_CACHE_NAME = "DistributedProcessingCache";
	private static final String PROCESSING_COUNT_CACHE_NAME = "ProcessingCountCache";
	private static final String PROCESSING_THREAD_CACHE_NAME = "ProcessingThreadCache";

	NamedCache<Integer, Integer> distributedProcessingCache = null;
	NamedCache<Integer, Integer> processingCountCache = null;
	NamedCache<Integer, String> processingThreadCache = null;
	EntryProcessor<Integer, Integer, Integer> countProcessor = e -> {
		if (e.getValue() == null) {
			e.setValue(1);
		} else {
			e.setValue(e.getValue() + 1);
		}
		return e.getValue();
	};
	int initTestData = 1;
	int numTestData = 20;
	int processingCountUp = 20;
	int numThread = 10;

	Integer expectedResultData = initTestData + processingCountUp;
	List<Integer> testDataList = IntStream.range(initTestData, initTestData + numTestData).boxed()
			.collect(Collectors.toList());
	List<Integer> expectedResultDataList = IntStream
			.range(initTestData + processingCountUp, initTestData + numTestData + processingCountUp).boxed()
			.collect(Collectors.toList());

	@BeforeClass
	public static void initializeTests() {
		CacheFactory.ensureCluster();
	}

	@AfterClass
	public static void destroyTests() {
		CacheFactory.shutdown();
	}

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

	@Test
	public void testCountProcessor() {
		startTest("testCountProcessor", testDataList);

		System.out.println(" first processing...");
		testDataList.forEach(i -> processingCountCache.invoke(i, countProcessor));

		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": " + processingCountCache.size());
		assertEquals(testDataList.size(), processingCountCache.size());
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": " + new HashMap<>(processingCountCache));

		System.out.println(" second processing...");
		testDataList.forEach(i -> processingCountCache.invoke(i, countProcessor));

		checkProcessingCount(testDataList.size(), 2);

		System.out.println("end of the test");
		System.out.println();
	}

	@Test
	public void testFifoDistributedConsumer() {
		startTest("testFifoDistributedConsumer", initTestData);

		System.out.println(" processing...");
		FifoDistributedConsumer<Integer> fifoDistributedConsumer = new FifoDistributedConsumer<Integer>(
				"DistributedProcessingCache") {
			@Override
			public void process(Integer i) {
				processingCountCache.invoke(i, countProcessor);
				processingThreadCache.put(i, Thread.currentThread().getName());
			}
		};
		fifoDistributedConsumer.accept(initTestData);

		checkProcessingCount(1, 1);
		finishTest(1, false);
	}

	@Test
	public void testFifoDistributedBiConsumer() {
		startTest("testFifoDistributedBiConsumer", initTestData);

		System.out.println(" processing...");
		FifoDistributedBiConsumer<Integer> fifoDistributedConsumer = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
		};
		fifoDistributedConsumer.accept(initTestData, "DistributedProcessingCache");

		checkProcessingCount(1, 1);
		finishTest(1, false);
	}

	@Test
	public void testFifoDistributedFunction() {
		startTest("testFifoDistributedFunction", initTestData);

		System.out.println(" processing...");
		FifoDistributedFunction<Integer, Integer> fifoDistributedFunction = new FifoDistributedFunction<Integer, Integer>(
				"DistributedProcessingCache") {
			@Override
			public Integer process(Integer i) {
				processingCountCache.invoke(i, countProcessor);
				processingThreadCache.put(i, Thread.currentThread().getName());
				return i + processingCountUp;
			}
		};
		Integer resultData = fifoDistributedFunction.apply(initTestData);
		System.out.println("  processed result data: " + resultData);
		assertEquals(expectedResultData, resultData);

		checkProcessingCount(1, 1);
		finishTest(1, false);
	}

	@Test
	public void testFifoDistributedBiFunction() {
		startTest("testFifoDistributedBiFunction", initTestData);

		System.out.println(" processing...");
		FifoDistributedBiFunction<Integer, Integer> fifoDistributedBiFunction = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
			return i + processingCountUp;
		};
		Integer resultData = fifoDistributedBiFunction.apply(initTestData, "DistributedProcessingCache");
		System.out.println("  processed result data: " + resultData);
		assertEquals(expectedResultData, resultData);

		checkProcessingCount(1, 1);
		finishTest(1, false);
	}

	@Test
	public void singleThreadTestFifoDistributedConsumer() {
		startTest("singleThreadTestFifoDistributedConsumer", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		testDataList.forEach(new FifoDistributedConsumer<Integer>("DistributedProcessingCache") {
			@Override
			public void process(Integer i) {
				processingCountCache.invoke(i, countProcessor);
				processingThreadCache.put(i, Thread.currentThread().getName());
			}
		});

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), false);
	}

	@Test
	public void parallelTestFifoDistributedConsumer() {
		startTest("parallelTestFifoDistributedConsumer", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		testDataList.parallelStream().forEach(new FifoDistributedConsumer<Integer>("DistributedProcessingCache") {
			@Override
			public void process(Integer i) {
				processingCountCache.invoke(i, countProcessor);
				processingThreadCache.put(i, Thread.currentThread().getName());
			}
		});

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), true);
	}

	@Test
	public void multiThreadTestFifoDistributedConsumer() {
		startTest("multiThreadTestFifoDistributedConsumer", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		ExecutorService exec = Executors.newFixedThreadPool(numThread);
		List<Future<Integer>> futureList = IntStream.range(0, numThread).mapToObj(n -> {
			return exec.submit(() -> {
				testDataList.stream().forEach(new FifoDistributedConsumer<Integer>("DistributedProcessingCache") {
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

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), true);
	}

	@Test
	public void singleThreadTestFifoDistributedBiConsumer() {
		startTest("singleThreadTestFifoDistributedBiConsumer", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		FifoDistributedBiConsumer<Integer> fifoDistributedBiConsumer = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
		};
		testDataList.stream().collect(Collectors.toMap(Function.identity(), i -> "DistributedProcessingCache"))
				.forEach(fifoDistributedBiConsumer);

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), false);
	}

	@Test
	public void parallelTestFifoDistributedBiConsumer() {
		startTest("parallelTestFifoDistributedBiConsumer", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		FifoDistributedBiConsumer<Integer> fifoDistributedBiConsumer = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
		};
		testDataList.stream()
				.collect(Collectors.toConcurrentMap(Function.identity(), i -> "DistributedProcessingCache")).entrySet()
				.parallelStream().forEach(e -> {
					fifoDistributedBiConsumer.accept(e.getKey(), e.getValue());
				});

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), true);
	}

	@Test
	public void multiThreadTestFifoDistributedBiConsumer() {
		startTest("multiThreadTestFifoDistributedBiConsumer", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		ExecutorService exec = Executors.newFixedThreadPool(numThread);
		List<Future<Integer>> futureList = IntStream.range(0, numThread).mapToObj(n -> {
			return exec.submit(() -> {
				FifoDistributedBiConsumer<Integer> fifoDistributedBiConsumer = i -> {
					processingCountCache.invoke(i, countProcessor);
					processingThreadCache.put(i, Thread.currentThread().getName());
				};
				testDataList.stream()
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

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), true);
	}

	@Test
	public void singleThreadTestFifoDistributedFunction() {
		startTest("singleThreadTestFifoDistributedFunction", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		List<Integer> resultDataList = testDataList.stream()
				.map(new FifoDistributedFunction<Integer, Integer>("DistributedProcessingCache") {
					@Override
					public Integer process(Integer i) {
						processingCountCache.invoke(i, countProcessor);
						processingThreadCache.put(i, Thread.currentThread().getName());
						return i + processingCountUp;
					}
				}).collect(Collectors.toList());
		System.out.println("  processed result data: " + resultDataList);
		assertEquals(expectedResultDataList, resultDataList);

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), false);
	}

	@Test
	public void parallelTestFifoDistributedFunction() {
		startTest("parallelTestFifoDistributedFunction", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		List<Integer> resultDataList = testDataList.parallelStream()
				.map(new FifoDistributedFunction<Integer, Integer>("DistributedProcessingCache") {
					@Override
					public Integer process(Integer i) {
						processingCountCache.invoke(i, countProcessor);
						processingThreadCache.put(i, Thread.currentThread().getName());
						return i + processingCountUp;
					}
				}).collect(Collectors.toList());
		System.out.println("  processed result data: " + resultDataList);
		assertEquals(expectedResultDataList, resultDataList);

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), true);
	}

	@Test
	public void multiThreadTestFifoDistributedFunction() {
		startTest("multiThreadTestFifoDistributedFunction", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		ExecutorService exec = Executors.newFixedThreadPool(numThread);
		Map<Integer, String> execThreadNameMap = new ConcurrentHashMap<>();
		List<Future<List<Integer>>> futureList = IntStream.range(0, numThread).mapToObj(n -> {
			return exec.submit(() -> {
				execThreadNameMap.put(n, Thread.currentThread().getName());
				return testDataList.stream()
						.map(new FifoDistributedFunction<Integer, Integer>("DistributedProcessingCache") {
							@Override
							public Integer process(Integer i) {
								processingCountCache.invoke(i, countProcessor);
								processingThreadCache.put(i, Thread.currentThread().getName());
								return i + processingCountUp;
							}
						}).collect(Collectors.toList());
			});
		}).collect(Collectors.toList());
		IntStream.range(0, numThread).forEach(i -> {
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
		IntStream.range(0, numTestData).forEach(i -> {
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
		assertEquals(expectedResultDataList, resultDataList);

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), true);
	}

	@Test
	public void singleThreadTestFifoDistributedBiFunction() {
		startTest("singleThreadTestFifoDistributedBiFunction", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		FifoDistributedBiFunction<Integer, Integer> fifoDistributedBiFunction = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
			return i + processingCountUp;
		};
		List<Integer> resultDataList = testDataList.stream()
				.collect(Collectors.toConcurrentMap(Function.identity(), i -> "DistributedProcessingCache")).entrySet()
				.stream().map(e -> {
					return fifoDistributedBiFunction.apply(e.getKey(), e.getValue());
				}).collect(Collectors.toList());
		System.out.println("  processed result data: " + resultDataList);
		assertEquals(expectedResultDataList, resultDataList);

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), false);
	}

	@Test
	public void parallelTestFifoDistributedBiFunction() {
		startTest("parallelTestFifoDistributedBiFunction", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		FifoDistributedBiFunction<Integer, Integer> fifoDistributedBiFunction = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
			return i + processingCountUp;
		};
		List<Integer> resultDataList = testDataList.stream()
				.collect(Collectors.toConcurrentMap(Function.identity(), i -> "DistributedProcessingCache")).entrySet()
				.parallelStream().map(e -> {
					return fifoDistributedBiFunction.apply(e.getKey(), e.getValue());
				}).collect(Collectors.toList());
		// Sort result list since it was called to converted entry set
		Collections.sort(resultDataList);
		System.out.println("  processed result data: " + resultDataList);
		assertEquals(expectedResultDataList, resultDataList);

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), true);
	}

	@Test
	public void multiThreadTestFifoDistributedBiFunction() {
		startTest("multiThreadTestFifoDistributedBiFunction", testDataList);
		checkProcessingCountCacheSize(0);

		System.out.println(" processing...");
		ExecutorService exec = Executors.newFixedThreadPool(numThread);
		Map<Integer, String> execThreadNameMap = new ConcurrentHashMap<>();
		FifoDistributedBiFunction<Integer, Integer> fifoDistributedBiFunction = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
			return i + processingCountUp;
		};
		List<Future<List<Integer>>> futureList = IntStream.range(0, numThread).mapToObj(n -> {
			return exec.submit(() -> {
				execThreadNameMap.put(n, Thread.currentThread().getName());
				return testDataList.stream()
						.collect(Collectors.toConcurrentMap(Function.identity(), i -> "DistributedProcessingCache"))
						.entrySet().stream().map(e -> {
							return fifoDistributedBiFunction.apply(e.getKey(), e.getValue());
						}).collect(Collectors.toList());
			});
		}).collect(Collectors.toList());
		IntStream.range(0, numThread).forEach(i -> {
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
		IntStream.range(0, numTestData).forEach(i -> {
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
		assertEquals(expectedResultDataList, resultDataList);

		checkProcessingCount(testDataList.size(), 1);
		finishTest(testDataList.size(), true);
	}

	private void startTest(String testName, Object testData) {
		System.out.println(testName);
		System.out.println("  test data: " + testData);
		System.out.println(" before test");
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME + ": " + distributedProcessingCache.size());
		assertEquals(0, distributedProcessingCache.size());
	}

	private void checkProcessingCountCacheSize(int expectedProcessingCountCacheSize) {
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": " + processingCountCache.size());
		assertEquals(expectedProcessingCountCacheSize, processingCountCache.size());
	}

	private void checkProcessingCount(int expectedProcessingCountCacheSize, int expectedProcessingCount) {
		checkProcessingCountCacheSize(expectedProcessingCountCacheSize);
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": " + new HashMap<>(processingCountCache));
		processingCountCache.values().forEach(i -> assertEquals(new Integer(expectedProcessingCount), i));
	}

	private void finishTest(int expectedDistributedProcessingCacheSize, boolean multiTreading) {
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME + ": " + distributedProcessingCache.size());
		assertEquals(expectedDistributedProcessingCacheSize, distributedProcessingCache.size());
		System.out.println("  processing thread names in " + PROCESSING_THREAD_CACHE_NAME + ": "
				+ new HashMap<>(processingThreadCache));
		if (multiTreading) {
			assertThat(new HashSet<String>(processingThreadCache.values()).size(), greaterThan(1));
			System.out.println("   processed by multi thread");
		} else {
			assertThat(new HashSet<String>(processingThreadCache.values()), hasSize(1));
			System.out.println("  -> processed by single thread");
		}
		System.out.println("end of the test");
		System.out.println();
	}
}
