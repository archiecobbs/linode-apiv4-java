
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
 * Linode backup.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#backups">Backups</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Backup extends AbstractIntIdLabeled {

    private Status status;
    private Type type;
    private String regionId;
    private Date created;
    private Date updated;
    private Date finished;
    private String[] configs;
    private Disk[] disks;
    private Availability availability;

    public Status getStatus() {
        return this.status;
    }
    public void setStatus(final Status status) {
        this.status = status;
    }

    public Type getType() {
        return this.type;
    }
    public void setType(final Type type) {
        this.type = type;
    }

    @JsonProperty("region")
    public String getRegionId() {
        return this.regionId;
    }
    public void setRegionId(final String regionId) {
        this.regionId = regionId;
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

// Status

    /**
     * {@link Backup} status.
     */
    public enum Status {
        PAUSED,
        PENDING,
        RUNNING,
        NEEDS_POST_PROCESSING,
        FAILED,
        USER_ABORTED;

        @JsonCreator
        public static Status parse(String value) {
            return Status.valueOf(value.replaceAll("([A-Z])", "_$1").toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            final String name = this.name();
            StringBuilder buf = new StringBuilder(name.length());
            for (int i = 0; i < name.length(); i++) {
                final char ch = name.charAt(0);
                if (ch == '_')
                    buf.append(name.charAt(++i));
                else
                    buf.append(Character.toLowerCase(ch));
            }
            return buf.toString();
        }
    }

// Type

    /**
     * {@link Backup} type.
     */
    public enum Type {
        AUTO,
        SNAPSHOT;

        @JsonCreator
        public static Type parse(String value) {
            return Type.valueOf(value.toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
}
