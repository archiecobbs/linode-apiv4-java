
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

import org.dellroad.linode.apiv4.model.Error;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * Extension of Spring's {@link DefaultResponseErrorHandler} that decodes the JSON error payload
 * in HTTP 400 and 500 series errors so it can be included in the thrown exception.
 */
public class LinodeApiErrorHandler extends DefaultResponseErrorHandler {

    private final ObjectMapper errorMapper;

    public LinodeApiErrorHandler() {
        this.errorMapper = new ObjectMapper();
        this.errorMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        // Get error details by parsing JSON error response, if possible
        String errorMessage = response.getRawStatusCode() + " " + response.getStatusText();
        final MediaType mimeType = response.getHeaders().getContentType();
        if (mimeType != null && mimeType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            try (final InputStream body = response.getBody()) {
                final Error error = this.errorMapper.readValue(body, Error.class);
                if (error.getReason() != null) {
                    errorMessage = error.getReason();
                    if (error.getField() != null)
                        errorMessage = "[" + error.getField() + "] " + errorMessage;
                }
            } catch (IOException e) {
                // ignore
            }
        }

        // Throw exception
        throw new LinodeApiException(errorMessage);
    }
}
