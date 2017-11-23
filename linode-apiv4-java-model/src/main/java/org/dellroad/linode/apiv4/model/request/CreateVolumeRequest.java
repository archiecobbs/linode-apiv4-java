
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to create Linode volumes.
 *
 * @see org.dellroad.linode.apiv4.spring.LinodeApiRequestSender
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateVolumeRequest {

    private String label;
    private String region;
    private Integer size;
    private Integer linodeId;
    private Integer configId;

    public String getLabel() {
        return this.label;
    }
    public void setLabel(final String label) {
        this.label = label;
    }

    public String getRegion() {
        return this.region;
    }
    public void setRegion(final String region) {
        this.region = region;
    }

    public Integer getSize() {
        return this.size;
    }
    public void setSize(final Integer size) {
        this.size = size;
    }

    @JsonProperty("linode_id")
    public Integer getLinodeId() {
        return this.linodeId;
    }
    public void setLinodeId(final Integer linodeId) {
        this.linodeId = linodeId;
    }

    @JsonProperty("config_id")
    public Integer getConfigId() {
        return this.configId;
    }
    public void setConfigId(final Integer configId) {
        this.configId = configId;
    }
}
