
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
 * Linode image.
 *
 * <p>
 * Also used to represent distributions.
 *
 * @see <a href="https://developers.linode.com/v4/reference/images">Images</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image extends AbstractStringIdLabeled {

    private String description;
    private Status status;
    private Filesystem filesystem;
    private Date created;
    private Date updated;
    private Type type;
    private boolean publicx;
    private boolean deprecated;
    private Date lastUsed;
    private int minDeploySize;
    private String creator;
    private String vendor;

    public String getDescription() {
        return this.description;
    }
    public void setDescription(final String description) {
        this.description = description;
    }

    public Status getStatus() {
        return this.status;
    }
    public void setStatus(final Status status) {
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

    public boolean isDeprecated() {
        return this.deprecated;
    }
    public void setDeprecated(final boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Type getType() {
        return this.type;
    }
    public void setType(final Type type) {
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

    @JsonProperty("min_deploy_size")
    public int getMinDeploySize() {
        return this.minDeploySize;
    }
    public void setMinDeploySize(final int minDeploySize) {
        this.minDeploySize = minDeploySize;
    }

    public String getCreator() {
        return this.creator;
    }
    public void setCreator(final String creator) {
        this.creator = creator;
    }

    public String getVendor() {
        return this.vendor;
    }
    public void setVendor(final String vendor) {
        this.vendor = vendor;
    }

// Status

    /**
     * {@link Image} status.
     */
    public enum Status {
        CREATING,
        AVAILABLE,
        DELETED;

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
     * {@link Image} type.
     */
    public enum Type {
        MANUAL,
        AUTOMATIC;          // XXX unconfirmed

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
