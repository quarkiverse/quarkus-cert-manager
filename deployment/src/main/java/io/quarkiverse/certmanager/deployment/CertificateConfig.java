package io.quarkiverse.certmanager.deployment;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.certificate")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface CertificateConfig {

    /**
     * If enable/disable the Cert-Manager extension.
     */
    @WithDefault("true")
    boolean enabled();

    /**
     * The name of the certificate resource to be generated.
     * If not provided, it will use the default name for the application resources.
     */
    Optional<String> name();

    /**
     * SecretName is the name of the secret resource that will be automatically created and managed by this Certificate
     * resource.
     * It will be populated with a private key and certificate, signed by the denoted issuer.
     */
    String secretName();

    /**
     * The reference to the issuer for this certificate.
     */
    Optional<IssuerRefConfig> issuerRef();

    /**
     * The CA issuer configuration.
     */
    Optional<CAConfig> ca();

    /**
     * The Vault issuer configuration.
     */
    Optional<VaultConfig> vault();

    /**
     * The self-signed issuer configuration.
     */
    Optional<SelfSignedConfig> selfSigned();

    /**
     * Full X509 name specification (https://golang.org/pkg/crypto/x509/pkix/#Name).
     */
    Optional<SubjectConfig> subject();

    /**
     * CommonName is a common name to be used on the Certificate. The CommonName should have a length of 64 characters
     * or fewer
     * to avoid generating invalid CSRs.
     */
    Optional<String> commonName();

    /**
     * The lifetime of the Certificate.
     */
    Optional<String> duration();

    /**
     * How long before the currently issued certificate’s expiry cert-manager should renew the certificate.
     * The default is 2⁄3 of the issued certificate’s duration.
     */
    Optional<String> renewBefore();

    /**
     * The list of <a href="https://en.wikipedia.org/wiki/Subject_Alternative_Name">Subject Alternative Names</a>.
     */
    Optional<List<String>> dnsNames();

    /**
     * The list of IP address subjectAltNames to be set on the Certificate.
     */
    Optional<List<String>> ipAddresses();

    /**
     * The list of URI subjectAltNames to be set on the Certificate.
     */
    Optional<List<String>> uris();

    /**
     * The list of email subjectAltNames to be set on the Certificate.
     */
    Optional<List<String>> emailAddresses();

    /**
     * The Keystores generation configuration.
     */
    Optional<CertificateKeystoresConfig> keystores();

    /**
     * If true, it will mark this Certificate as valid for certificate signing.
     */
    Optional<Boolean> isCA();

    /**
     * The set of x509 usages that are requested for the certificate.
     */
    Optional<List<String>> usages();

    /**
     * Options to control private keys used for the Certificate.
     */
    Optional<CertificatePrivateKeyConfig> privateKey();

    /**
     * Whether key usages should be present in the CertificateRequest
     */
    @WithDefault("false")
    boolean encodeUsagesInRequest();

    /**
     * The mount path where the generated certificate resources will be mounted.
     */
    @WithDefault("/etc/certs")
    String volumeMountPath();

    /**
     * Whether to populate the HTTP SSL configuration as described in
     * <a href="https://quarkus.io/guides/http-reference#providing-a-keystore">here</a>.
     */
    @WithDefault("AUTOMATIC")
    AutoConfigureMode autoconfigure();
}
