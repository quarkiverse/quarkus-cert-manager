package io.quarkiverse.certmanager.deployment;

import java.util.Optional;

import io.dekorate.certmanager.annotation.PrivateKeyAlgorithm;
import io.dekorate.certmanager.annotation.PrivateKeyEncoding;
import io.dekorate.certmanager.annotation.RotationPolicy;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class CertificatePrivateKeyConfig {
    /**
     * RotationPolicy controls how private keys should be regenerated when a re-issuance is being processed.
     */
    @ConfigItem
    Optional<RotationPolicy> rotationPolicy;

    /**
     * The private key cryptography standards (PKCS) encoding for this certificate’s private key to be encoded in.
     */
    @ConfigItem
    Optional<PrivateKeyEncoding> encoding;

    /**
     * The private key algorithm of the corresponding private key for this certificate.
     */
    @ConfigItem
    Optional<PrivateKeyAlgorithm> algorithm;

    /**
     * The key bit size of the corresponding private key for this certificate.
     */
    @ConfigItem
    Optional<Integer> size;
}
