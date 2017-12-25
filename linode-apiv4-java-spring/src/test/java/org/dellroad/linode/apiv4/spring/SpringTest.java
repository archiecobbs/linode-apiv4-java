
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.dellroad.linode.apiv4.filter.FilterBuilder;
import org.dellroad.linode.apiv4.model.Backup;
import org.dellroad.linode.apiv4.model.BackupInfo;
import org.dellroad.linode.apiv4.model.Config;
import org.dellroad.linode.apiv4.model.Configs;
import org.dellroad.linode.apiv4.model.Disk;
import org.dellroad.linode.apiv4.model.Disks;
import org.dellroad.linode.apiv4.model.IPInfo;
import org.dellroad.linode.apiv4.model.IPv4;
import org.dellroad.linode.apiv4.model.IPv4Info;
import org.dellroad.linode.apiv4.model.IPv6;
import org.dellroad.linode.apiv4.model.IPv6Info;
import org.dellroad.linode.apiv4.model.Image;
import org.dellroad.linode.apiv4.model.Kernel;
import org.dellroad.linode.apiv4.model.Linode;
import org.dellroad.linode.apiv4.model.Linodes;
import org.dellroad.linode.apiv4.model.Region;
import org.dellroad.linode.apiv4.model.Regions;
import org.dellroad.linode.apiv4.model.StackScript;
import org.dellroad.linode.apiv4.model.StackScripts;
import org.dellroad.linode.apiv4.model.Stats;
import org.dellroad.linode.apiv4.model.Type;
import org.dellroad.linode.apiv4.model.Types;
import org.dellroad.linode.apiv4.model.Volume;
import org.dellroad.linode.apiv4.request.CreateLinodeRequest;
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
    protected ThreadPoolTaskExecutor executor;
    protected LinodeApiRequestSender.AsyncExecutor asyncExecutor;

    private Type cheapestType;

    @BeforeClass
    @Parameters("authToken")
    public void setup(@Optional String authToken) {

        // Save auth token, if any
        if (authToken != null && authToken.isEmpty())
            authToken = null;
        this.authToken = authToken;

        // Logit
        this.log.debug("creating unit test application context");
        if (this.authToken == null)
            this.log.warn("no <authToken> configured - tests requiring authentication will be skipped");

        // Load context
        this.context = new ClassPathXmlApplicationContext("linodeApi.xml", this.getClass());

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
        this.executor = new ThreadPoolTaskExecutor();
        this.executor.setCorePoolSize(3);
        this.executor.setMaxPoolSize(5);
        this.executor.afterPropertiesSet();
        this.asyncExecutor = LinodeApiRequestSender.AsyncExecutor.of(this.executor);
    }

    @Test
    public void verifyAuthToken() {
        if (this.authToken == null)
            throw new RuntimeException("no <authToken> configured - tests requiring authentication cannot be performed");
    }

    @AfterClass
    public void shutdown() {
        this.context.close();
        this.executor.destroy();
    }

    protected Region randomRegion() throws InterruptedException {
        final List<Region> regions = this.sender.getRegions(this.asyncExecutor, MAX_RESULTS, null);
        return regions.get(this.random.nextInt(regions.size()));
    }

    // Note: result must be between 3 and 32 characters
    protected String randomLabel() {
        return this.getClass().getSimpleName() + "-" + (this.random.nextInt() & 0x7fffffff);
    }

    protected String unitTestGroup() {
        return this.getClass().getSimpleName() + " Unit Test";
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
