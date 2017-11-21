
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model.request;

import org.dellroad.linode.apiv4.model.Devices;

/**
 * Used to boot Linode instances in rescue mode.
 *
 * @see org.dellroad.linode.apiv4.spring.LinodeApiRequestSender
 */
public class RescueLinodeRequest {

    private Devices devices;

    public Devices getDevices() {
        return this.devices;
    }
    public void setDevices(final Devices devices) {
        this.devices = devices;
    }
}
