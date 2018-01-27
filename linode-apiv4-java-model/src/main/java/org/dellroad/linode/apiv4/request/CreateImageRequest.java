
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to create disk images.
 *
 * @see org.dellroad.linode.apiv4.spring.LinodeApiRequestSender
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateImageRequest {

    private int diskId;
    private String label;
    private String description;

    @JsonProperty("backup_id")
    public int getDiskId() {
        return this.diskId;
    }
    public void setDiskId(final int diskId) {
        this.diskId = diskId;
    }

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
