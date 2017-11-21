
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model.request;

/**
 * Used to imagize disks.
 *
 * @see org.dellroad.linode.apiv4.spring.LinodeApiRequestSender
 */
public class ImagizeDiskRequest {

    private String label;
    private String description;

    public String getLabel() {
        return this.label;
    }
    public void setLabel(final String label) {
        this.label = label;
    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription(final String description) {
        this.description = description;
    }
}
