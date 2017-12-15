
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to create Linode instances.
 *
 * @see org.dellroad.linode.apiv4.spring.LinodeApiRequestSender
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateLinodeRequest extends AbstractDiskRequest {

    private String regionId;
    private String typeId;
    private String group;
    private Integer backupId;
    private boolean backupsEnabled;
    private boolean booted;

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

    public String getGroup() {
        return this.group;
    }
    public void setGroup(final String group) {
        this.group = group;
    }

    @JsonProperty("backup_id")
    public Integer getBackupId() {
        return this.backupId;
    }
    public void setBackupId(final Integer backupId) {
        this.backupId = backupId;
    }

    @JsonProperty("backups_enabled")
    public boolean isBackupsEnabled() {
        return this.backupsEnabled;
    }
    public void setBackupsEnabled(final boolean backupsEnabled) {
        this.backupsEnabled = backupsEnabled;
    }

    public boolean isBooted() {
        return this.booted;
    }
    public void setBooted(final boolean booted) {
        this.booted = booted;
    }
}
