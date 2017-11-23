
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

import org.dellroad.linode.apiv4.model.Errors;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * Extension of Spring's {@link DefaultResponseErrorHandler} that decodes the JSON error payload
 * in HTTP 400 and 500 series errors so it can be included in the thrown exception.
 */
public class LinodeApiErrorHandler extends DefaultResponseErrorHandler {

    private final ObjectMapper errorMapper = new ObjectMapper();

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        // Default exception message
        String errorMessage = response.getRawStatusCode() + " " + response.getStatusText();

        // Parse JSON error payload, if possible, and extract the error message therein
        Errors errors = null;
        final MediaType mimeType = response.getHeaders().getContentType();
        if (mimeType != null && mimeType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            try (final InputStream body = response.getBody()) {
                errors = this.errorMapper.readValue(body, Errors.class);
                if (errors.getErrors().length > 0) {
                    final Errors.Error firstError = errors.getErrors()[0];
                    if (firstError.getReason() != null) {
                        errorMessage = firstError.getReason();
                        if (firstError.getField() != null)
                            errorMessage = "field `" + firstError.getField() + "': " + errorMessage;
                    }
                }
            } catch (Exception e) {
                LoggerFactory.getLogger(this.getClass()).warn("failed to parse JSON error payload", e);
            }
        }

        // Build and throw exception
        final LinodeApiException e = new LinodeApiException(errorMessage);
        e.setErrors(errors);
        throw e;
    }
}
