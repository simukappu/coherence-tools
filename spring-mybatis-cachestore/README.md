# Spring MyBatis CacheStore  
[![Maven Central](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fio%2Fgithub%2Fsimukappu%2Fspring-mybatis-cachestore%2Fmaven-metadata.xml&label=maven%20central)](https://central.sonatype.com/artifact/io.github.simukappu/spring-mybatis-cachestore)

CacheStore implementation integrated with Spring and MyBatis framework

## Dependency
Current implementation depends on Spring Framework 7 and MyBatis-Spring 4.

## Usage
### Use with Apache Maven
Add dependency to pom.xml like this:
```xml
<dependency>
  <groupId>io.github.simukappu</groupId>
  <artifactId>spring-mybatis-cachestore</artifactId>
  <version>1.1.0</version>
</dependency>
```

### Configure with Spring and MyBatis
1. Make datasource configuration in Spring context file.  
See [src/test/resources/META-INF/spring/datasource-context.xml](src/test/resources/META-INF/spring/datasource-context.xml).

2. Make MyBatis SQL mapper configuration in MyBatis mapper xml file.  
See [src/test/resources/META-INF/mybatis/mapper.xml](src/test/resources/META-INF/mybatis/mapper.xml).

3. Make Spring bean configuration of CacheStore in Spring context file.  
See [src/test/resources/META-INF/spring/cachestore-context.xml](src/test/resources/META-INF/spring/cachestore-context.xml).

4. Make Coherence cache configration file according to Coherence Spring Integration.  
See [src/test/resources/spring-cache-config.xml](src/test/resources/spring-cache-config.xml).  
See also <https://github.com/coherence-community/coherence-spring-integration>.

## Testing
1. Set Database environment.

2. Edit [src/test/resources/META-INF/spring/datasource-context.xml](src/test/resources/META-INF/spring/datasource-context.xml) for your environment.

3. Create table in your Database.  
Sample SQL script for MySQL and Oracle is provided.  
For MySQL, 
```sh
$ mysql -u root
mysql> CREATE DATABASE IF NOT EXISTS coherence_tools_test;
mysql> use coherence_tools_test
mysql> source spring-mybatis-cachestore/script/create_table_mysql.sql
```  
For Oracle, 
```sh
$ sqlplus scott/tiger@localhost:1521/orcl @spring-mybatis-cachestore/script/create_table_oracle.sql
```  

4. Edit pom.xml according to your datasource-context.xml.

5. Run test modules under [test.com.simukappu.coherence.cachestore.spring.mybatis](src/test/java/test/com/simukappu/coherence/cachestore/spring/mybatis) package as JUnit Test.

## API Document
<https://simukappu.github.io/coherence-tools/spring-mybatis-cachestore/docs/project-reports.html>

## License
[Apache License](LICENSE)
