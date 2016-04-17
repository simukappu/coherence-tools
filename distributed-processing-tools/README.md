# Distributed Processing Tools
Tools for distributed processing, which provides following functions
* FifoDistributedProcessor: Consumer/Function implementation for distributed processing as First in, first out
* HashModFilter(Coming soon): Filter to get target entries by hashCode modulo of a field in key class

## Usage
### FifoDistributedProcessor
Call as Consumer/Function interface for extended interfaces or implemented abstract classes.  
For example, invoke as follows:  
```java
List<Integer> testDataList = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());

// Distributed processing by FifoDistributedConsumer
{
	testDataList.parallelStream().forEach(new FifoDistributedConsumer<Integer>("DistributedProcessingCache") {
		@Override
		public void process(Integer i) {
			// write a processing
			System.out.println(i);
		}
	});
}

// Distributed processing by FifoDistributedBiConsumer
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

// Distributed processing by FifoDistributedFunction
{
	List<Integer> resultDataList = testDataList
			.parallelStream()
			.map(new FifoDistributedFunction<Integer, Integer>("DistributedProcessingCache") {
				@Override
				public Integer process(Integer i) {
					// write a processing
					return i + 10;
				}
			}).collect(Collectors.toList());
}
```
### HashModFilter
Coming soon  

See [Javadoc](https://simukappu.github.io/coherence-tools/distributed-processing-tools/docs/apidocs/index.html) for more details.

## Testing
Just run [test.com.simukappu.coherence.distributed.TestFifoDistributedProcessor](https://github.com/simukappu/coherence-tools/blob/master/distributed-processing-tools/src/test/java/test/com/simukappu/coherence/distributed/TestFifoDistributedProcessor.java) as JUnit Test.  
You can run this test as stand-alone or multi-processes cluster by running CacheServer before the test.  
CacheServer joining this cluster can be started by [test.com.simukappu.coherence.distributed.server.CacheServer](https://github.com/simukappu/coherence-tools/blob/master/distributed-processing-tools/src/test/java/test/com/simukappu/coherence/distributed/server/CacheServer.java).

## API Document
<https://simukappu.github.io/coherence-tools/distributed-processing-tools/docs/project-reports.html>

## License
[Apache License](https://github.com/simukappu/coherence-tools/blob/master/LICENSE)
