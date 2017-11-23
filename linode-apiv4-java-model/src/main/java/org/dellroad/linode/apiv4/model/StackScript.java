
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import org.dellroad.linode.apiv4.Constants;

/**
 * Linode StackScript.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#stackscripts">StackScripts</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StackScript extends AbstractIntIdLabeled {

    private String username;
    private String userGravatarId;
    private String description;
    private String[] distributionIds;
    private int deploymentsTotal;
    private int deploymentsActive;
    private boolean publicx;
    private Date created;
    private Date updated;
    private String revNote;
    private String script;
    private UserDefinedField[] userDefinedFields;

    public String getUsername() {
        return this.username;
    }
    public void setUsername(final String username) {
        this.username = username;
    }

    @JsonProperty("user_gravatar_id")
    public String getUserGravatarId() {
        return this.userGravatarId;
    }
    public void setUserGravatarId(final String userGravatarId) {
        this.userGravatarId = username;
    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription(final String description) {
        this.description = description;
    }

    @JsonProperty("distributions")
    public String[] getDistributionIds() {
        return this.distributionIds;
    }
    public void setDistributionIds(final String[] distributionIds) {
        this.distributionIds = distributionIds;
    }

    @JsonProperty("deployments_total")
    public int getDeploymentsTotal() {
        return this.deploymentsTotal;
    }
    public void setDeploymentsTotal(final int deploymentsTotal) {
        this.deploymentsTotal = deploymentsTotal;
    }

    @JsonProperty("deployments_active")
    public int getDeploymentsActive() {
        return this.deploymentsActive;
    }
    public void setDeploymentsActive(final int deploymentsActive) {
        this.deploymentsActive = deploymentsActive;
    }

    @JsonProperty("is_public")
    public boolean isPublic() {
        return this.publicx;
    }
    public void setPublic(final boolean publicx) {
        this.publicx = publicx;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = "UTC")
    public Date getCreated() {
        return this.created;
    }
    public void setCreated(final Date created) {
        this.created = created;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_FORMAT, timezone = "UTC")
    public Date getUpdated() {
        return this.updated;
    }
    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    public String getRevNote() {
        return this.revNote;
    }
    public void setRevNote(final String revNote) {
        this.revNote = revNote;
    }

    public String getScript() {
        return this.script;
    }
    public void setScript(final String script) {
        this.script = script;
    }

    @JsonProperty("user_defined_fields")
    public UserDefinedField[] getUserDefinedFields() {
        return this.userDefinedFields;
    }
    public void setUserDefinedFields(final UserDefinedField[] userDefinedFields) {
        this.userDefinedFields = userDefinedFields;
    }
}
