
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Date;

import org.dellroad.linode.apiv4.Constants;

/**
 * Linode disk.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#disks">Disks</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Disk extends AbstractIntIdLabeled {

    private Status status;
    private int size;
    private Filesystem filesystem;
    private Date created;
    private Date updated;

    public Status getStatus() {
        return this.status;
    }
    public void setStatus(final Status status) {
        this.status = status;
    }

    public int getSize() {
        return this.size;
    }
    public void setSize(final int size) {
        this.size = size;
    }

    public Filesystem getFilesystem() {
        return this.filesystem;
    }
    public void setFilesystem(final Filesystem filesystem) {
        this.filesystem = filesystem;
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

// Status

    /**
     * Linode {@link Disk} status.
     *
     * @see <a href="https://developers.linode.com/v4/reference/linode#disks">Disks</a>
     */
    public enum Status {
        READY,
        NOT_READY,
        DELETING;

        @JsonCreator
        public static Status parse(String value) {
            return Status.valueOf(value.replace(' ', '_').toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return this.name().replace('_', ' ').toLowerCase();
        }
    }
}
