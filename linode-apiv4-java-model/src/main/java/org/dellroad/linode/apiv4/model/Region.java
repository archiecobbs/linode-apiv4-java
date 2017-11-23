
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Linode region.
 *
 * @see <a href="https://developers.linode.com/v4/reference/endpoints/regions">Regions</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Region extends AbstractStringId {

    private String country;

    public String getCountry() {
        return this.country;
    }
    public void setCountry(final String country) {
        this.country = country;
    }
}
