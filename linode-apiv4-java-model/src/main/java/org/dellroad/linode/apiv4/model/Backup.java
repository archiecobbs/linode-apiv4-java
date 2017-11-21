
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Date;

import org.dellroad.linode.apiv4.Constants;

/**
 * Linode backup.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#backups">Backups</a>
 */
public class Backup extends AbstractIntIdLabeled {

    private String status;
    private String type;
    private String region;
    private Date created;
    private Date updated;
    private Date finished;
    private String[] configs;
    private Disk[] disks;
    private Availability availability;

    // XXX what is this?
    public String getStatus() {
        return this.status;
    }
    public void setStatus(final String status) {
        this.status = status;
    }

    // XXX what is this?
    public String getType() {
        return this.type;
    }
    public void setType(final String type) {
        this.type = type;
    }

    public String getRegion() {
        return this.region;
    }
    public void setRegion(final String region) {
        this.region = region;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMESTAMP_TIMEZONE)
    public Date getCreated() {
        return this.created;
    }
    public void setCreated(final Date created) {
        this.created = created;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMESTAMP_TIMEZONE)
    public Date getUpdated() {
        return this.updated;
    }
    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMESTAMP_TIMEZONE)
    public Date getFinished() {
        return this.finished;
    }
    public void setFinished(final Date finished) {
        this.finished = finished;
    }

    public String[] getConfigs() {
        return this.configs;
    }
    public void setConfigs(final String[] configs) {
        this.configs = configs;
    }

    public Disk[] getDisks() {
        return this.disks;
    }
    public void setDisks(final Disk[] disks) {
        this.disks = disks;
    }

    public Availability getAvailability() {
        return this.availability;
    }
    public void setAvailability(final Availability availability) {
        this.availability = availability;
    }

// Availability

    /**
     * {@link Backup} availability.
     */
    public enum Availability {
        DAILY,
        WEEKLY,
        SNAPSHOT,
        UNAVAILABLE;

        @JsonCreator
        public static Availability parse(String value) {
            return Availability.valueOf(value.toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
}
