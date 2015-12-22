# Write Behind Tools
Tools for write behind queue, which provides following functions
* Get current write behind queue size in the cluster
* Clear retaining data in write behind queue in the cluster

## Usage
Invoke Write Behind Tools Processor from invokeAll method in NamedCache<Object, Object>.  
For example, invoke as follows:  
```java
NamedCache<Object, Object> namedCache = CacheFactory.getCache("CacheName");
Map<Object, Map.Entry<Integer, Integer>> mapResults1 = namedCache.invokeAll(new AlwaysFilter(), new GetWriteQueueSizeProcessor(targetCacheName));
Map<Object, Map.Entry<Integer, Integer>> mapResults2 = namedCache.invokeAll(new AlwaysFilter(), new ClearWriteQueueProcessor(targetCacheName));
```
See [Javadoc](https://simukappu.github.io/coherence-tools/write-behind-tools/docs/apidocs/index.html) for more details.

## Testing
Just run [test.com.simukappu.coherence.writequeue.IntegrationTest.java](https://github.com/simukappu/coherence-tools/blob/master/write-behind-tools/src/test/java/test/tool/coherence/util/writequeue/IntegrationTest.java).  
You can run this test as stand-alone or multi-processes cluster by running CacheServer before you run this test. You can start CacheServer by running test.com.simukappu.coherence.writequeue.server.CacheServer.

## API Document
<https://simukappu.github.io/coherence-tools/write-behind-tools/docs/project-reports.html>

## License
[Apache License](https://github.com/simukappu/coherence-tools/blob/master/LICENSE)
