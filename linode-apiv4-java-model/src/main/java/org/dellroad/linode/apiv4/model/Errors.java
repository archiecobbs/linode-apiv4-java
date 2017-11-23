
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Linode API error payload.
 *
 * @see <a href="https://developers.linode.com/v4/errors">Errors</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Errors {

    private Error[] errors;

    public Error[] getErrors() {
        return this.errors;
    }
    public void setErrors(final Error[] errors) {
        this.errors = errors;
    }

// Error

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {

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
}
