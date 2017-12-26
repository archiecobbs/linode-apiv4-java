
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import java.util.ArrayList;
import java.util.List;

import org.dellroad.linode.apiv4.filter.FilterBuilder;
import org.dellroad.linode.apiv4.model.BackupInfo;
import org.dellroad.linode.apiv4.model.Config;
import org.dellroad.linode.apiv4.model.Disk;
import org.dellroad.linode.apiv4.model.IPInfo;
import org.dellroad.linode.apiv4.model.IPv4;
import org.dellroad.linode.apiv4.model.Image;
import org.dellroad.linode.apiv4.model.Kernel;
import org.dellroad.linode.apiv4.model.Linode;
import org.dellroad.linode.apiv4.model.Region;
import org.dellroad.linode.apiv4.model.StackScript;
import org.dellroad.linode.apiv4.model.StackScripts;
import org.dellroad.linode.apiv4.model.Stats;
import org.dellroad.linode.apiv4.model.Type;
import org.dellroad.linode.apiv4.model.Volume;
import org.dellroad.linode.apiv4.request.CreateLinodeRequest;
import org.testng.annotations.Test;

public class QueryTest extends SpringTest {

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

// getLinodes(), getLinodeBackupInfo(), getLinodeConfigs(), getLinodeDisks(), getLinodeVolumes(), getIPInfo(), getStats()

    @Test(dependsOnMethods = "verifyAuthToken")
    public void testLinodes() throws Exception {
        for (Linode linode : this.sender.getLinodes(this.asyncExecutor, MAX_RESULTS, null)) {

            // Linode
            this.log.info("getLinodes(): {}", this.toString(linode));
            linode = this.sender.getLinode(linode.getId());
            this.log.info("getLinode({}): {}", linode.getId(), this.toString(linode));

            // Backup info
            final BackupInfo backupInfo = this.sender.getLinodeBackupInfo(linode.getId());
            this.log.info("getLinodeBackupInfo({}): {}", linode.getId(), this.toString(backupInfo));

            // Configs
            for (Config config : this.sender.getLinodeConfigs(this.asyncExecutor, MAX_RESULTS, null, linode.getId())) {
                this.log.info("getLinodeConfigs(): {}", this.toString(config));
                config = this.sender.getLinodeConfig(linode.getId(), config.getId());
                this.log.info("getLinodeConfig({}): {}", config.getId(), this.toString(config));
            }

            // Disks
            for (Disk disk : this.sender.getLinodeDisks(this.asyncExecutor, MAX_RESULTS, null, linode.getId())) {
                this.log.info("getLinodeDisks(): {}", this.toString(disk));
                disk = this.sender.getLinodeDisk(linode.getId(), disk.getId());
                this.log.info("getLinodeDisk({}): {}", disk.getId(), this.toString(disk));
            }

            // Volumes
            for (Volume volume : this.sender.getLinodeVolumes(this.asyncExecutor, MAX_RESULTS, null, linode.getId())) {
                this.log.info("getLinodeVolumes(): {}", this.toString(volume));
                volume = this.sender.getVolume(volume.getId());
                this.log.info("getVolume({}): {}", volume.getId(), this.toString(volume));
            }

            // IPInfo
            final IPInfo ipInfo = this.sender.getIPInfo(linode.getId());
            this.log.info("getIPInfo({}): {}", linode.getId(), this.toString(ipInfo));
            for (IPv4 ip : ipInfo.getIPv4().getPublic())
                this.log.info("getIPInfo({}): public: {}", linode.getId(), this.toString(ip));
            for (IPv4 ip : ipInfo.getIPv4().getPrivate())
                this.log.info("getIPInfo({}): private: {}", linode.getId(), this.toString(ip));
            for (IPv4 ip : ipInfo.getIPv4().getShared())
                this.log.info("getIPInfo({}): shared: {}", linode.getId(), this.toString(ip));

            // Stats
            try {
                final Stats stats = this.sender.getStats(linode.getId());
                this.log.info("getStats(): {}", this.toString(stats));
            } catch (LinodeApiException e) {
                if (!e.getMessage().contains("unavailable at this time"))   // happens with new linodes
                    throw e;
            }
        }
    }

// createLinode() - error

    @Test(dependsOnMethods = "verifyAuthToken")
    public void testCreateLinodeError() throws Exception {

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

// createLinode(), updateLinode(), deleteLinode(),

    @Test(dependsOnMethods = "verifyAuthToken")
    public void testCreateLinode() throws Exception {

        // Create create request
        final CreateLinodeRequest createRequest = new CreateLinodeRequest();
        createRequest.setTypeId(this.cheapestType().getId());
        createRequest.setRegionId(this.randomRegion().getId());
        createRequest.setLabel(this.randomLabel());
        createRequest.setGroup(this.unitTestGroup());
        createRequest.setBooted(false);

        // Create it
        this.log.info("creating Linode using " + this.toString(createRequest));
        final Linode linode = this.sender.createLinode(createRequest);
        this.log.info("created new Linode " + this.toString(linode));

        // Update it
        final Linode updateRequest = new Linode();
        updateRequest.setId(linode.getId());
        updateRequest.setLabel(this.randomLabel());
        this.log.info("updating Linode using " + this.toString(updateRequest));
        this.sender.updateLinode(updateRequest);

        // Delete it
        this.log.info("deleting Linode " + linode.getId());
        this.sender.deleteLinode(linode.getId());
    }

// bootLinode() - TODO
// cloneLinode() - TODO
// kvmifyLinode() - TODO
// mutateLinode() - TODO
// rebootLinode() - TODO
// rebuildLinode() - TODO
// rescueLinode() - TODO
// resizeLinode() - TODO
// shutdownLinode() - TODO
// restoreBackup() - TODO
// enableBackup() - TODO
// cancelBackup() - TODO

// createLinodeConfig() - TODO
// updateLinodeConfig() - TODO
// deleteLinodeConfig() - TODO

// createLinodeDisk() - TODO
// updateLinodeDisk() - TODO
// deleteLinodeDisk() - TODO
// updateLinodeDisk() - TODO
// updateLinodeDiskPassword() - TODO

// allocateIP()
// getIP()
// updateIP()
// deleteIP()

// getKernels()

    @Test
    public void testKernels() throws Exception {
        int count = 0;
        for (Kernel kernel : this.sender.getKernels(this.asyncExecutor, MAX_RESULTS, null)) {
            this.log.info("getKernels(): {}", this.toString(kernel));
            if (kernel.getVersion().equals("2.6.18"))      // avoid weird bug
                continue;
            kernel = this.sender.getKernel(kernel.getId());
            this.log.info("getKernel({}): {}", kernel.getId(), this.toString(kernel));
            if (++count == 10)                              // avoid API overload
                continue;
        }
    }

// getStackScripts()

    @Test
    public void testStackScripts() throws Exception {
        this.log.info("getStackScripts() page 1");
        final StackScripts page1 = this.sender.getStackScriptsPage(null, 1);
        this.log.info("getStackScripts() page 2");
        final StackScripts page2 = this.sender.getStackScriptsPage(null, 2);
        this.log.info("getStackScripts() page 3");
        final StackScripts page3 = this.sender.getStackScriptsPage(null, 3);
        List<StackScript> list = new ArrayList<>();
        list.addAll(page1.getData());
        list.addAll(page2.getData());
        list.addAll(page3.getData());
        for (StackScript stackScript : list)
            this.log.info("getStackScript({}): {} length script", stackScript.getId(), stackScript.getScript().length());
    }

// getTypes()

    @Test
    public void testTypes() throws Exception {
        for (Type type : this.sender.getTypes(this.asyncExecutor, MAX_RESULTS, null)) {
            this.log.info("getTypes(): {}", this.toString(type));
            type = this.sender.getType(type.getId());
            this.log.info("getType({}): {}", type.getId(), this.toString(type));
        }
    }

// getVolumes()

    @Test(dependsOnMethods = "verifyAuthToken")
    public void testVolumes() throws Exception {
        for (Volume volume : this.sender.getVolumes(this.asyncExecutor, MAX_RESULTS, null)) {
            this.log.info("getVolumes(): {}", this.toString(volume));
            volume = this.sender.getVolume(volume.getId());
            this.log.info("getVolume({}): {}", volume.getId(), this.toString(volume));
        }
    }

// createVolume() - TODO
// deleteVolume() - TODO
// attachVolume() - TODO
// cloneVolume() - TODO

// Domains - TODO
// Longview - TODO
// NodeBalancers - TODO
// Networking - TODO

// getRegions()

    @Test
    public void testRegions() throws Exception {
        for (Region region : this.sender.getRegions(this.asyncExecutor, MAX_RESULTS, null)) {
            this.log.info("getRegions(): {}", this.toString(region));
            region = this.sender.getRegion(region.getId());
            this.log.info("getRegion({}): {}", region.getId(), this.toString(region));
        }
    }

// Support - TODO
// Account - TODO
// Profile - TODO

// getImages()

    @Test
    public void testImages() throws Exception {
        for (Image image : this.sender.getImages(this.asyncExecutor, MAX_RESULTS, null)) {
            this.log.info("getImages(): {}", this.toString(image));
            image = this.sender.getImage(image.getId());
            this.log.info("getImage({}): {}", image.getId(), this.toString(image));
        }
    }

    @Test
    public void testFilterImages() throws Exception {
        final FilterBuilder fb = new FilterBuilder();
        Image prev = null;
        for (Image image : this.sender.getImages(this.asyncExecutor, MAX_RESULTS,
          fb.where(fb.and(fb.equal("public", true), fb.equal("vendor", "Debian"))).orderBy("label").build())) {
            assert image.getVendor().equals("Debian") : "wrong vendor: \"" + image.getVendor() + "\" != \"Debian\"";
            if (prev == null)
                prev = image;
            else {
                assert image.getLabel().compareTo(prev.getLabel()) >= 0 :
                  "wrong order: \"" + image.getLabel() + "\" < \"" + prev.getLabel() + "\"";
            }
        }
    }

// deleteImage() - TODO
}
