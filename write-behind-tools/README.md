# Write Behind Tools
Tools for write behind queue, which provides following functions
* Get current write behind queue size in the cluster
* Clear retaining data in write behind queue in the cluster

## Usage
Invoke Write Behind Tools Processor from invokeAll method in NamedCache\<Object, Object\>.  
For example, invoke as follows:  
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
Just run [test.com.simukappu.coherence.writequeue.IntegrationTest.java](https://github.com/simukappu/coherence-tools/blob/master/write-behind-tools/src/test/java/test/com/simukappu/coherence/writequeue/IntegrationTest.java).  
You can run this test as stand-alone or multi-processes cluster by running CacheServer before you run this test.  
CacheServer joining this cluster can be started by [test.com.simukappu.coherence.writequeue.server.CacheServer](https://github.com/simukappu/coherence-tools/blob/master/write-behind-tools/src/test/java/test/com/simukappu/coherence/writequeue/server/CacheServer.java).

## API Document
<https://simukappu.github.io/coherence-tools/write-behind-tools/docs/project-reports.html>

## License
[Apache License](https://github.com/simukappu/coherence-tools/blob/master/LICENSE)
