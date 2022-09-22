package io.quarkiverse.certmanager.deployment;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;

@ConfigGroup
public interface SelfSignedConfig {

    /**
     * If the self-signed issuer should be generated.
     */
    boolean enabled();

    /**
     * The CRL distribution points is an X.509 v3 certificate extension which identifies the location of the CRL from which the
     * revocation of this certificate can be checked.
     */
    Optional<List<String>> crlDistributionPoints();
}
