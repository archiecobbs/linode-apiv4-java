# linode-apiv4-java

This is a Java library for using Linode's version 4 API.

There are two sub-modules.

The **linode-apiv4-java-model** module defines Java model classes ready for JSON (de)serialization using [Jackson](https://github.com/FasterXML/jackson).

The **linode-apiv4-java-spring** module is for projects using [Spring Framework](https://spring.io/), providing a convenient setup for performing Linode REST API queries over HTTPS.

### Status

**Current status:** partially complete; not fully tested.

Current release: None yet

Current pre-release: `1.0.0-SNAPSHOT`

### Using It

See [this Javadoc](https://archiecobbs.github.io/linode-apiv4-java/site/apidocs/index.html?org/dellroad/linode/apiv4/spring/package-summary.html) for an example of how to use this library.

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
```

[API Javadocs](http://archiecobbs.github.io/linode-apiv4-java/site/apidocs/index.html)
