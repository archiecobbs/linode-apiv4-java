
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
import org.dellroad.linode.apiv4.model.Distribution;
import org.dellroad.linode.apiv4.model.IPInfo;
import org.dellroad.linode.apiv4.model.IPv4;
import org.dellroad.linode.apiv4.model.IPv4Info;
import org.dellroad.linode.apiv4.model.IPv6;
import org.dellroad.linode.apiv4.model.IPv6Info;
import org.dellroad.linode.apiv4.model.Linode;
import org.dellroad.linode.apiv4.model.Linodes;
import org.dellroad.linode.apiv4.model.Region;
import org.dellroad.linode.apiv4.model.Regions;
import org.dellroad.linode.apiv4.model.StackScript;
import org.dellroad.linode.apiv4.model.StackScripts;
import org.dellroad.linode.apiv4.model.Stats;
import org.dellroad.linode.apiv4.model.Type;
import org.dellroad.linode.apiv4.model.Types;
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

public class QueryTest {

    private final Random random = new Random();

    private String authToken;
    private ClassPathXmlApplicationContext context;
    private LinodeApiRequestSender sender;
    private ThreadPoolTaskExecutor executor;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @BeforeClass
    @Parameters("authToken")
    public void setup(@Optional String authToken) {

        // Save auth token, if any
        if (authToken != null && authToken.isEmpty())
            authToken = null;
        this.authToken = authToken;

        // Load context
        this.log.debug("creating unit test application context");
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
    }

    @AfterClass
    public void shutdown() {
        this.context.close();
        this.executor.destroy();
    }

    @Test
    public void testError() throws Exception {
        try {
            this.sender.getRegion("nonexistent");
            assert false;
        } catch (LinodeApiException e) {
            assert e.getMessage() != null;
            this.log.info("got expected " + e);
        }
    }

    @Test
    public void testLinodes() throws Exception {
        if (this.authToken == null)
            return;
        for (Linode linode : this.sender.getAllLinodes(this.executor, null)) {

            // Linode
            this.log.info("getAllLinodes(): {}", this.toString(linode));
            linode = this.sender.getLinode(linode.getId());
            this.log.info("getLinode({}): {}", linode.getId(), this.toString(linode));

            // Backups
            final BackupInfo backupInfo = this.sender.getLinodeBackupInfo(linode.getId());
            this.log.info("getLinodeBackupInfo({}): {}", linode.getId(), this.toString(backupInfo));

            // Configs
            for (Config config : this.sender.getAllLinodeConfigs(this.executor, null, linode.getId())) {
                this.log.info("getAllLinodeConfigs(): {}", this.toString(config));
                config = this.sender.getLinodeConfig(linode.getId(), config.getId());
                this.log.info("getLinodeConfig({}): {}", config.getId(), this.toString(config));
            }

            // Disks
            for (Disk disk : this.sender.getAllLinodeDisks(this.executor, null, linode.getId())) {
                this.log.info("getAllLinodeDisks(): {}", this.toString(disk));
                disk = this.sender.getLinodeDisk(linode.getId(), disk.getId());
                this.log.info("getLinodeDisk({}): {}", disk.getId(), this.toString(disk));
            }

            // IPInfo
            final IPInfo ipInfo = this.sender.getIPInfo(linode.getId());
            this.log.info("getLinodeIPInfo({}): {}", linode.getId(), this.toString(ipInfo));
            for (IPv4 ip : ipInfo.getIPv4().getPublic())
                this.log.info("getLinodeIPInfo({}): public: {}", linode.getId(), this.toString(ip));
            for (IPv4 ip : ipInfo.getIPv4().getPrivate())
                this.log.info("getLinodeIPInfo({}): private: {}", linode.getId(), this.toString(ip));
            for (IPv4 ip : ipInfo.getIPv4().getShared())
                this.log.info("getLinodeIPInfo({}): shared: {}", linode.getId(), this.toString(ip));

            // Stats
            final Stats stats = this.sender.getStats(linode.getId());
            this.log.info("getStats(): {}", this.toString(stats));
        }
    }

    @Test
    public void testCreateLinodeError() throws Exception {
        if (this.authToken == null)
            return;

        // Create create request
        final CreateLinodeRequest request = new CreateLinodeRequest();
        request.setTypeId(this.cheapestType().getId());
        request.setRegionId("nonexistent");
        request.setLabel(this.randomLabel());
        request.setGroup(this.unitTestGroup());
        request.setBooted(false);

        // Create it
        this.log.info("creating Linode using " + this.toString(request));
        try {
            this.sender.createLinode(request);
            assert false : "request should have failed!";
        } catch (LinodeApiException e) {
            assert e.getMessage() != null : "didn't set error message";
            this.log.info("got expected " + e);
            assert e.getErrors() != null : "didn't parse JSON error payload";
            this.log.info("got expected errors: " + this.toString(e.getErrors()));
        }
    }

    @Test
    public void testCreateLinode() throws Exception {
        if (this.authToken == null)
            return;

        // Create create request
        final CreateLinodeRequest request = new CreateLinodeRequest();
        request.setTypeId(this.cheapestType().getId());
        request.setRegionId(this.randomRegion().getId());
        request.setLabel(this.randomLabel());
        request.setGroup(this.unitTestGroup());
        request.setBooted(false);

        // Create it
        this.log.info("creating Linode using " + this.toString(request));
        final Linode linode = this.sender.createLinode(request);
        this.log.info("created new Linode " + this.toString(linode));

        // Delete it
        this.log.info("deleting Linode " + linode.getId());
        this.sender.deleteLinode(linode.getId());
    }

    @Test
    public void testRegions() throws Exception {
        for (Region region : this.sender.getAllRegions(this.executor, null)) {
            this.log.info("getRegions(): {}", this.toString(region));
            region = this.sender.getRegion(region.getId());
            this.log.info("getRegion({}): {}", region.getId(), this.toString(region));
        }
    }

    @Test
    public void testFilterDistributions() throws Exception {
        final FilterBuilder fb = new FilterBuilder();
        Distribution prev = null;
        for (Distribution dist : this.sender.getAllDistributions(
          this.executor, fb.where(fb.equal("vendor", "Debian")).orderBy("label").build())) {
            assert dist.getVendor().equals("Debian") : "wrong vendor: \"" + dist.getVendor() + "\" != \"Debian\"";
            if (prev == null)
                prev = dist;
            else {
                assert dist.getLabel().compareTo(prev.getLabel()) >= 0 :
                  "wrong order: \"" + dist.getLabel() + "\" < \"" + prev.getLabel() + "\"";
            }
        }
    }

    @Test
    public void testTypes() throws Exception {
        for (Type type : this.sender.getAllTypes(this.executor, null)) {
            this.log.info("getTypes(): {}", this.toString(type));
            type = this.sender.getType(type.getId());
            this.log.info("getType({}): {}", type.getId(), this.toString(type));
        }
    }

    @Test
    public void testStackScripts() throws Exception {
        this.log.info("getStackScripts() page 1");
        final StackScripts page1 = this.sender.getStackScripts(null, 1);
        this.log.info("getStackScripts() page 2");
        final StackScripts page2 = this.sender.getStackScripts(null, 2);
        this.log.info("getStackScripts() page 3");
        final StackScripts page3 = this.sender.getStackScripts(null, 3);
        List<StackScript> list = new ArrayList<>();
        list.addAll(page1.getData());
        list.addAll(page2.getData());
        list.addAll(page3.getData());
        for (StackScript stackScript : list)
            this.log.info("getStackScript({}): {} length script", stackScript.getId(), stackScript.getScript().length());
    }

    private Region randomRegion() throws InterruptedException {
        final List<Region> regions = this.sender.getAllRegions(this.executor, null);
        return regions.get(this.random.nextInt(regions.size()));
    }

    // Note: result must be between 3 and 32 characters
    private String randomLabel() {
        return this.getClass().getSimpleName() + "-" + (this.random.nextInt() & 0x7fffffff);
    }

    private String unitTestGroup() {
        return this.getClass().getSimpleName() + " Unit Test";
    }

    private Type cheapestType() throws InterruptedException {
        final List<Type> types = this.sender.getAllTypes(this.executor, null);
        Collections.sort(types, Comparator.<Type>comparingDouble(t -> t.getPrice().getHourly()));
        return types.get(0);
    }

    public String toString(Object obj) {
        return new ReflectionToStringBuilder(obj, new MultilineRecursiveToStringStyle()).toString();
    }
}
