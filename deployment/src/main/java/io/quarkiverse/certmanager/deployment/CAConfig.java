package io.quarkiverse.certmanager.deployment;

import java.util.List;
import java.util.Optional;

public interface CAConfig {
    /**
     * The name of the secret used to sign Certificates issued by this Issuer.
     */
    String secretName();

    /**
     * The CRL distribution points is an X.509 v3 certificate extension which identifies the location of the CRL from which the
     * revocation of this certificate can be checked.
     */
    Optional<List<String>> crlDistributionPoints();
}
