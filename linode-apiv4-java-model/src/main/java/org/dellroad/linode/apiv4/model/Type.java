
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Linode instance type.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#types">Types</a>
 */
public class Type extends Specs {

    private String id;
    private Class linodeClass;
    private Price price;
    private String label;
    private AddOns addOns;
    private int networkOut;

    public String getId() {
        return this.id;
    }
    public void setId(final String id) {
        this.id = id;
    }

    @JsonProperty("class")
    public Class getLinodeClass() {
        return this.linodeClass;
    }
    public void setLinodeClass(final Class linodeClass) {
        this.linodeClass = linodeClass;
    }

    public Price getPrice() {
        return this.price;
    }
    public void setPrice(final Price price) {
        this.price = price;
    }

    public String getLabel() {
        return this.label;
    }
    public void setLabel(final String label) {
        this.label = label;
    }

    public AddOns getAddOns() {
        return this.addOns;
    }
    public void setAddOns(final AddOns addOns) {
        this.addOns = addOns;
    }

    @JsonProperty("network_out")
    public int getNetworkOut() {
        return this.networkOut;
    }
    public void setNetworkOut(final int networkOut) {
        this.networkOut = networkOut;
    }

// AddOns

    public static class AddOns {

        private Backups backups;

        public Backups getBackups() {
            return this.backups;
        }
        public void setBackups(final Backups backups) {
            this.backups = backups;
        }

    // Backups

        public static class Backups {

            private Price price;

            public Price getPrice() {
                return this.price;
            }
            public void setPrice(final Price price) {
                this.price = price;
            }
        }
    }

// Class

    /**
     * {@link Type} class.
     */
    public enum Class {
        NANODE,
        STANDARD,
        HIGHMEM;

        @JsonCreator
        public static Class parse(String value) {
            return Class.valueOf(value.toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

// Price

    /**
     * {@link Type} price.
     */
    public static class Price {

        private float hourly;
        private int monthly;

        public float getHourly() {
            return this.hourly;
        }
        public void setHourly(final float hourly) {
            this.hourly = hourly;
        }

        public int getMonthly() {
            return this.monthly;
        }
        public void setMonthly(final int monthly) {
            this.monthly = monthly;
        }
    }
}
