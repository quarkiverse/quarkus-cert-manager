package io.quarkiverse.certmanager.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;

@ConfigGroup
public interface VaultKubernetesAuthConfig {
    /**
     * The mount path to use when authenticating with Vault.
     */
    String mountPath();

    /**
     * The required Secret field containing a Kubernetes ServiceAccount JWT used for authenticating with Vault.
     */
    String role();

    /**
     * The reference to a key in a Secret that contains the App Role secret used to authenticate with Vault.
     */
    Optional<LocalObjectReferenceConfig> secretRef();
}
