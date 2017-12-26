# linode-apiv4-java

This is a Java library for using Linode's version 4 API.

**linode-apiv4-java-model** defines Java model classes ready for JSON (de)serialization using [Jackson](https://github.com/FasterXML/jackson).

**linode-apiv4-java-spring** relies on [Spring Framework](https://spring.io/) functionality to provide a convenient API for performing Linode REST API queries over HTTPS.

**linode-apiv4-java-workers** adds a basic "worker pool" service on top of that.

### Status

**Current status:** partially complete; partially tested.

Current release: None yet

Current pre-release: `1.0.0-SNAPSHOT`

### Using It

Documentation is provided mainly through [API Javadocs](http://archiecobbs.github.io/linode-apiv4-java/site/apidocs/index.html).

See [the org.dellroad.apiv4.spring package](https://archiecobbs.github.io/linode-apiv4-java/site/apidocs/index.html?org/dellroad/linode/apiv4/spring/package-summary.html) for an example of how to setup the [LinodeApiRequestSender](https://archiecobbs.github.io/linode-apiv4-java/site/apidocs/index.html?org/dellroad/linode/apiv4/spring/LinodeApiRequestSender.html) for API queries.

See [WorkerPool](https://archiecobbs.github.io/linode-apiv4-java/site/apidocs/index.html?org/dellroad/linode/apiv4/workers/WorkerPool.html) for details on setting up worker pool.

### Getting It

**linode-apiv4-java** is available from [Maven Central](http://search.maven.org/#search|ga|1|a%3Alinode-apiv4-java):

```xml
    <dependency>
        <groupId>org.dellroad</groupId>
        <artifactId>linode-apiv4-java-model</artifactId>
    </dependency>
    <dependency>
        <groupId>org.dellroad</groupId>
        <artifactId>linode-apiv4-java-spring</artifactId>
    </dependency>
    <dependency>
        <groupId>org.dellroad</groupId>
        <artifactId>linode-apiv4-java-workers</artifactId>
    </dependency>
```
