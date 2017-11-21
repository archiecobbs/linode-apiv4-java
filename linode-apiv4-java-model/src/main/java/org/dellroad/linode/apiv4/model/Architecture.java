
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Linode Linux distribution architectures.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#distributions">Distributions</a>
 */
public enum Architecture {
    I386(32),
    X86_64(64);

    private final int wordSize;

    Architecture(int wordSize) {
        this.wordSize = wordSize;
    }

    public int getWordSize() {
        return this.wordSize;
    }

    @JsonCreator
    public static Architecture parse(String value) {
        return Architecture.valueOf(value.toUpperCase());
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
