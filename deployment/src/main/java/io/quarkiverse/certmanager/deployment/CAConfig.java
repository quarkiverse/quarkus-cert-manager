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

    /**
     * If true, the application will be configured to trust the CA certificate (`ca.crt`) that Cert-Manager populates in the
     * generated certificate secret. This is done by setting the default TLS trust store of the application
     * (`quarkus.tls.trust-store.pem.certs`) to the mounted `ca.crt` file, so clients using the default TLS configuration
     * (for example, the REST client) can call services whose certificates were signed by this CA issuer.
     * <p>
     * Note that when enabled, the default TLS trust store will only trust the CA issuer certificate.
     * By default, it is false.
     */
    Optional<Boolean> trust();
}
