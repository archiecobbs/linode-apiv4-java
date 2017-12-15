
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Linode kernel.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#kernels">Kernels</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Kernel extends AbstractStringIdLabeled {

    private boolean xen;
    private boolean kvm;
    private String version;
    private Architecture architecture;
    private boolean pvops;

    public boolean isXen() {
        return this.xen;
    }
    public void setXen(final boolean xen) {
        this.xen = xen;
    }

    public boolean isKvm() {
        return this.kvm;
    }
    public void setKvm(final boolean kvm) {
        this.kvm = kvm;
    }

    public String getVersion() {
        return this.version;
    }
    public void setVersion(final String version) {
        this.version = version;
    }

    public Architecture getArchitecture() {
        return this.architecture;
    }
    public void setArchitecture(final Architecture architecture) {
        this.architecture = architecture;
    }

    public boolean isPvops() {
        return this.pvops;
    }
    public void setPvops(final boolean pvops) {
        this.pvops = pvops;
    }

// Architecture

    /**
     * Linode {@link Kernel} architectures.
     *
     * @see <a href="https://developers.linode.com/v4/reference/linode#kernels">Kernels</a>
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
}
