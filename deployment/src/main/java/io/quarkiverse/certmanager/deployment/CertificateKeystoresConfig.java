package io.quarkiverse.certmanager.deployment;

import java.util.Optional;

public interface CertificateKeystoresConfig {
    /**
     * JKS configures options for storing a JKS keystore in the spec.secretName Secret resource.
     * If set, a file named keystore.jks will be created in the target Secret resource, encrypted using the password stored in
     * passwordSecretRef.
     */
    Optional<CertificateKeystoreConfig> jks();

    /**
     * PKCS12 configures options for storing a PKCS12 keystore in the spec.secretName Secret resource.
     * If set, a file named keystore.p12 will be created in the target Secret resource, encrypted using the password stored in
     * passwordSecretRef.
     */
    Optional<CertificateKeystoreConfig> pkcs12();
}
