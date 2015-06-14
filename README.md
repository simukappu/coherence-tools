# Coherence tools
Utility tools for Oracle Coherence

## Description
Utility tools for Oracle Coherence includes following components

### Spring MyBatis CacheStore  
CacheStore implementation integrated with Spring and MyBatis framework

### Write Behind Tools
Tools for write behind queue, which provides following functions
* Get current write behind queue size in the cluster
* Clear retaining data in write behind queue in the cluster

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

## Usage
### Spring MyBatis CacheStore
1. Make datasource configuration in Spring context file.  
See [src/test/resources/META-INF/spring/datasource-context.xml](https://github.com/simukappu/Coherence-tools/blob/master/spring-mybatis-cachestore/src/test/resources/META-INF/spring/datasource-context.xml).

2. Make MyBatis SQL mapper configuration in MyBatis mapper xml file.  
See [src/test/resources/META-INF/mybatis/mapper.xml](https://github.com/simukappu/Coherence-tools/blob/master/spring-mybatis-cachestore/src/test/resources/META-INF/mybatis/mapper.xml).

3. Make Spring bean configuration of CacheStore in Spring context file.  
See [src/test/resources/META-INF/spring/cachestore-context.xml](https://github.com/simukappu/Coherence-tools/blob/master/spring-mybatis-cachestore/src/test/resources/META-INF/spring/cachestore-context.xml).

4. Make Coherence cache configration file according to Coherence Spring Integration.  
See [src/test/resources/spring-cache-config.xml](https://github.com/simukappu/Coherence-tools/blob/master/spring-mybatis-cachestore/src/test/resources/spring-cache-config.xml).  
See also <https://github.com/coherence-community/coherence-spring-integration>.

### Write Behind Tools
Invoke Write Behind Tools Processor from invokeAll method.  
For example, invoke as follows:  
```java
Map mapResults = namedCache.invokeAll(new AlwaysFilter(), new GetWriteQueueSizeProcessor(targetCacheName));
Map mapResults = namedCache.invokeAll(new AlwaysFilter(), new ClearWriteQueueProcessor(targetCacheName));
```
See [Javadoc](https://simukappu.github.io/Coherence-tools/write-behind-tools/docs/apidocs/index.html) for more details.

## Testing
### Spring MyBatis CacheStore
1. Set Database environment.

2. Edit [src/test/resources/META-INF/spring/datasource-context.xml](https://github.com/simukappu/Coherence-tools/blob/master/spring-mybatis-cachestore/src/test/resources/META-INF/spring/datasource-context.xml) for your environment.

3. Create table in your Database.  
Sample SQL script for MySQL and Oracle is provided.  
For MySQL, 
    ```sh
$ mysql -u root
mysql> source spring-mybatis-cachestore/script/create_table_mysql.sql
```  
For Oracle, 
    ```sh
$ sqlplus scott/tiger@localhost:1521/orcl @spring-mybatis-cachestore/script/create_table_oracle.sql
```  

4. Edit pom.xml according to your datasource-context.xml.

5. Run test modules under [test.tool.coherence.cachestore.spring.mybatis](https://github.com/simukappu/Coherence-tools/tree/master/spring-mybatis-cachestore/src/test/java/test/tool/coherence/cachestore/spring/mybatis) package as JUnit module.

### Write Behind Tools
Just run [test.tool.coherence.util.writequeue.IntegrationTest.java](https://github.com/simukappu/Coherence-tools/blob/master/write-behind-tools/src/test/java/test/tool/coherence/util/writequeue/IntegrationTest.java).

## API Document
### Spring MyBatis CacheStore
<https://simukappu.github.io/Coherence-tools/spring-mybatis-cachestore/docs/project-reports.html>

### Write Behind Tools
<https://simukappu.github.io/Coherence-tools/write-behind-tools/docs/project-reports.html>

## License
[Apache License](https://github.com/simukappu/Coherence-tools/blob/master/LICENSE)
