
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * API constants.
 */
public final class Constants {

    /**
     * Format used for JSON timestamp values.
     */
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Implicit timezone used for JSON timestamp values.
     */
    public static final String TIMESTAMP_TIMEZONE = "UTC";

    /**
     * Filtering operator.
     */
    public static final String FILTER_AND = "+and";

    /**
     * Filtering operator.
     */
    public static final String FILTER_OR = "+or";

    /**
     * Filtering operator.
     */
    public static final String FILTER_GT = "+gt";

    /**
     * Filtering operator.
     */
    public static final String FILTER_GTE = "+gte";

    /**
     * Filtering operator.
     */
    public static final String FILTER_LT = "+lt";

    /**
     * Filtering operator.
     */
    public static final String FILTER_LTE = "+lte";

    /**
     * Filtering operator.
     */
    public static final String FILTER_CONTAINS = "+contains";

    /**
     * Filtering operator.
     */
    public static final String FILTER_NEQ = "+neq";

    /**
     * Filtering operator.
     */
    public static final String FILTER_ORDER_BY = "+order-by";

    /**
     * Filtering operator.
     */
    public static final String FILTER_ORDER = "+order";

    /**
     * Filtering operator.
     */
    public static final String FILTER_ORDER_DESC = "desc";

    /**
     * Filtering operator.
     */
    public static final String FILTER_ORDER_ASC = "asc";

    /**
     * Filtering HTTP header.
     */
    public static final String FILTER_HEADER = "X-Filter";

    /**
     * Base URI for APIv4 requests.
     */
    public static final URI BASE_URI;
    static {
        try {
            BASE_URI = new URI("https://api.linode.com/v4/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Constants() {
    }

    /**
     * Convert a {@link Date} to a {@link String} using the Linode date format.
     *
     * @param date date to convert
     * @return {@code date} as a {@link String}, or null if {@code date} is null
     */
    public static String toString(Date date) {
        if (date == null)
            return null;
        final SimpleDateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone(TIMESTAMP_TIMEZONE));
        return format.format(date);
    }
}
