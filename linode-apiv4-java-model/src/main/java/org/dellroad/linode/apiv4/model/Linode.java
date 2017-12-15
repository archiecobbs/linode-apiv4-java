
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Date;

import org.dellroad.linode.apiv4.Constants;

/**
 * Linode instance.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode">Linodes</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Linode extends AbstractIntIdLabeled {

    private Alerts alerts;
    private Backups backups;
    private Date created;
    private String regionId;
    private String imageId;
    private String group;
    private String[] ipv4;
    private String ipv6;
    private String typeId;
    private Status status;
    private Date updated;
    private Hypervisor hypervisor;
    private Specs specs;

    public Alerts getAlerts() {
        return this.alerts;
    }
    public void setAlerts(final Alerts alerts) {
        this.alerts = alerts;
    }

    public Backups getBackups() {
        return this.backups;
    }
    public void setBackups(final Backups backups) {
        this.backups = backups;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMESTAMP_TIMEZONE)
    public Date getCreated() {
        return this.created;
    }
    public void setCreated(final Date created) {
        this.created = created;
    }

    @JsonProperty("region")
    public String getRegionId() {
        return this.regionId;
    }
    public void setRegionId(final String regionId) {
        this.regionId = regionId;
    }

    @JsonProperty("image")
    public String getImageId() {
        return this.imageId;
    }
    public void setImageId(final String imageId) {
        this.imageId = imageId;
    }

    public String getGroup() {
        return this.group;
    }
    public void setGroup(final String group) {
        this.group = group;
    }

    public String[] getIpv4() {
        return this.ipv4;
    }
    public void setIpv4(final String[] ipv4) {
        this.ipv4 = ipv4;
    }

    public String getIpv6() {
        return this.ipv6;
    }
    public void setIpv6(final String ipv6) {
        this.ipv6 = ipv6;
    }

    @JsonProperty("type")
    public String getTypeId() {
        return this.typeId;
    }
    public void setTypeId(final String typeId) {
        this.typeId = typeId;
    }

    public Status getStatus() {
        return this.status;
    }
    public void setStatus(final Status status) {
        this.status = status;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMESTAMP_TIMEZONE)
    public Date getUpdated() {
        return this.updated;
    }
    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    public Hypervisor getHypervisor() {
        return this.hypervisor;
    }
    public void setHypervisor(final Hypervisor hypervisor) {
        this.hypervisor = hypervisor;
    }

    public Specs getSpecs() {
        return this.specs;
    }
    public void setSpecs(final Specs specs) {
        this.specs = specs;
    }

// Status

    /**
     * {@link Linode} status.
     */
    public enum Status {
        OFFLINE,
        BOOTING,
        RUNNING,
        SHUTTING_DOWN,
        REBOOTING,
        PROVISIONING,
        DELETING,
        MIGRATING;

        @JsonCreator
        public static Status parse(String value) {
            return Status.valueOf(value.toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

// Alerts

    /**
     * {@link Linode} alert thresholds.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Alerts {

        private int cpu;
        private int io;
        private int networkIn;
        private int networkOut;
        private int transferQuota;

        public int getCpu() {
            return this.cpu;
        }
        public void setCpu(final int cpu) {
            this.cpu = cpu;
        }

        public int getIo() {
            return this.io;
        }
        public void setIo(final int io) {
            this.io = io;
        }

        @JsonProperty("network_in")
        public int getNetworkIn() {
            return this.networkIn;
        }
        public void setNetworkIn(final int networkIn) {
            this.networkIn = networkIn;
        }

        @JsonProperty("network_out")
        public int getNetworkOut() {
            return this.networkOut;
        }
        public void setNetworkOut(final int networkOut) {
            this.networkOut = networkOut;
        }

        @JsonProperty("transfer_quota")
        public int getTransferQuota() {
            return this.transferQuota;
        }
        public void setTransferQuota(final int transferQuota) {
            this.transferQuota = transferQuota;
        }
    }

// Hypervisor

    /**
     * {@link Linode} hypervisor.
     */
    public enum Hypervisor {
        KVM,
        XEN;

        @JsonCreator
        public static Hypervisor parse(String value) {
            return Hypervisor.valueOf(value.toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * {@link Linode} backup config.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Backups {

        private boolean enabled;
        private Schedule schedule;
        private Backup lastBackup;
        private Backup snapshot;

        public boolean isEnabled() {
            return this.enabled;
        }
        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }

        public Schedule getSchedule() {
            return this.schedule;
        }
        public void setSchedule(final Schedule schedule) {
            this.schedule = schedule;
        }

        @JsonProperty("last_backup")
        public Backup getLastBackup() {
            return this.lastBackup;
        }
        public void setLastBackup(final Backup lastBackup) {
            this.lastBackup = lastBackup;
        }

        public Backup getSnapshot() {
            return this.snapshot;
        }
        public void setSnapshot(final Backup snapshot) {
            this.snapshot = snapshot;
        }

    // Schedule

        /**
         * {@link Linode.Backups} schedule.
         */
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Schedule {

            private String day;
            private String window;

            public String getDay() {
                return this.day;
            }
            public void setDay(final String day) {
                this.day = day;
            }

            public String getWindow() {
                return this.window;
            }
            public void setWindow(final String window) {
                this.window = window;
            }
        }
    }
}
