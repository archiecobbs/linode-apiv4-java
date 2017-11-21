
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import org.dellroad.linode.apiv4.Constants;

/**
 * Linode Linux distribution.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#distributions">Distributions</a>
 */
public class Distribution extends AbstractStringId {

    private Date updated;
    private String label;
    private int diskMinimum;
    private boolean deprecated;
    private String vendor;
    private Architecture architecture;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMESTAMP_TIMEZONE)
    public Date getUpdated() {
        return this.updated;
    }
    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    public String getLabel() {
        return this.label;
    }
    public void setLabel(final String label) {
        this.label = label;
    }

    @JsonProperty("disk_minimum")
    public int getDiskMinimum() {
        return this.diskMinimum;
    }
    public void setDiskMinimum(final int diskMinimum) {
        this.diskMinimum = diskMinimum;
    }

    public boolean isDeprecated() {
        return this.deprecated;
    }
    public void setDeprecated(final boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getVendor() {
        return this.vendor;
    }
    public void setVendor(final String vendor) {
        this.vendor = vendor;
    }

    public Architecture getArchitecture() {
        return this.architecture;
    }
    public void setArchitecture(final Architecture architecture) {
        this.architecture = architecture;
    }
}
