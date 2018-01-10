
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.dellroad.linode.apiv4.filter.FilterBuilder;
import org.dellroad.linode.apiv4.model.Image;
import org.dellroad.linode.apiv4.model.Region;
import org.dellroad.linode.apiv4.model.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Support superclass for unit tests that rely on the standard Spring context being setup.
 */
public abstract class SpringTest {

    public static final int MAX_RESULTS = 17;

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final Random random = new Random();

    protected String authToken;
    protected ClassPathXmlApplicationContext context;
    protected LinodeApiRequestSender sender;
    protected ThreadPoolTaskExecutor taskExecutor;
    protected LinodeApiRequestSender.AsyncExecutor asyncExecutor;

    private Type cheapestType;

    @BeforeClass
    @Parameters("authToken")
    public void setupContext(@Optional String authToken) {

        // Save auth token, if any
        if (authToken != null && authToken.isEmpty())
            authToken = null;
        this.authToken = authToken;

        // Logit
        this.log.debug("creating unit test application context");
        if (this.authToken == null)
            this.log.warn("no <authToken> configured - tests requiring authentication will be skipped");

        // Load context
        this.context = new ClassPathXmlApplicationContext("/org/dellroad/linode/apiv4/spring/linodeApi.xml", this.getClass());

        // Get request sender
        this.sender = this.context.getBean("linodeApiRequestSender", LinodeApiRequestSender.class);

        // Configure auth token
        if (this.authToken != null) {
            this.log.info("configuring authorization token for Linode API unit tests");
            final LinodeApiHttpRequestFactory requestFactory
              = this.context.getBean("linodeApiHttpRequestFactory", LinodeApiHttpRequestFactory.class);
            requestFactory.setAuthorizationToken(this.authToken);
        }

        // Setup executor
        this.taskExecutor = new ThreadPoolTaskExecutor();
        this.taskExecutor.setCorePoolSize(3);
        this.taskExecutor.setMaxPoolSize(5);
        this.taskExecutor.afterPropertiesSet();
        this.asyncExecutor = LinodeApiRequestSender.AsyncExecutor.of(this.taskExecutor);
    }

    @Test
    public void verifyAuthToken() {
        if (this.authToken == null)
            throw new RuntimeException("no <authToken> configured - tests requiring authentication cannot be performed");
    }

    @AfterClass
    public void shutdownContext() {
        this.context.close();
        this.taskExecutor.destroy();
    }

    // Pick a random region in the US
    protected Region randomRegion() throws InterruptedException {
        final List<Region> regions = this.sender.getRegions(this.asyncExecutor, MAX_RESULTS, null);
        for (Iterator<Region> i = regions.iterator(); i.hasNext(); ) {
            if (!i.next().getCountry().equalsIgnoreCase("US"))
                i.remove();
        }
        return regions.get(this.random.nextInt(regions.size()));
    }

    // Note: result must be between 3 and 32 characters
    protected String randomLabel() {
        return this.getClass().getSimpleName() + "-" + (this.random.nextInt() & 0x7fffffff);
    }

    protected String unitTestGroup() {
        return this.getClass().getSimpleName() + " Unit Test";
    }

    protected Image newestPublicVendorImage(String vendor) throws InterruptedException {
        final FilterBuilder fb = new FilterBuilder();
        this.log.info("finding the most recent public image from vendor \"{}\"", vendor);
        final List<Image> images;
        if (false)                                      //TODO - API is broken, returns "Cannot filter on vendor"
            images = this.sender.getImages(this.asyncExecutor, 0, fb.where(fb.equal("vendor", vendor)).build());
        else {
            images = this.sender.getImages(this.asyncExecutor, 0, null);
            for (Iterator<Image> i = images.iterator(); i.hasNext(); ) {
                if (!i.next().getVendor().equals(vendor))
                    i.remove();
            }
        }
        if (images.isEmpty())
            throw new RuntimeException("no images found matching vendor \"" + vendor + "\"");
        Collections.sort(images,
          Comparator.comparing(Image::isPublic, ((Comparator<Boolean>)Boolean::compare).reversed())
          .thenComparing(Image::isDeprecated)
          .thenComparing(Image::getId));
        final Image image = images.get(0);
        this.log.info("the most recent public image from vendor \"{}\" is \"{}\"", vendor, image.getId());
        return image;
    }

    protected Type cheapestType() throws InterruptedException {
        if (this.cheapestType == null) {
            final List<Type> types = this.sender.getTypes(this.asyncExecutor, 0, null);
            Collections.sort(types, Comparator.<Type>comparingDouble(t -> t.getPrice().getHourly()));
            this.cheapestType = types.get(0);
        }
        return this.cheapestType;
    }

    protected String toString(Object obj) {
        return new ReflectionToStringBuilder(obj, new MultilineRecursiveToStringStyle()).toString();
    }
}
