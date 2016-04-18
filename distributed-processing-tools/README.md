# Distributed Processing Tools
Tools for distributed processing, which provides following functions
* FifoDistributedProcessor: Consumer/Function implementation for distributed processing as First in, first out
* HashModFilter(Coming soon): Filter to get target entries by hashCode modulo of a field in key class

## Usage
### FifoDistributedProcessor
Call as Consumer/Function interface with extended interfaces or implemented abstract classes.  
For example, process as follows:  
```java
// Single processing with FifoDistributedConsumer
{
	FifoDistributedConsumer<Integer> fifoDistributedConsumer = 
		new FifoDistributedConsumer<Integer>("DistributedProcessingCache") {
			@Override
			public void process(Integer i) {
				// write a processing
				System.out.println(i);
			}
		};
	fifoDistributedConsumer.accept(1);
}

// Single processing with FifoDistributedBiConsumer
{
	FifoDistributedBiConsumer<Integer> fifoDistributedConsumer = i -> {
		// write a processing
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
				// write a processing
				return i + 10;
			}
		};
	Integer resultData = fifoDistributedFunction.apply(1);
}

// Single processing with FifoDistributedBiFunction
{
	FifoDistributedBiFunction<Integer, Integer> fifoDistributedBiFunction = i -> {
		// write a processing
		return i + 10;
	};
	Integer resultData = fifoDistributedBiFunction.apply(1, "DistributedProcessingCache");
}

List<Integer> testDataList = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());

// Distributed processing with FifoDistributedConsumer from parallel stream processing
{
	testDataList
		.parallelStream()
		.forEach(new FifoDistributedConsumer<Integer>("DistributedProcessingCache") {
			@Override
			public void process(Integer i) {
				// write a processing
				System.out.println(i);
			}
		});
}

// Distributed processing with FifoDistributedBiConsumer from stream processing
{
	FifoDistributedBiConsumer<Integer> fifoDistributedBiConsumer = i -> {
		// write a processing
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
				// write a processing
				return i + 10;
			}
		})
		.collect(Collectors.toList());
}
```
### HashModFilter
Coming soon  

See [Javadoc](https://simukappu.github.io/coherence-tools/distributed-processing-tools/docs/apidocs/index.html) for more details.

## Testing
Just run [test.com.simukappu.coherence.distributed.fifo.TestFifoDistributedProcessor](https://github.com/simukappu/coherence-tools/blob/master/distributed-processing-tools/src/test/java/test/com/simukappu/coherence/distributed/fifo/TestFifoDistributedProcessor.java) as JUnit Test.  
You can run this test as stand-alone or multi-processes cluster by running CacheServer before the test.  
CacheServer joining this cluster can be started by [test.com.simukappu.coherence.distributed.server.CacheServer](https://github.com/simukappu/coherence-tools/blob/master/distributed-processing-tools/src/test/java/test/com/simukappu/coherence/distributed/server/CacheServer.java).

## API Document
<https://simukappu.github.io/coherence-tools/distributed-processing-tools/docs/project-reports.html>

## License
[Apache License](https://github.com/simukappu/coherence-tools/blob/master/LICENSE)
