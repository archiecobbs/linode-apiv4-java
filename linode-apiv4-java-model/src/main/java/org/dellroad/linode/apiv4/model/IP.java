
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Superclass of {@link IPv4} and {@link IPv6} containing common properties.
 */
public abstract class IP {

    private int linodeId;
    private String regionId;
    private String address;
    private String gateway;
    private String subnetMask;
    private Integer prefix;
    private Type type;
    private String rdns;

    @JsonProperty("linode_id")
    public int getLinodeId() {
        return this.linodeId;
    }
    public void setLinodeId(final int linodeId) {
        this.linodeId = linodeId;
    }

    @JsonProperty("region")
    public String getRegionId() {
        return this.regionId;
    }
    public void setRegionId(final String regionId) {
        this.regionId = regionId;
    }

    public String getAddress() {
        return this.address;
    }
    public void setAddress(final String address) {
        this.address = address;
    }

    public String getGateway() {
        return this.gateway;
    }
    public void setGateway(final String gateway) {
        this.gateway = gateway;
    }

    @JsonProperty("subnet_mask")
    public String getSubnetMask() {
        return this.subnetMask;
    }
    public void setSubnetMask(final String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public Integer getPrefix() {
        return this.prefix;
    }
    public void setPrefix(final Integer prefix) {
        this.prefix = prefix;
    }

    public String getRdns() {
        return this.rdns;
    }
    public void setRdns(final String rdns) {
        this.rdns = rdns;
    }

    public Type getType() {
        return this.type;
    }
    public void setType(final Type type) {
        this.type = type;
    }

// Type

    /**
     * IP address type.
     */
    public enum Type {
        IPV4,
        IPV6;

        @JsonCreator
        public static Type parse(String value) {
            return Type.valueOf(value.toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
}
