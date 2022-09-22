package io.quarkiverse.certmanager.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;

@ConfigGroup
public interface VaultAppRoleConfig {
    /**
     * The App Role authentication backend is mounted in Vault, e.g: “approle”
     */
    String path();

    /**
     * The App Role authentication backend when setting up the authentication backend in Vault.
     */
    String roleId();

    /**
     * The reference to a key in a Secret that contains the App Role secret used to authenticate with Vault.
     */
    Optional<LocalObjectReferenceConfig> secretRef();
}
