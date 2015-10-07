# Coherence tools
Utility tools for Oracle Coherence

## Description
Utility tools for Oracle Coherence includes following components

### Spring MyBatis CacheStore  
CacheStore implementation integrated with Spring and MyBatis framework  
Go to [spring-mybatis-cachestore](https://github.com/simukappu/coherence-tools/tree/master/spring-mybatis-cachestore "spring-mybatis-cachestore")

### Write Behind Tools
Tools for write behind queue, which provides following functions
* Get current write behind queue size in the cluster
* Clear retaining data in write behind queue in the cluster

Go to [write-behind-tools](https://github.com/simukappu/coherence-tools/tree/master/write-behind-tools "write-behind-tools")

## Requirement
Installing Oracle Coherence (Oracle Coherence license is needed)
<http://www.oracle.com/technetwork/jp/middleware/coherence/overview/index.html>

### For development environment
1. Download Coherence Stand-Alone Install from here
<http://www.oracle.com/technetwork/jp/middleware/coherence/downloads/index.html>

2. Run installer as following command
    ```sh
$ java -jar fmw_12.1.3.0.0_coherence_Disk1_1of1.jar
```

3. Register Coherence to local Maven repository
    ```sh
$ mvn -DpomFile=$ORACLE_HOME/oracle_common/plugins/maven/com/oracle/maven/oracle-maven-sync/12.1.3/oracle-maven-sync-12.1.3.pom -Dfile=$ORACLE_HOME/oracle_common/plugins/maven/com/oracle/maven/oracle-maven-sync/12.1.3/oracle-maven-sync-12.1.3.jar install:install-file
$ mvn -Doracle-maven-sync.oracleHome=$ORACLE_HOME -Doracle-maven-sync.testOnly=false com.oracle.maven:oracle-maven-sync:12.1.3-0-0:push
```

## License
[Apache License](https://github.com/simukappu/coherence-tools/blob/master/LICENSE)
