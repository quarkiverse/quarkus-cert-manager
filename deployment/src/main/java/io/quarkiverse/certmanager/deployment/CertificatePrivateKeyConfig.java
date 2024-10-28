package io.quarkiverse.certmanager.deployment;

import io.dekorate.certmanager.annotation.PrivateKeyAlgorithm;
import io.dekorate.certmanager.annotation.PrivateKeyEncoding;
import io.dekorate.certmanager.annotation.RotationPolicy;
import io.smallrye.config.WithDefault;

public interface CertificatePrivateKeyConfig {
    /**
     * RotationPolicy controls how private keys should be regenerated when a re-issuance is being processed.
     */
    @WithDefault("Unset")
    RotationPolicy rotationPolicy();

    /**
     * The private key cryptography standards (PKCS) encoding for this certificate’s private key to be encoded in.
     */
    @WithDefault("Unset")
    PrivateKeyEncoding encoding();

    /**
     * The private key algorithm of the corresponding private key for this certificate.
     */
    @WithDefault("Unset")
    PrivateKeyAlgorithm algorithm();

    /**
     * The key bit size of the corresponding private key for this certificate.
     */
    @WithDefault("-1")
    int size();
}
