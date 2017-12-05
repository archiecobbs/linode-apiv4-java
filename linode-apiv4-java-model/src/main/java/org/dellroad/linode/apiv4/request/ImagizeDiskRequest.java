
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.request;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Used to imagize disks.
 *
 * @see org.dellroad.linode.apiv4.spring.LinodeApiRequestSender
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
