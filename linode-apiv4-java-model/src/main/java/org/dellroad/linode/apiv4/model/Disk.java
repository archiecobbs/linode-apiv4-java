
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import org.dellroad.linode.apiv4.Constants;

/**
 * Linode disk.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#disks">Disks</a>
 */
public class Disk extends AbstractIntIdLabeled {

    private DiskStatus status;
    private int size;
    private Filesystem filesystem;
    private Date created;
    private Date updated;

    public DiskStatus getStatus() {
        return this.status;
    }
    public void setStatus(final DiskStatus status) {
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
}
