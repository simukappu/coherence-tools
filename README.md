# Coherence tools
[![Build Status](https://travis-ci.com/simukappu/coherence-tools.svg?branch=master)](https://travis-ci.com/simukappu/coherence-tools)
[![Coherence Version](http://img.shields.io/github/v/tag/simukappu/coherence-tools)](https://github.com/simukappu/coherence-tools/tags)
[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](MIT-LICENSE)

Utility tools for Oracle Coherence

## Description
Utility tools for Oracle Coherence includes following components

### Distributed Processing Tools  
Tools to run distributed processing task in multiple Coherence member processes exclusively. This component enables distributed processing with dynamic changes in number of processing members, which provides following functions
* FIFO distributed processor: Consumer/Function implementation for distributed processing as first in, first out
* Hash modulo filter: Filter to get target entries by hashCode modulo of a key/value object or a field of it

See [distributed-processing-tools](distributed-processing-tools)

### Write Behind Tools
Tools for write behind queue, which provides following functions
* Get current write behind queue size in the cluster
* Clear retaining data in write behind queue in the cluster

See [write-behind-tools](write-behind-tools)

### Spring MyBatis CacheStore  
CacheStore implementation integrated with Spring and MyBatis framework

See [spring-mybatis-cachestore](spring-mybatis-cachestore)

### Multi Clusters Proxy
Tools for Coherence*Extend proxy to connect with multiple clusters, which provides following components
* SelectableCacheFactory: Extended CacheFactory class to operate multiple named caches from different clusters

See [multi-clusters-proxy](multi-clusters-proxy)

## Requirements
These tools require Oracle Coherence.

### Using Oracle Coherence Community Edition
All builtin test modules uses [Oracle Coherence Community Edition](https://coherence.community). All required modules are packaged as Maven project.

### Using Oracle Coherence Non-Community Edition
If you would like to use non-community edition, you need to install Oracle Coherence. 
See [Oracle Coherence](https://www.oracle.com/middleware/technologies/coherence.html) for more details (When not for development purposes, Oracle Coherence license is needed).

1. Download Coherence Stand-Alone Install from [Oracle Technology Network](https://www.oracle.com/middleware/technologies/coherence-downloads.html)

2. Run installer as following command with your Coherence version
```sh
$ java -jar fmw_12.2.1.0.0_coherence.jar
```

3. Register Coherence to local Maven repository
```sh
$ mvn -DpomFile=$ORACLE_HOME/oracle_common/plugins/maven/com/oracle/maven/oracle-maven-sync/12.2.1/oracle-maven-sync-12.2.1.pom -Dfile=$ORACLE_HOME/oracle_common/plugins/maven/com/oracle/maven/oracle-maven-sync/12.2.1/oracle-maven-sync-12.2.1.jar install:install-file
$ mvn -Doracle-maven-sync.oracleHome=$ORACLE_HOME -Doracle-maven-sync.testOnly=false com.oracle.maven:oracle-maven-sync:12.2.1-0-0:push
```

## License
[MIT License](MIT-LICENSE)
