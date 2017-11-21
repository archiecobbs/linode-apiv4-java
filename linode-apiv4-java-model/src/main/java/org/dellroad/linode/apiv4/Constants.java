
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

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String TIMESTAMP_TIMEZONE = "UTC";

    /**
     * The first page number ({@value #FIRST_PAGE}) in paginated responses.
     *
     * @see org.dellroad.linode.apiv4.model.Paginated
     */
    public static final int FIRST_PAGE = 1;

    /**
     * The maximimum number of results per page ({@value #PAGE_SIZE}) in paginated responses.
     *
     * @see org.dellroad.linode.apiv4.model.Paginated
     */
    public static final int PAGE_SIZE = 25;

    /**
     * Base URI for API requests.
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
