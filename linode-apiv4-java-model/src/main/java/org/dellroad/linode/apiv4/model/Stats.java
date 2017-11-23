
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Linode statistics.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stats {

    private String title;
    private Data data;

    public String getTitle() {
        return this.title;
    }
    public void setTitle(final String title) {
        this.title = title;
    }

    public Data getData() {
        return this.data;
    }
    public void setData(final Data data) {
        this.data = data;
    }

// Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {

        private float[][] cpu;
        private Io io;
        private Net netv4;
        private Net netv6;

        public float[][] getCpu() {
            return this.cpu;
        }
        public void setCpu(final float[][] cpu) {
            this.cpu = cpu;
        }

        public Io getIo() {
            return this.io;
        }
        public void setIo(final Io io) {
            this.io = io;
        }

        public Net getNetv4() {
            return this.netv4;
        }
        public void setNetv4(final Net netv4) {
            this.netv4 = netv4;
        }

        public Net getNetv6() {
            return this.netv6;
        }
        public void setNetv6(final Net netv6) {
            this.netv6 = netv6;
        }

    // Io

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Io {

            private float[][] swap;
            private float[][] io;

            public float[][] getSwap() {
                return this.swap;
            }
            public void setSwap(final float[][] swap) {
                this.swap = swap;
            }

            public float[][] getIo() {
                return this.io;
            }
            public void setIo(final float[][] io) {
                this.io = io;
            }
        }

    // Net

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Net {

            private float[][] in;
            private float[][] out;
            private float[][] privateIn;
            private float[][] privateOut;

            public float[][] getIn() {
                return this.in;
            }
            public void setIn(final float[][] in) {
                this.in = in;
            }

            public float[][] getOut() {
                return this.out;
            }
            public void setOut(final float[][] out) {
                this.out = out;
            }

            @JsonProperty("private_in")
            public float[][] getPrivateIn() {
                return this.privateIn;
            }
            public void setPrivateIn(final float[][] privateIn) {
                this.privateIn = privateIn;
            }

            @JsonProperty("private_out")
            public float[][] getPrivateOut() {
                return this.privateOut;
            }
            public void setPrivateOut(final float[][] privateOut) {
                this.privateOut = privateOut;
            }
        }
    }
}
