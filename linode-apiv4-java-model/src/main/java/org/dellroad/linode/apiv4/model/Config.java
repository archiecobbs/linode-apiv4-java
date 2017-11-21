
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Date;

import org.dellroad.linode.apiv4.Constants;

/**
 * Linode config.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#configs">Configs</a>
 */
public class Config extends AbstractIntIdLabeled {

    private String comments;
    private Date created;
    private Devices devices;
    private Helpers helpers;
    private Integer initrd;
    private String kernel;
    private Integer memoryLimit;
    private String rootDevice;
    private RunLevel runLevel;
    private VirtMode virtMode;
    private Date updated;

    public String getComments() {
        return this.comments;
    }
    public void setComments(final String comments) {
        this.comments = comments;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMESTAMP_TIMEZONE)
    public Date getCreated() {
        return this.created;
    }
    public void setCreated(final Date created) {
        this.created = created;
    }

    public Devices getDevices() {
        return this.devices;
    }
    public void setDevices(final Devices devices) {
        this.devices = devices;
    }

    public Helpers getHelpers() {
        return this.helpers;
    }
    public void setHelpers(final Helpers helpers) {
        this.helpers = helpers;
    }

    public Integer getInitrd() {
        return this.initrd;
    }
    public void setInitrd(final Integer initrd) {
        this.initrd = initrd;
    }

    public String getKernel() {
        return this.kernel;
    }
    public void setKernel(final String kernel) {
        this.kernel = kernel;
    }

    @JsonProperty("memory_limit")
    public Integer getMemoryLimit() {
        return this.memoryLimit;
    }
    public void setMemoryLimit(final Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    @JsonProperty("root_device")
    public String getRootDevice() {
        return this.rootDevice;
    }
    public void setRootDevice(final String rootDevice) {
        this.rootDevice = rootDevice;
    }

    public RunLevel getRunLevel() {
        return this.runLevel;
    }
    public void setRunLevel(final RunLevel runLevel) {
        this.runLevel = runLevel;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMESTAMP_TIMEZONE)
    public Date getUpdated() {
        return this.updated;
    }
    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    @JsonProperty("virt_mode")
    public VirtMode getVirtMode() {
        return this.virtMode;
    }
    public void setVirtMode(final VirtMode virtMode) {
        this.virtMode = virtMode;
    }

// Helpers

    /**
     * {@link Config} helpers.
     */
    public static class Helpers {

        private boolean updateDbDisabled;
        private boolean distro;
        private boolean modulesDep;
        private boolean network;
        private boolean devTmpFsAutomount;

        @JsonProperty("updatedb_disabled")
        public boolean isUpdateDbDisabled() {
            return this.updateDbDisabled;
        }
        public void setUpdateDbDisabled(final boolean updateDbDisabled) {
            this.updateDbDisabled = updateDbDisabled;
        }

        public boolean isDistro() {
            return this.distro;
        }
        public void setDistro(final boolean distro) {
            this.distro = distro;
        }

        @JsonProperty("modules_dep")
        public boolean isModulesDep() {
            return this.modulesDep;
        }
        public void setModulesDep(final boolean modulesDep) {
            this.modulesDep = modulesDep;
        }

        public boolean isNetwork() {
            return this.network;
        }
        public void setNetwork(final boolean network) {
            this.network = network;
        }

        @JsonProperty("devtmpfs_automount")
        public boolean isDevTmpFsAutomount() {
            return this.devTmpFsAutomount;
        }
        public void setDevTmpFsAutomount(final boolean devTmpFsAutomount) {
            this.devTmpFsAutomount = devTmpFsAutomount;
        }
    }

// RunLevel

    /**
     * {@link Config} startup run level.
     */
    public enum RunLevel {
        DEFAULT,
        SINGLE,
        BINBASH;

        @JsonCreator
        public static RunLevel parse(String value) {
            return RunLevel.valueOf(value.toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

// VirtMode

    /**
     * {@link Config} virtualization mode.
     */
    public enum VirtMode {
        FULLVIRT,
        PARAVIRT;

        @JsonCreator
        public static VirtMode parse(String value) {
            return VirtMode.valueOf(value.toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
}
