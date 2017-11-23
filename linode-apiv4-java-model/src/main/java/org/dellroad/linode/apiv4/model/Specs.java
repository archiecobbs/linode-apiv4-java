
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Linode instance specs.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode">Linodes</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Specs {

    private int disk;
    private int memory;
    private int vcpus;
    private int transfer;

    public int getDisk() {
        return this.disk;
    }
    public void setDisk(final int disk) {
        this.disk = disk;
    }

    public int getMemory() {
        return this.memory;
    }
    public void setMemory(final int memory) {
        this.memory = memory;
    }

    public int getVcpus() {
        return this.vcpus;
    }
    public void setVcpus(final int vcpus) {
        this.vcpus = vcpus;
    }

    public int getTransfer() {
        return this.transfer;
    }
    public void setTransfer(final int transfer) {
        this.transfer = transfer;
    }
}
