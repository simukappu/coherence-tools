# Distributed Processing Tools
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.simukappu/distributed-processing-tools/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.simukappu/distributed-processing-tools)

Tools to run distributed processing task in multiple Coherence member processes exclusively. This component enables distributed processing with dynamic changes in number of processing members, which provides following functions
* FIFO distributed processor: Consumer/Function implementation for distributed processing as first in, first out
* Hash modulo filter: Filter to get target entries by hashCode modulo of a key/value object or a field of it

## Usage
### Use with Apache Maven
Add dependency to pom.xml like this:
```xml
<dependency>
  <groupId>io.github.simukappu</groupId>
  <artifactId>distributed-processing-tools</artifactId>
  <version>1.0.0</version>
</dependency>
```

### FIFO distributed processor
Call as Consumer/Function interface with extended functional interfaces or implemented abstract classes.  
For example, use as follows:  
```java
// Prepare test data list
List<Integer> testDataList = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());

// Distributed processing with FifoDistributedConsumer from parallel stream processing
{
	testDataList
		.parallelStream()
		.forEach(new FifoDistributedConsumer<Integer>("DistributedProcessingCache") {
			@Override
			public void process(Integer i) {
				// write a processing here
				System.out.println(i);
			}
		});
}

// Distributed processing with FifoDistributedBiConsumer from stream processing
{
	FifoDistributedBiConsumer<Integer> fifoDistributedBiConsumer = i -> {
		// write a processing here
		System.out.println(i);
	};
	testDataList
		.stream()
		.collect(Collectors.toMap(Function.identity(), i -> "DistributedProcessingCache"))
		.forEach(fifoDistributedBiConsumer);
}

// Distributed processing with FifoDistributedFunction from parallel stream processing
{
	List<Integer> resultDataList = testDataList
		.parallelStream()
		.map(new FifoDistributedFunction<Integer, Integer>("DistributedProcessingCache") {
			@Override
			public Integer process(Integer i) {
				// write a processing here
				return i + 10;
			}
		})
		.collect(Collectors.toList());
}

// Distributed processing with FifoDistributedBiFunction from parallel stream processing
{
	FifoDistributedBiFunction<Integer, Integer> fifoDistributedBiFunction = i -> {
		// write a processing here
		return i + 10;
	};
	List<Integer> resultDataList = testDataList.stream()
		.collect(Collectors.toConcurrentMap(Function.identity(), i -> "DistributedProcessingCache")).entrySet()
		.parallelStream().map(e -> {
			return fifoDistributedBiFunction.apply(e.getKey(), e.getValue());
			// Sort result list since parallelStream was called from converted entry set
		}).sorted().collect(Collectors.toList());
}
```
Or, you can simply call these functions as follows:
```java
// Single processing with FifoDistributedConsumer
{
	FifoDistributedConsumer<Integer> fifoDistributedConsumer = 
		new FifoDistributedConsumer<Integer>("DistributedProcessingCache") {
			@Override
			public void process(Integer i) {
				// write a processing here
				System.out.println(i);
			}
		};
	fifoDistributedConsumer.accept(1);
}

// Single processing with FifoDistributedBiConsumer
{
	FifoDistributedBiConsumer<Integer> fifoDistributedConsumer = i -> {
		// write a processing here
		System.out.println(i);
	};
	fifoDistributedConsumer.accept(1, "DistributedProcessingCache");
}

// Single processing with FifoDistributedFunction
{
	FifoDistributedFunction<Integer, Integer> fifoDistributedFunction = 
		new FifoDistributedFunction<Integer, Integer>("DistributedProcessingCache") {
			@Override
			public Integer process(Integer i) {
				// write a processing here
				return i + 10;
			}
		};
	Integer resultData = fifoDistributedFunction.apply(1);
}

// Single processing with FifoDistributedBiFunction
{
	FifoDistributedBiFunction<Integer, Integer> fifoDistributedBiFunction = i -> {
		// write a processing here
		return i + 10;
	};
	Integer resultData = fifoDistributedBiFunction.apply(1, "DistributedProcessingCache");
}
```

### Hash modulo filter
You can use Coherence member role to manage the number of distributed members and distributed id for each processes. This function provides a utility class (DistributedMemberUtil) to help you. Create filter instance from the extractor using the number of distributed members and managed distributed id.  
For example, use as follows:  
```java
// Use HashModExtractor as key extractor
int targetModulo = DistributedMemberUtil.getSequentialIdFromSameRoleMemberSet();
int numProcesses = DistributedMemberUtil.getSameRoleMembers().size();
Filter<TestEntity> hashModFilter = new EqualsFilter<TestEntity, Integer>(
	new HashModExtractor<TestEntity>(AbstractExtractor.KEY, numProcesses), targetModulo);
Set<TestEntity> targetKeys = testEntityPoolCache.keySet(hashModFilter);
Map<TestEntity, TestEntity> filteredDataMap = testEntityPoolCache.getAll(targetKeys);
filteredDataMap.entrySet().forEach(e -> {
	// write a processing here
	System.out.println("key=" + e.getKey() + ", value=" + e.getValue());
});

// Use ReflectionHashModExtractor as value extractor
int targetModulo = DistributedMemberUtil.getSequentialIdFromSameRoleMemberSet();
int numProcesses = DistributedMemberUtil.getSameRoleMembers().size();
Filter<TestEntity> reflectionHashModFilter = new EqualsFilter<TestEntity, Integer>(
	new ReflectionHashModExtractor<TestEntity>("getIntId", null, AbstractExtractor.KEY, numProcesses),　targetModulo);
Set<TestEntity> targetKeys = testEntityPoolCache.keySet(reflectionHashModFilter);
Map<TestEntity, TestEntity> filteredDataMap = testEntityPoolCache.getAll(targetKeys);
filteredDataMap.entrySet().forEach(e -> {
	// write a processing here
	System.out.println("key=" + e.getKey() + ", value=" + e.getValue());
});
```

See [Javadoc](https://simukappu.github.io/coherence-tools/distributed-processing-tools/docs/apidocs/index.html) for more details.

## Testing
### FIFO distributed processor
Just run [test.com.simukappu.coherence.distributed.fifo.TestFifoDistributedProcessor](src/test/java/test/com/simukappu/coherence/distributed/fifo/TestFifoDistributedProcessor.java) as JUnit Test.  
You can run this test as stand-alone or multi-processes cluster by running CacheServer before the test.  
CacheServer joining this cluster can be started by [test.com.simukappu.coherence.distributed.server.CacheServer](src/test/java/test/com/simukappu/coherence/distributed/server/CacheServer.java).

### Hash modulo filter
Run [test.com.simukappu.coherence.distributed.hash.SingleProcessTestHashDistributedProcessing](src/test/java/test/com/simukappu/coherence/distributed/hash/SingleProcessTestHashDistributedProcessing.java) as JUnit Test.  
For the testing on multi processes, Run [test.com.simukappu.coherence.distributed.hash.MultiProcessTestHashModExtractor](src/test/java/test/com/simukappu/coherence/distributed/hash/MultiProcessTestHashModExtractor.java) and/or [test.com.simukappu.coherence.distributed.hash.MultiProcessTestReflectionHashModExtractor](src/test/java/test/com/simukappu/coherence/distributed/hash/MultiProcessTestReflectionHashModExtractor.java) as JUnit Test. You have to run several number of these test processes depending on BaseHashDistributedProcessingTest.NUM_PROCESSES (default is 3) in the same time.  

## API Document
<https://simukappu.github.io/coherence-tools/distributed-processing-tools/docs/project-reports.html>

## License
[Apache License](LICENSE)
