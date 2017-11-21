
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.dellroad.linode.apiv4.model.Filesystem;

/**
 * Used to create Linode disks.
 *
 * @see org.dellroad.linode.apiv4.spring.LinodeApiRequestSender
 */
public class CreateDiskRequest extends AbstractDiskRequest {

    private int size;
    private Filesystem filesystem;
    private boolean readOnly;

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

    @JsonProperty("read_only")
    public boolean isReadOnly() {
        return this.readOnly;
    }
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }
}
