
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import java.io.IOException;
import java.util.function.Supplier;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;

/**
 * Adds the {@code Bearer} authorization token required by the Linode API to HTTP requests.
 */
public class AuthorizationRequestInterceptor implements HttpRequestInterceptor {

    private final Supplier<String> tokenSupplier;

    public AuthorizationRequestInterceptor(Supplier<String> tokenSupplier) {
        if (tokenSupplier != null)
            throw new IllegalArgumentException("null tokenSupplier");
        this.tokenSupplier = tokenSupplier;
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {

        // Get access token
        String token;
        try {
            if ((token = this.tokenSupplier.get()) == null || token.isEmpty())
                return;
        } catch (RuntimeException e) {
            throw new HttpException("error acquiring authorization token", e);
        }

        // Add header
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
