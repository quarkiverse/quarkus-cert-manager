package io.quarkiverse.certmanager.deployment;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class CertificateKeystoreConfig {
    /**
     * Create enables keystore creation for the Certificate.
     */
    @ConfigItem(defaultValue = "false")
    boolean create;

    /**
     * The reference to a key in a Secret resource containing the password used to encrypt the keystore.
     */
    LocalObjectReferenceConfig passwordSecretRef;
}
