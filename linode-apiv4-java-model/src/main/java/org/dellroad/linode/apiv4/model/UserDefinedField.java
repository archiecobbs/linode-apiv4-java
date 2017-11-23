
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@link StackScript} user-defined field.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDefinedField {

    private String label;
    private String example;
    private String defaultx;
    private String oneOf;

    public String getLabel() {
        return this.label;
    }
    public void setLabel(final String label) {
        this.label = label;
    }

    public String getExample() {
        return this.example;
    }
    public void setExample(final String example) {
        this.example = example;
    }

    public String getDefault() {
        return this.defaultx;
    }
    public void setDefault(final String defaultx) {
        this.defaultx = defaultx;
    }

    public String getOneOf() {
        return this.oneOf;
    }
    public void setOneOf(final String oneOf) {
        this.oneOf = oneOf;
    }
}
