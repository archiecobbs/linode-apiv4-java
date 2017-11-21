
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Linode API error.
 *
 * @see <a href="https://developers.linode.com/v4/errors">Errors</a>
 */
public class Error {

    private String field;
    private String reason;

    public String getField() {
        return this.field;
    }
    public void setField(final String field) {
        this.field = field;
    }

    public String getReason() {
        return this.reason;
    }
    public void setReason(final String reason) {
        this.reason = reason;
    }
}
