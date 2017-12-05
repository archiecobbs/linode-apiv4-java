
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

class AbstractDiskRequest {

    private String distributionId;
    private String rootPassword;
    private String[] authorizedKeys;
    private String label;
    private Integer stackScriptId;
    private Map<String, String> stackScriptData;

    @JsonProperty("distribution")
    public String getDistributionId() {
        return this.distributionId;
    }
    public void setDistributionId(final String distributionId) {
        this.distributionId = distributionId;
    }

    @JsonProperty("root_pass")
    public String getRootPassword() {
        return this.rootPassword;
    }
    public void setRootPassword(final String rootPassword) {
        this.rootPassword = rootPassword;
    }

    @JsonProperty("authorized_keys")
    public String[] getAuthorizedKeys() {
        return this.authorizedKeys;
    }
    public void setAuthorizedKeys(final String[] authorizedKeys) {
        this.authorizedKeys = authorizedKeys;
    }

    public String getLabel() {
        return this.label;
    }
    public void setLabel(final String label) {
        this.label = label;
    }

    @JsonProperty("stackscript_id")
    public Integer getStackScriptId() {
        return this.stackScriptId;
    }
    public void setStackScriptId(final Integer stackScriptId) {
        this.stackScriptId = stackScriptId;
    }

    @JsonProperty("stackscript_data")
    public Map<String, String> getStackScriptData() {
        return this.stackScriptData;
    }
    public void setStackScriptData(final Map<String, String> stackScriptData) {
        this.stackScriptData = stackScriptData;
    }
}
