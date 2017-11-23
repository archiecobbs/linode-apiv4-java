
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.dellroad.linode.apiv4.model.Config;
import org.dellroad.linode.apiv4.model.Devices;

/**
 * Used to create Linode instance configs.
 *
 * @see org.dellroad.linode.apiv4.spring.LinodeApiRequestSender
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateConfigRequest {

    private String kernelId;
    private String label;
    private Devices devices;
    private String comments;
    private Integer ramLimit;
    private boolean rootDeviceReadOnly;
    private Config.RunLevel runLevel;
    private Config.VirtMode virtMode;
    private Config.Helpers helpers;

    @JsonProperty("kernel")
    public String getKernelId() {
        return this.kernelId;
    }
    public void setKernelId(final String kernelId) {
        this.kernelId = kernelId;
    }

    public String getLabel() {
        return this.label;
    }
    public void setLabel(final String label) {
        this.label = label;
    }

    public Devices getDevices() {
        return this.devices;
    }
    public void setDevices(final Devices devices) {
        this.devices = devices;
    }

    public String getComments() {
        return this.comments;
    }
    public void setComments(final String comments) {
        this.comments = comments;
    }

    @JsonProperty("ram_limit")
    public Integer getRamLimit() {
        return this.ramLimit;
    }
    public void setRamLimit(final Integer ramLimit) {
        this.ramLimit = ramLimit;
    }

    @JsonProperty("root_device_ro")
    public boolean isRootDeviceReadOnly() {
        return this.rootDeviceReadOnly;
    }
    public void setRootDeviceReadOnly(final boolean rootDeviceReadOnly) {
        this.rootDeviceReadOnly = rootDeviceReadOnly;
    }

    public Config.RunLevel getRunLevel() {
        return this.runLevel;
    }
    public void setRunLevel(final Config.RunLevel runLevel) {
        this.runLevel = runLevel;
    }

    @JsonProperty("virt_mode")
    public Config.VirtMode getVirtMode() {
        return this.virtMode;
    }
    public void setVirtMode(final Config.VirtMode virtMode) {
        this.virtMode = virtMode;
    }

    public Config.Helpers getHelpers() {
        return this.helpers;
    }
    public void setHelpers(final Config.Helpers helpers) {
        this.helpers = helpers;
    }
}
