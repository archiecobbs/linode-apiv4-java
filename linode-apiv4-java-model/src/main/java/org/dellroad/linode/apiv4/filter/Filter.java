
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.filter;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.Map;

/**
 * A filter for Linode queries.
 *
 * <p>
 * Instances are created via a {@link FilterBuilder}.
 *
 * <p>
 * Instances are immutable.
 */
public class Filter {

    private final Map<String, Object> map;

    Filter(Map<String, Object> map) {
        this.map = map;
    }

    @JsonAnyGetter
    Map<String, Object> getProperties() {
        return this.map;
    }
}
