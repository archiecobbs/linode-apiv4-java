
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link Linode} backup info.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#backups">Backups</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BackupInfo {

    private Backup[] automatic;
    private Snapshot snapshot;

    public Backup[] getAutomatic() {
        return this.automatic;
    }
    public void seAutomatic(final Backup[] automatic) {
        this.automatic = automatic;
    }

    public Snapshot getSnapshot() {
        return this.snapshot;
    }
    public void setSnapshot(final Snapshot snapshot) {
        this.snapshot = snapshot;
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
}
