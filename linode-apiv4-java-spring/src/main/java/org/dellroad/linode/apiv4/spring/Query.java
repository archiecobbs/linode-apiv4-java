
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import java.util.Date;

import org.dellroad.linode.apiv4.Constants;
import org.springframework.util.LinkedMultiValueMap;

/**
 * Generic request with name/value pairs as parameters.
 */
@SuppressWarnings("serial")
class Query extends LinkedMultiValueMap<String, Object> {

    Query() {
    }

    Query(String name, Object... value) {
        for (int i = 0; i < value.length - 1; i++) {
            final String key = (String)value[i++];
            final Object val = value[i++];
            if (val instanceof String || val instanceof Number || val instanceof Boolean || val instanceof Enum)
                this.add(name, val.toString());
            else if (val instanceof Date)
                this.add(name, Constants.toString((Date)val));
            else {
                throw new IllegalArgumentException("illegal JSON request value of type "
                  + (val != null ? val.getClass().getName() : "null"));
            }
        }
    }
}
