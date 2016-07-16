# Spring MyBatis CacheStore  
CacheStore implementation integrated with Spring and MyBatis framework

## Usage
1. Make datasource configuration in Spring context file.  
See [src/test/resources/META-INF/spring/datasource-context.xml](spring-mybatis-cachestore/src/test/resources/META-INF/spring/datasource-context.xml).

2. Make MyBatis SQL mapper configuration in MyBatis mapper xml file.  
See [src/test/resources/META-INF/mybatis/mapper.xml](spring-mybatis-cachestore/src/test/resources/META-INF/mybatis/mapper.xml).

3. Make Spring bean configuration of CacheStore in Spring context file.  
See [src/test/resources/META-INF/spring/cachestore-context.xml](spring-mybatis-cachestore/src/test/resources/META-INF/spring/cachestore-context.xml).

4. Make Coherence cache configration file according to Coherence Spring Integration.  
See [src/test/resources/spring-cache-config.xml](spring-mybatis-cachestore/src/test/resources/spring-cache-config.xml).  
See also <https://github.com/coherence-community/coherence-spring-integration>.

## Testing
1. Set Database environment.

2. Edit [src/test/resources/META-INF/spring/datasource-context.xml](spring-mybatis-cachestore/src/test/resources/META-INF/spring/datasource-context.xml) for your environment.

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

5. Run test modules under [test.com.simukappucoherence.cachestore.spring.mybatis](https://github.com/simukappu/coherence-tools/tree/master/spring-mybatis-cachestore/src/test/java/test/com/simukappu/coherence/cachestore/spring/mybatis) package as JUnit Test.

## API Document
<https://simukappu.github.io/coherence-tools/spring-mybatis-cachestore/docs/project-reports.html>

## License
[Apache License](LICENSE)
