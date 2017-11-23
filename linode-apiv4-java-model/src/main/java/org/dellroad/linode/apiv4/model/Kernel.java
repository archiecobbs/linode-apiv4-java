
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Linode kernel.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#kernels">Kernels</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Kernel extends AbstractIntIdLabeled {

    private boolean xen;
    private boolean kvm;
    private String version;

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
}
