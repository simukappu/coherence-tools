# Multi Clusters Proxy
Tools for Coherence*Extend proxy to connect with multiple clusters, which provides following components
* SelectableCacheFactory: Extended CacheFactory class to operate multiple named caches from different clusters

## Usage
SelectableCacheFactory needs to be configured with cache configuration files and class loaders for each Coherence*Extend proxy configuration to connect with clusters.  
After a few configurations, SelectableCacheFactory can be used like com.tangosol.net.CacheFactory.  
For example, use as follows:  
```java
final String CACHE_IN_BOTH_CLUSTERS = "DataCacheInBothClusters";

// Configure SelectableCacheFactory
String clusterNameA = SelectableCacheFactory.addCacheFactory(
		"clientA-cache-config.xml", ClassLoader.getSystemClassLoader(),
		CACHE_IN_BOTH_CLUSTERS);
String clusterNameB = SelectableCacheFactory.addCacheFactory(
		"clientB-cache-config.xml", ClassLoader.getSystemClassLoader(),
		CACHE_IN_BOTH_CLUSTERS);

// Get cache from each cluster
NamedCache<String, String> multiCacheInClusterA = SelectableCacheFactory
		.getSelectableTypedCache(clusterNameA, CACHE_IN_BOTH_CLUSTERS,
				TypeAssertion.withTypes(String.class, String.class));
NamedCache<String, String> multiCacheInClusterB = SelectableCacheFactory
		.getSelectableTypedCache(clusterNameB, CACHE_IN_BOTH_CLUSTERS,
				TypeAssertion.withTypes(String.class, String.class));

// Use as normal NamedCache
multiCacheInClusterA.put("ClusterName", "ForClusterA");
multiCacheInClusterB.put("ClusterName", "ForClusterB");
```
See [Javadoc](https://simukappu.github.io/coherence-tools/multi-clusters-proxy/docs/apidocs/index.html) for more details.

## Testing
First, run extend proxy servers by [test.com.simukappu.coherence.mclusters.proxy.ProxyServerInClusterA](src/test/java/test/com/simukappu/coherence/mclusters/proxy/ProxyServerInClusterA.java) and [test.com.simukappu.coherence.mclusters.proxy.ProxyServerInClusterB](src/test/java/test/com/simukappu/coherence/mclusters/proxy/ProxyServerInClusterB.java).  
Then, run [test.com.simukappu.coherence.mclusters.IntegrationTest](src/test/java/test/com/simukappu/coherence/mclusters/IntegrationTest.java) as JUnit Test.  
You can run this test as stand-alone or multi-processes cluster by running CacheServer before the test.  
CacheServer joining one of these clusters can be started by [test.com.simukappu.coherence.mclusters.server.CacheServerInClusterA](src/test/java/test/com/simukappu/coherence/mclusters/server/CacheServerInClusterA.java) and [test.com.simukappu.coherence.mclusters.server.CacheServerInClusterB](src/test/java/test/com/simukappu/coherence/mclusters/server/CacheServerInClusterB.java).

## API Document
<https://simukappu.github.io/coherence-tools/multi-clusters-proxy/docs/project-reports.html>

## License
[Apache License](LICENSE)
