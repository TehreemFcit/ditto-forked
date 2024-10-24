/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.ditto.gateway.service.endpoints.routes.checkpermissions;

import org.eclipse.ditto.policies.model.PolicyId;

/**
 * Wrapper class for handling permission checks along with the associated {@link PolicyId}.
 * <p>
 * This class wraps an {@link ImmutablePermissionCheck} and provides an additional field to hold
 * the {@link PolicyId} that is associated with the permission check. The {@link PermissionCheckWrapper}
 * allows for flexible management of both the permission check and its related policy.
 *
 * @since 3.7.0
 */
public class PermissionCheckWrapper {
    private final ImmutablePermissionCheck permissionCheck;
    private PolicyId policyId;

    /**
     * Constructor to initialize the wrapper with a given {@link ImmutablePermissionCheck}.
     *
     * @param permissionCheck the permission check to wrap.
     */
    public PermissionCheckWrapper(ImmutablePermissionCheck permissionCheck) {
        this.permissionCheck = permissionCheck;
    }

    /**
     * Retrieves the {@link ImmutablePermissionCheck} contained within this wrapper.
     *
     * @return the wrapped permission check.
     */
    public ImmutablePermissionCheck getPermissionCheck() {
        return permissionCheck;
    }

    /**
     * Sets the {@link PolicyId} associated with this permission check.
     *
     * @param policyId the policy ID to set.
     */
    public void setPolicyId(PolicyId policyId) {
        this.policyId = policyId;
    }

    /**
     * Retrieves the {@link PolicyId} associated with this permission check.
     *
     * @return the associated policy ID, or {@code null} if not set.
     */
    public PolicyId getPolicyId() {
        return policyId;
    }
}
