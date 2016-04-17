package com.simukappu.coherence.distributed;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simukappu.coherence.distributed.fifo.FifoDistributedBiConsumer;
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
	List<Integer> testDataList = IntStream.rangeClosed(1, 10).boxed()
			.collect(Collectors.toList());
	List<Integer> expectedResultDataList = IntStream.rangeClosed(11, 20)
			.boxed().collect(Collectors.toList());

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
		distributedProcessingCache = CacheFactory.getTypedCache(
				DISTRIBUTED_PROCESSING_CACHE_NAME,
				TypeAssertion.withTypes(Integer.class, Integer.class));
		distributedProcessingCache.truncate();
		processingCountCache = CacheFactory.getTypedCache(
				PROCESSING_COUNT_CACHE_NAME,
				TypeAssertion.withTypes(Integer.class, Integer.class));
		processingCountCache.truncate();
		processingThreadCache = CacheFactory.getTypedCache(
				PROCESSING_THREAD_CACHE_NAME,
				TypeAssertion.withTypes(Integer.class, String.class));
		processingThreadCache.truncate();
	}

	@Test
	public void testCountProcessor() {
		System.out.println("testCountProcessor");
		System.out.println("  test data list: " + testDataList);
		System.out.println(" before test");
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(0, processingCountCache.size());

		System.out.println(" first processing...");
		testDataList.forEach(i -> processingCountCache
				.invoke(i, countProcessor));

		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(testDataList.size(), processingCountCache.size());
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ new HashMap<>(processingCountCache));

		System.out.println(" second processing...");
		testDataList.forEach(i -> processingCountCache
				.invoke(i, countProcessor));

		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(testDataList.size(), processingCountCache.size());
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ new HashMap<>(processingCountCache));
		processingCountCache.values().forEach(
				i -> assertEquals(new Integer(2), i));

		System.out.println("end of the test");
		System.out.println();
	}

	@Test
	public void singleThreadTestFifoDistributedConsumer() {
		System.out.println("singleThreadTestFifoDistributedConsumer");
		System.out.println("  test data list: " + testDataList);
		System.out.println(" before test");
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(0, distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(0, processingCountCache.size());

		System.out.println(" processing...");
		testDataList.forEach(new FifoDistributedConsumer<Integer>(
				"DistributedProcessingCache") {
			@Override
			public void process(Integer i) {
				processingCountCache.invoke(i, countProcessor);
				processingThreadCache.put(i, Thread.currentThread().getName());
			}
		});

		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(testDataList.size(), distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(testDataList.size(), processingCountCache.size());
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ new HashMap<>(processingCountCache));
		processingCountCache.values().forEach(
				i -> assertEquals(new Integer(1), i));
		System.out.println("  processing thread names in "
				+ PROCESSING_THREAD_CACHE_NAME + ": "
				+ new HashMap<>(processingThreadCache));
		assertThat(new HashSet<String>(processingThreadCache.values()),
				hasSize(1));

		System.out.println("end of the test");
		System.out.println();
	}

	@Test
	public void singleThreadTestFifoDistributedBiConsumer() {
		System.out.println("singleThreadTestFifoDistributedBiConsumer");
		System.out.println("  test data list: " + testDataList);
		System.out.println(" before test");
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(0, distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(0, processingCountCache.size());

		System.out.println(" processing...");
		FifoDistributedBiConsumer<Integer> fifoDistributedBiConsumer = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
		};
		testDataList
				.stream()
				.collect(
						Collectors.toMap(Function.identity(),
								i -> "DistributedProcessingCache"))
				.forEach(fifoDistributedBiConsumer);

		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(testDataList.size(), distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(testDataList.size(), processingCountCache.size());
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ new HashMap<>(processingCountCache));
		processingCountCache.values().forEach(
				i -> assertEquals(new Integer(1), i));
		System.out.println("  processing thread names in "
				+ PROCESSING_THREAD_CACHE_NAME + ": "
				+ new HashMap<>(processingThreadCache));
		assertThat(new HashSet<String>(processingThreadCache.values()),
				hasSize(1));

		System.out.println("end of the test");
		System.out.println();
	}

	@Test
	public void multiThreadTestFifoDistributedConsumer() {
		System.out.println("multiThreadTestFifoDistributedConsumer");
		System.out.println("  test data list: " + testDataList);
		System.out.println(" before test");
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(0, distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(0, processingCountCache.size());

		System.out.println(" processing...");
		testDataList.parallelStream().forEach(
				new FifoDistributedConsumer<Integer>(
						"DistributedProcessingCache") {
					@Override
					public void process(Integer i) {
						processingCountCache.invoke(i, countProcessor);
						processingThreadCache.put(i, Thread.currentThread()
								.getName());
					}
				});

		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(testDataList.size(), distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(testDataList.size(), processingCountCache.size());
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ new HashMap<>(processingCountCache));
		processingCountCache.values().forEach(
				i -> assertEquals(new Integer(1), i));
		System.out.println("  processing thread names in "
				+ PROCESSING_THREAD_CACHE_NAME + ": "
				+ new HashMap<>(processingThreadCache));
		assertThat(new HashSet<String>(processingThreadCache.values()).size(),
				greaterThan(1));

		System.out.println("end of the test");
		System.out.println();
	}

	@Test
	public void multiThreadTestFifoDistributedBiConsumer() {
		System.out.println("multiThreadTestFifoDistributedBiConsumer");
		System.out.println("  test data list: " + testDataList);
		System.out.println(" before test");
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(0, distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(0, processingCountCache.size());

		System.out.println(" processing...");
		FifoDistributedBiConsumer<Integer> fifoDistributedBiConsumer = i -> {
			processingCountCache.invoke(i, countProcessor);
			processingThreadCache.put(i, Thread.currentThread().getName());
		};
		// TODO make multi thread processing
		testDataList
				.stream()
				.collect(
						Collectors.toConcurrentMap(Function.identity(),
								i -> "DistributedProcessingCache"))
				.forEach(fifoDistributedBiConsumer);

		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(testDataList.size(), distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(testDataList.size(), processingCountCache.size());
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ new HashMap<>(processingCountCache));
		processingCountCache.values().forEach(
				i -> assertEquals(new Integer(1), i));
		System.out.println("  processing thread names in "
				+ PROCESSING_THREAD_CACHE_NAME + ": "
				+ new HashMap<>(processingThreadCache));
		// TODO make multi thread processing
		// assertThat(new
		// HashSet<String>(processingThreadCache.values()).size(),
		// greaterThan(1));

		System.out.println("end of the test");
		System.out.println();
	}

	@Test
	public void singleThreadTestFifoDistributedFunction() {
		System.out.println("singleThreadTestFifoDistributedFunction");
		System.out.println("  test data list: " + testDataList);
		System.out.println(" before test");
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(0, distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(0, processingCountCache.size());

		System.out.println(" processing...");
		List<Integer> resultDataList = testDataList
				.stream()
				.map(new FifoDistributedFunction<Integer, Integer>(
						"DistributedProcessingCache") {
					@Override
					public Integer process(Integer i) {
						processingCountCache.invoke(i, countProcessor);
						processingThreadCache.put(i, Thread.currentThread()
								.getName());
						return i + 10;
					}
				}).collect(Collectors.toList());

		System.out.println("  processed result data list: " + resultDataList);
		assertEquals(expectedResultDataList, resultDataList);
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(testDataList.size(), distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(testDataList.size(), processingCountCache.size());
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ new HashMap<>(processingCountCache));
		processingCountCache.values().forEach(
				i -> assertEquals(new Integer(1), i));
		System.out.println("  processing thread names in "
				+ PROCESSING_THREAD_CACHE_NAME + ": "
				+ new HashMap<>(processingThreadCache));
		assertThat(new HashSet<String>(processingThreadCache.values()),
				hasSize(1));

		System.out.println("end of the test");
		System.out.println();
	}

	@Test
	public void multiThreadTestFifoDistributedFunction() {
		System.out.println("multiThreadTestFifoDistributedFunction");
		System.out.println("  test data list: " + testDataList);
		System.out.println(" before test");
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(0, distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(0, processingCountCache.size());

		System.out.println(" processing...");
		List<Integer> resultDataList = testDataList
				.parallelStream()
				.map(new FifoDistributedFunction<Integer, Integer>(
						"DistributedProcessingCache") {
					@Override
					public Integer process(Integer i) {
						processingCountCache.invoke(i, countProcessor);
						processingThreadCache.put(i, Thread.currentThread()
								.getName());
						return i + 10;
					}
				}).collect(Collectors.toList());

		System.out.println("  processed result data list: " + resultDataList);
		assertEquals(expectedResultDataList, resultDataList);
		System.out.println("  size of " + DISTRIBUTED_PROCESSING_CACHE_NAME
				+ ": " + distributedProcessingCache.size());
		assertEquals(testDataList.size(), distributedProcessingCache.size());
		System.out.println("  size of " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ processingCountCache.size());
		assertEquals(testDataList.size(), processingCountCache.size());
		System.out.println("  result in " + PROCESSING_COUNT_CACHE_NAME + ": "
				+ new HashMap<>(processingCountCache));
		processingCountCache.values().forEach(
				i -> assertEquals(new Integer(1), i));
		System.out.println("  processing thread names in "
				+ PROCESSING_THREAD_CACHE_NAME + ": "
				+ new HashMap<>(processingThreadCache));
		assertThat(new HashSet<String>(processingThreadCache.values()).size(),
				greaterThan(1));

		System.out.println("end of the test");
		System.out.println();
	}

}
