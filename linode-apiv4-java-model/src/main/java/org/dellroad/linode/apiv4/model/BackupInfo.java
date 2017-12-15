
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import org.dellroad.linode.apiv4.Constants;

/**
 * {@link Linode} backup info.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#backups">Backups</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BackupInfo {

    private Backup daily;
    private Backup[] weekly;
    private Snapshot snapshot;
    private Service service;

    public Backup getDaily() {
        return this.daily;
    }
    public void setDaily(final Backup daily) {
        this.daily = daily;
    }

    public Backup[] getWeekly() {
        return this.weekly;
    }
    public void setWeekly(final Backup[] weekly) {
        this.weekly = weekly;
    }

    public Snapshot getSnapshot() {
        return this.snapshot;
    }
    public void setSnapshot(final Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    public Service getService() {
        return this.service;
    }
    public void setService(final Service service) {
        this.service = service;
    }

// Snapshot

    /**
     * {@link BackupInfo} snapshot info.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snapshot {

        private Backup current;
        private Backup inProgress;

        public Backup getCurrent() {
            return this.current;
        }
        public void setCurrent(final Backup current) {
            this.current = current;
        }

        @JsonProperty("in_progress")
        public Backup getInProgress() {
            return this.inProgress;
        }
        public void setInProgress(final Backup inProgress) {
            this.inProgress = inProgress;
        }
    }

// Service

    /**
     * {@link BackupInfo} service info.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Service {

        private boolean enabled;
        private Date updated;

        public boolean isEnabled() {
            return this.enabled;
        }
        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMESTAMP_TIMEZONE)
        public Date getUpdated() {
            return this.updated;
        }
        public void setUpdated(final Date updated) {
            this.updated = updated;
        }
    }
}
