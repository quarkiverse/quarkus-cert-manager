package io.quarkiverse.certmanager.deployment;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME, name = "cert-manager")
public class CertManagerConfig {
    /**
     * The name of the certificate resource to be generated.
     */
    @ConfigItem
    String name;

    /**
     * SecretName is the name of the secret resource that will be automatically created and managed by this Certificate resource.
     * It will be populated with a private key and certificate, signed by the denoted issuer.
     */
    @ConfigItem
    String secretName;

    /**
     * The reference to the issuer for this certificate.
     */
    @ConfigItem
    Optional<IssuerRefConfig> issuerRef;

    /**
     * The CA issuer configuration.
     */
    @ConfigItem
    Optional<CAConfig> ca;

    /**
     * The Vault issuer configuration.
     */
    /*@ConfigItem
    Optional<VaultConfig> vault;*/

    /**
     * The self-signed issuer configuration.
     */
    @ConfigItem
    Optional<SelfSignedConfig> selfSigned;

    /**
     * Full X509 name specification (https://golang.org/pkg/crypto/x509/pkix/#Name).
     */
    @ConfigItem
    SubjectConfig subject;

    /**
     * CommonName is a common name to be used on the Certificate. The CommonName should have a length of 64 characters or fewer
     * to avoid generating invalid CSRs.
     */
    @ConfigItem
    String commonName;

    /**
     * The lifetime of the Certificate.
     */
    @ConfigItem
    String duration;

    /**
     * How long before the currently issued certificate’s expiry cert-manager should renew the certificate.
     * The default is 2⁄3 of the issued certificate’s duration.
     */
    @ConfigItem
    String renewBefore;

    /**
     * @return the list of <a href="https://en.wikipedia.org/wiki/Subject_Alternative_Name">Subject Alternative Names</a>.
     */
    @ConfigItem
    List<String> dnsNames;

    /**
     * @return the list of IP address subjectAltNames to be set on the Certificate.
     */
    @ConfigItem
    List<String> ipAddresses;

    /**
     * @return the list of URI subjectAltNames to be set on the Certificate.
     */
    @ConfigItem
    List<String> uris;

    /**
     * @return the list of email subjectAltNames to be set on the Certificate.
     */
    @ConfigItem
    List<String> emailAddresses;

    /**
     * @return the Keystores generation configuration.
     */
    @ConfigItem
    CertificateKeystoresConfig keystores;

    /**
     * @return if true, it will mark this Certificate as valid for certificate signing.
     */
    @ConfigItem(defaultValue = "false")
    boolean isCA;

    /**
     * @return the set of x509 usages that are requested for the certificate.
     */
    @ConfigItem
    List<String> usages;

    /**
     * @return options to control private keys used for the Certificate.
     */
    @ConfigItem
    CertificatePrivateKeyConfig privateKey;

    /**
     * @return whether key usages should be present in the CertificateRequest
     */
    @ConfigItem(defaultValue = "false")
    boolean encodeUsagesInRequest;

    /**
     * @return the mount path where the generated certificate resources will be mounted.
     */
    @ConfigItem
    String volumeMountPath;
}
