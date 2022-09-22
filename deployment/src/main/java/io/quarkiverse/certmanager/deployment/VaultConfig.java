package io.quarkiverse.certmanager.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;

@ConfigGroup
public interface VaultConfig {
    /**
     * The connection address for the Vault server, e.g: “https://vault.example.com:8200”.
     */
    String server();

    /**
     * The mount path of the Vault PKI backend’s sign endpoint, e.g: “my_pki_mount/sign/my-role-name”.
     */
    String path();

    /**
     * The reference where to retrieve the Vault token.
     */
    Optional<LocalObjectReferenceConfig> authTokenSecretRef();

    /**
     * The Vault authentication using App Role auth mechanism.
     *
     */
    Optional<VaultAppRoleConfig> authAppRole();

    /**
     * The Vault authentication using Kubernetes service account.
     */
    Optional<VaultKubernetesAuthConfig> authKubernetes();

    /**
     * @return the vault namespace.
     */
    Optional<String> namespace();

    /**
     * The PEM-encoded CA bundle (base64-encoded) used to validate Vault server certificate.
     */
    String caBundle();
}
