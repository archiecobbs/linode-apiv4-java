
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.dellroad.linode.apiv4.model.Linode;
import org.dellroad.linode.apiv4.model.Linodes;
import org.dellroad.linode.apiv4.model.Region;
import org.dellroad.linode.apiv4.model.Regions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class QueryTest {

    private String authToken;
    private ClassPathXmlApplicationContext context;
    private LinodeApiRequestSender sender;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @BeforeClass
    @Parameters("authToken")
    public void setup(@Optional String authToken) {

        // Save auth token, if any
        if (authToken != null && authToken.isEmpty())
            authToken = null;
        this.authToken = authToken;

        // Load context
        this.context = new ClassPathXmlApplicationContext("linodeApi.xml", this.getClass());
        this.context.refresh();

        // Get request sender
        this.sender = this.context.getBean("linodeApiRequestSender", LinodeApiRequestSender.class);

        // Configure auth token
        if (this.authToken != null) {
            this.context.getBean("linodeApiHttpClientManager", LinodeApiHttpClientManager.class)
              .setAuthorizationToken(this.authToken);
        }
    }

    @AfterClass
    public void shutdown() {
        this.context.close();
    }

    @Test
    public void testLinodes() throws Exception {
        if (this.authToken == null)
            return;
        final Linodes linodes = this.sender.getLinodes(1);
        this.log.info("getLinodes(): {}", this.toString(linodes));
        for (Linode linode : linodes) {
            linode = this.sender.getLinode(linode.getId());
            this.log.info("getLinode({}): {}", linode.getId(), this.toString(linode));
            // ...
        }
    }

    @Test
    public void testRegions() throws Exception {
        final Regions regions = this.sender.getRegions(1);
        this.log.info("getRegions(): {}", this.toString(regions));
        for (Region region : regions) {
            region = this.sender.getRegion(region.getId());
            this.log.info("getRegion({}): {}", region.getId(), this.toString(region));
        }
    }

    public String toString(Object obj) {
        return ReflectionToStringBuilder.toString(obj, ToStringStyle.MULTI_LINE_STYLE);
    }
}
