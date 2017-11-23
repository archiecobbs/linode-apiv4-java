
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4;

import java.net.URI;
import java.net.URISyntaxException;

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
}
