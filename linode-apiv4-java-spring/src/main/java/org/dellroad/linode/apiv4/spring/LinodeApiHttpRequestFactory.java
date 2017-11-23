
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import java.net.URI;
import java.util.function.Supplier;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * Extends Spring's {@link HttpComponentsClientHttpRequestFactory} to set connection and read timeouts,
 * setup authorization, etc.
 */
public class LinodeApiHttpRequestFactory extends HttpComponentsClientHttpRequestFactory
  implements InitializingBean, DisposableBean {

    /**
     * Default request timeout in milliseconds ({@value #DEFAULT_TIMEOUT}).
     */
    public static final int DEFAULT_TIMEOUT = 30_000;

    /**
     * Default maximum number of simultaneous connections ({@value #DEFAULT_MAX_SIMULTANEOUS_REQUESTS}).
     */
    public static final int DEFAULT_MAX_SIMULTANEOUS_REQUESTS = 16;

    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    private int timeout = DEFAULT_TIMEOUT;
    private Supplier<String> tokenSupplier;
    private int maxSimultaneousRequests = DEFAULT_MAX_SIMULTANEOUS_REQUESTS;

// Properties

    /**
     * Set timeout. Default is {@value #DEFAULT_TIMEOUT}ms.
     *
     * @param timeout timeout in milliseconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Configure the maximum number of simultaneous requests.
     *
     * @param maxSimultaneousRequests max simultaneous requests
     * @throws IllegalArgumentException if {@code maxSimultaneousRequests} is zero or negative
     */
    public void setMaxSimultaneousRequests(final int maxSimultaneousRequests) {
        if (maxSimultaneousRequests < 1)
            throw new IllegalArgumentException("maxSimultaneousRequests < 1");
        this.maxSimultaneousRequests = maxSimultaneousRequests;
    }

    /**
     * Configure where authorization tokens come from.
     *
     * @param tokenSupplier supplier of authorization tokens, or null for no authorization
     */
    public void setAuthorizationTokenSupplier(final Supplier<String> tokenSupplier) {
        this.tokenSupplier = tokenSupplier;
    }

    /**
     * Configure a fixed authorization token.
     *
     * @param token authorization token
     * @throws IllegalArgumentException if {@code token} is null or empty
     */
    public void setAuthorizationToken(final String token) {
        if (token == null || token.isEmpty())
            throw new IllegalArgumentException("null/empty token");
        this.tokenSupplier = () -> token;
    }

// Lifecycle

    @Override
    public void afterPropertiesSet() throws Exception {
        this.configureConnectionManager(this.connectionManager);
        final HttpClientBuilder builder = HttpClients.custom()
          .setConnectionManager(this.connectionManager)
          .addInterceptorLast((HttpRequestInterceptor)(request, context) -> {
            final String token = this.tokenSupplier != null ? this.tokenSupplier.get() : null;
            if (token != null)
                request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
          })
          .setDefaultSocketConfig(SocketConfig.custom().setSoKeepAlive(true).build());
        this.setHttpClient(this.configureHttpClient(builder).build());
    }

    @Override
    public void destroy() {
        this.connectionManager.shutdown();
    }

    protected void configureConnectionManager(PoolingHttpClientConnectionManager manager) {
        manager.setMaxTotal(this.maxSimultaneousRequests);
        manager.setDefaultMaxPerRoute(this.maxSimultaneousRequests);
    }

    protected HttpClientBuilder configureHttpClient(HttpClientBuilder builder) {
        return builder;
    }

// HttpComponentsClientHttpRequestFactory

    @Override
    protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
        final HttpUriRequest uriRequest = super.createHttpUriRequest(httpMethod, uri);
        if (!(uriRequest instanceof HttpRequestBase))
            return uriRequest;
        final HttpRequestBase request = (HttpRequestBase)uriRequest;
        final RequestConfig.Builder builder = request.getConfig() != null ?
          RequestConfig.copy(request.getConfig()) : RequestConfig.custom();
        request.setConfig(builder.setConnectTimeout(this.timeout)
          .setConnectionRequestTimeout(this.timeout)
          .setSocketTimeout(this.timeout).build());
        return request;
    }
}

