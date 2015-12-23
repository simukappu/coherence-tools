# Multi Clusters Proxy
Tools for extend proxy to connect multiple clusters, which provides following components
* SelectableCacheFactory: Selectable cache factory class to operate multiple named caches in several clusters

## Usage
SelectableCacheFactory needs configure with cache configuration files and class loaders for each extend proxy configuration to connect clusters.  
After a few configurations, SelectableCacheFactory can be used like com.tangosol.net.CacheFactory.  
For example, use as follows:  
```java
final String CACHE_IN_BOTH_CLUSTERS = "DataCacheInBothCluster";

// Configure SelectableCacheFactory
String clusterNameA = SelectableCacheFactory.addCacheFactory(
		"clientA-cache-config.xml", ClassLoader.getSystemClassLoader(),
		CACHE_IN_BOTH_CLUSTERS);
String clusterNameB = SelectableCacheFactory.addCacheFactory(
		"clientB-cache-config.xml", ClassLoader.getSystemClassLoader(),
		CACHE_IN_BOTH_CLUSTERS);

// Get caches from each clusters
NamedCache<String, String> cacheBothClusterInClusterA = SelectableCacheFactory
		.getSelectableTypedCache(clusterNameA, CACHE_IN_BOTH_CLUSTERS,
				TypeAssertion.withTypes(String.class, String.class));
NamedCache<String, String> cacheBothClusterInClusterB = SelectableCacheFactory
		.getSelectableTypedCache(clusterNameB, CACHE_IN_BOTH_CLUSTERS,
				TypeAssertion.withTypes(String.class, String.class));

// Use as normal NamedCache
cacheBothClusterInClusterA.put("ClusterName", "For cluster A");
cacheBothClusterInClusterB.put("ClusterName", "For cluster B");
```
See [Javadoc](https://simukappu.github.io/coherence-tools/multi-clusters-proxy/docs/apidocs/index.html) for more details.

## Testing
First, run extend proxy servers by [test.com.simukappu.coherence.mclusters.proxy.ProxyServerInClusterA](https://github.com/simukappu/coherence-tools/blob/master/multi-clusters-proxy/src/test/java/test/com/simukappu/coherence/mclusters/proxy/ProxyServerInClusterA.java) and [test.com.simukappu.coherence.mclusters.proxy.ProxyServerInClusterB](https://github.com/simukappu/coherence-tools/blob/master/multi-clusters-proxy/src/test/java/test/com/simukappu/coherence/mclusters/proxy/ProxyServerInClusterB.java).  
Then, run [test.com.simukappu.coherence.mclusters.IntegrationTest.java](https://github.com/simukappu/coherence-tools/blob/master/multi-clusters-proxy/src/test/java/test/com/simukappu/coherence/mclusters/IntegrationTest.java) as JUnit Test.  
You can run this test as stand-alone or multi-processes cluster by running CacheServer before the test.  
CacheServer joining this cluster can be started by [test.com.simukappu.coherence.mclusters.server.CacheServerInClusterA](https://github.com/simukappu/coherence-tools/blob/master/multi-clusters-proxy/src/test/java/test/com/simukappu/coherence/mclusters/server/CacheServerInClusterA.java) and [test.com.simukappu.coherence.mclusters.server.CacheServerInClusterB](https://github.com/simukappu/coherence-tools/blob/master/multi-clusters-proxy/src/test/java/test/com/simukappu/coherence/mclusters/server/CacheServerInClusterB.java).

## API Document
<https://simukappu.github.io/coherence-tools/multi-clusters-proxy/docs/project-reports.html>

## License
[Apache License](https://github.com/simukappu/coherence-tools/blob/master/LICENSE)
