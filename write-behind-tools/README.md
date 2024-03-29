# Write Behind Tools
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.simukappu/write-behind-tools/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.simukappu/write-behind-tools)

Tools for write behind queue, which provides following functions
* Get current write behind queue size in the cluster
* Clear retaining data in write behind queue in the cluster

## Usage
### Use with Apache Maven
Add dependency to pom.xml like this:
```xml
<dependency>
  <groupId>io.github.simukappu</groupId>
  <artifactId>write-behind-tools</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Write Behind Tools Processor
Invoke Write Behind Tools Processor from invokeAll method in NamedCache\<Object, Object\>.  
For example, use as follows:  
```java
NamedCache<Object, Object> namedCache = CacheFactory.getCache("CacheName");

// Get current write behind queue size in the cluster
{
	// Invoke GetWriteQueueSizeProcessor to all local-storage enabled nodes
	Map<Object, Map.Entry<Integer, Integer>> mapResults = targetCache
			.invokeAll(new AlwaysFilter<Object>(),
					new GetWriteQueueSizeProcessor(targetCacheName));
	
	// Sort result map set by keys
	List<Map.Entry<Integer, Integer>> resultList = new ArrayList<>(
			mapResults.values());
	resultList.sort((a, b) -> a.getKey() - b.getKey());
	
	// Display results
	System.out.println("Size of write behind queue:");
	resultList.forEach(resultEntry -> {
		System.out.println(" " + resultEntry.getValue()
				+ " entries in Node# " + resultEntry.getKey());
	});
}

// Clear retaining data in write behind queue in the cluster
{
	// Invoke ClearWriteQueueProcessor to all local-storage enabled nodes
	Map<Object, Map.Entry<Integer, Integer>> mapResults = targetCache
			.invokeAll(new AlwaysFilter<Object>(),
					new ClearWriteQueueProcessor(targetCacheName));
	
	// Sort result map set by keys
	List<Map.Entry<Integer, Integer>> resultList = new ArrayList<>(
			mapResults.values());
	resultList.sort((a, b) -> a.getKey() - b.getKey());
	
	// Display results
	System.out.println("Removed entries from write behind queue:");
	resultList.forEach(resultEntry -> {
		System.out.println(" " + resultEntry.getValue()
				+ " entries from Node# " + resultEntry.getKey());
	});
}
```
See [Javadoc](https://simukappu.github.io/coherence-tools/write-behind-tools/docs/apidocs/index.html) for more details.

## Testing
Just run [test.com.simukappu.coherence.writequeue.IntegrationTest](src/test/java/test/com/simukappu/coherence/writequeue/IntegrationTest.java) as JUnit Test.  
You can run this test as stand-alone or multi-processes cluster by running CacheServer before the test.  
CacheServer joining this cluster can be started by [test.com.simukappu.coherence.writequeue.server.CacheServer](src/test/java/test/com/simukappu/coherence/writequeue/server/CacheServer.java).

## API Document
<https://simukappu.github.io/coherence-tools/write-behind-tools/docs/project-reports.html>

## License
[Apache License](LICENSE)
