
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Date;

import org.dellroad.linode.apiv4.Constants;

/**
 * Linode image.
 *
 * @see <a href="https://developers.linode.com/v4/reference/images">Images</a>
 */
public class Image extends AbstractIntIdLabeled {

    private String description;
    private String status;
    private Filesystem filesystem;
    private Date created;
    private Date updated;
    private String type;
    private boolean publicx;
    private Date lastUsed;

    public String getDescription() {
        return this.description;
    }
    public void setDescription(final String description) {
        this.description = description;
    }

    // XXX - what is this??
    public String getStatus() {
        return this.status;
    }
    public void setStatus(final String status) {
        this.status = status;
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

    @JsonProperty("is_public")
    public boolean isPublic() {
        return this.publicx;
    }
    public void setPublic(final boolean publicx) {
        this.publicx = publicx;
    }

    // XXX - what is this??
    public String getType() {
        return this.type;
    }
    public void setType(final String type) {
        this.type = type;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMESTAMP_TIMEZONE)
    @JsonProperty("last_used")
    public Date getLastUsed() {
        return this.lastUsed;
    }
    public void setLastUsed(final Date lastUsed) {
        this.lastUsed = lastUsed;
    }

// Status

    /**
     * {@link Linode} status. XXX incomplete
     */
    public enum Status {
        AVAILABLE;

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

// Type

    /**
     * {@link Image} type. XXX incomplete
     */
    public enum Type {
        MANUAL;

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
