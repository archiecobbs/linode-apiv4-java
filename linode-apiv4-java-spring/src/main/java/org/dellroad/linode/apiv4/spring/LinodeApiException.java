
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import org.dellroad.linode.apiv4.model.Errors;
import org.springframework.web.client.RestClientException;

/**
 * Thrown when an error occurs while communicating with the Linode API.
 */
@SuppressWarnings("serial")
public class LinodeApiException extends RestClientException {

    private Errors errors;

    public LinodeApiException(String message) {
        super(message);
    }

    public LinodeApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Get the {@link Errors} returned from the API, if any.
     *
     * @return errors detailed in the HTTP response, if any, otherwise null
     */
    public Errors getErrors() {
        return this.errors;
    }
    public void setErrors(final Errors errors) {
        this.errors = errors;
    }
}
