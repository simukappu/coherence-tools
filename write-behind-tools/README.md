# Write Behind Tools
Tools for write behind queue, which provides following functions
* Get current write behind queue size in the cluster
* Clear retaining data in write behind queue in the cluster

## Usage
Invoke Write Behind Tools Processor from invokeAll method.  
For example, invoke as follows:  
```java
Map mapResults = namedCache.invokeAll(new AlwaysFilter(), new GetWriteQueueSizeProcessor(targetCacheName));
Map mapResults = namedCache.invokeAll(new AlwaysFilter(), new ClearWriteQueueProcessor(targetCacheName));
```
See [Javadoc](https://simukappu.github.io/coherence-tools/write-behind-tools/docs/apidocs/index.html) for more details.

## Testing
Just run [test.tool.coherence.util.writequeue.IntegrationTest.java](https://github.com/simukappu/coherence-tools/blob/master/write-behind-tools/src/test/java/test/tool/coherence/util/writequeue/IntegrationTest.java).

## API Document
<https://simukappu.github.io/coherence-tools/write-behind-tools/docs/project-reports.html>

## License
[Apache License](https://github.com/simukappu/coherence-tools/blob/master/LICENSE)
