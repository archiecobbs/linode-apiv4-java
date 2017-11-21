
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import org.springframework.web.client.RestClientException;

/**
 * Thrown when an error occurs while communicating with the Linode API.
 */
@SuppressWarnings("serial")
public class LinodeApiException extends RestClientException {

    public LinodeApiException(String message) {
        super(message);
    }

    public LinodeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
