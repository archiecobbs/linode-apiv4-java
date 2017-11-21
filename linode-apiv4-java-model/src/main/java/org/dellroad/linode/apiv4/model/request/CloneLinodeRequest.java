
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to clone Linode instances.
 *
 * @see org.dellroad.linode.apiv4.spring.LinodeApiRequestSender
 */
public class CloneLinodeRequest {

    private String regionId;
    private String typeId;
    private Integer linodeId;
    private String label;
    private String group;
    private boolean backupsEnabled;
    private String[] diskIds;
    private String[] configIds;

    @JsonProperty("region")
    public String getRegionId() {
        return this.regionId;
    }
    public void setRegionId(final String regionId) {
        this.regionId = regionId;
    }

    @JsonProperty("type")
    public String getTypeId() {
        return this.typeId;
    }
    public void setTypeId(final String typeId) {
        this.typeId = typeId;
    }

    @JsonProperty("linode_id")
    public Integer getLinodeId() {
        return this.linodeId;
    }
    public void setLinodeId(final Integer linodeId) {
        this.linodeId = linodeId;
    }

    public String getLabel() {
        return this.label;
    }
    public void setLabel(final String label) {
        this.label = label;
    }

    public String getGroup() {
        return this.group;
    }
    public void setGroup(final String group) {
        this.group = group;
    }

    @JsonProperty("backups_enabled")
    public boolean isBackupsEnabled() {
        return this.backupsEnabled;
    }
    public void setBackupsEnabled(final boolean backupsEnabled) {
        this.backupsEnabled = backupsEnabled;
    }

    @JsonProperty("disks")
    public String[] getDiskIds() {
        return this.diskIds;
    }
    public void setDiskIds(final String[] diskIds) {
        this.diskIds = diskIds;
    }

    @JsonProperty("configs")
    public String[] getConfigIds() {
        return this.configIds;
    }
    public void setConfigIds(final String[] configIds) {
        this.configIds = configIds;
    }
}
