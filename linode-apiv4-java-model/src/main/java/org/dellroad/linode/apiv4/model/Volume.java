
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
 * Linode attached volume.
 *
 * @see <a href="https://developers.linode.com/v4/reference/endpoints/linode/instances/$id/volumes">Volumes</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Volume extends AbstractIntIdLabeled {

    private Status status;
    private int size;
    private String regionId;
    private Date created;
    private Date updated;
    private int linodeId;

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

    @JsonProperty("linode_id")
    public int getLinodeId() {
        return this.linodeId;
    }
    public void setLinodeId(final int linodeId) {
        this.linodeId = linodeId;
    }

    /**
     * Linode volume status.
     *
     * @see <a href="https://developers.linode.com/v4/reference/endpoints/linode/instances/$id/volumes">Volumes</a>
     */
    public enum Status {
        CREATING,
        ACTIVE,
        RESIZING,
        OFFLINE;

        @JsonCreator
        public static Status parse(String value) {
            return Status.valueOf(value.toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
}
