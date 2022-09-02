package io.quarkiverse.certmanager.deployment;

import static io.dekorate.kubernetes.config.KubernetesConfigGenerator.KUBERNETES;
import static io.quarkiverse.certmanager.deployment.utils.KeystoreType.JKS;
import static io.quarkiverse.certmanager.deployment.utils.KeystoreType.PKCS12;

import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import io.dekorate.certmanager.adapter.CertificateConfigAdapter;
import io.dekorate.config.PropertyConfiguration;
import io.dekorate.kubernetes.config.EnvBuilder;
import io.dekorate.kubernetes.config.IngressBuilder;
import io.dekorate.kubernetes.decorator.AddEnvVarDecorator;
import io.dekorate.kubernetes.decorator.AddIngressTlsDecorator;
import io.quarkiverse.certmanager.deployment.utils.CertManagerConfigUtil;
import io.quarkiverse.certmanager.deployment.utils.KeystoreType;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ApplicationInfoBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.kubernetes.spi.ConfigurationSupplierBuildItem;
import io.quarkus.kubernetes.spi.DecoratorBuildItem;

public class CertManagerProcessor {
    private static final String FEATURE = "cert-manager";
    private static final String ISSUERS_REQUIREMENT_MESSAGE = "You need to set only one of the following issuers: `issuerRef`, "
            + "`selfSigned`, "
            + "`ca`, "
            + "`vault`.";
    private static final String NO_KEYSTORES_SET_FOR_AUTO_CONFIGURATION_MESSAGE = "No certificate keystores has been set. "
            + "Skipping auto configuration of HTTP SSL.";
    private static final String QUARKUS_HTTP_SSL_KEYSTORE_FILE = "QUARKUS_HTTP_SSL_CERTIFICATE_KEY_STORE_FILE";
    private static final String QUARKUS_HTTP_SSL_KEYSTORE_TYPE = "QUARKUS_HTTP_SSL_CERTIFICATE_KEY_STORE_FILE_TYPE";
    private static final String QUARKUS_HTTP_SSL_KEYSTORE_PASSWORD = "QUARKUS_HTTP_SSL_CERTIFICATE_KEY_STORE_PASSWORD";
    private static final String QUARKUS_KUBERNETES_INGRESS_EXPOSE = "quarkus.kubernetes.ingress.expose";
    private static final Logger LOGGER = Logger.getLogger(CertManagerProcessor.class);

    @BuildStep
    FeatureBuildItem feature(ApplicationInfoBuildItem applicationInfo, CertificateConfig config,
            BuildProducer<ConfigurationSupplierBuildItem> configurationSupplier,
            BuildProducer<DecoratorBuildItem> decorators) {
        validate(config);
        configureDekorateToGenerateCertManagerResources(config, configurationSupplier);
        if (config.httpSslAutoConfiguration()) {
            configureQuarkusHttpSsl(config, decorators);
            configureIngressTslIfEnabled(applicationInfo, config, decorators);
        }
        return new FeatureBuildItem(FEATURE);
    }

    private static void configureQuarkusHttpSsl(CertificateConfig config, BuildProducer<DecoratorBuildItem> decorators) {
        if (config.keystores().isEmpty()) {
            LOGGER.warn(NO_KEYSTORES_SET_FOR_AUTO_CONFIGURATION_MESSAGE);
        } else {
            CertificateKeystoresConfig keystores = config.keystores().get();
            if (keystores.jks().isPresent() && keystores.jks().get().create()) {
                configureQuarkusHttpSslWithKeystore(config, JKS, keystores.jks().get().passwordSecretRef(), decorators);
            } else if (keystores.pkcs12().isPresent() && keystores.pkcs12().get().create()) {
                configureQuarkusHttpSslWithKeystore(config, PKCS12, keystores.pkcs12().get().passwordSecretRef(), decorators);
            } else {
                LOGGER.warn(NO_KEYSTORES_SET_FOR_AUTO_CONFIGURATION_MESSAGE);
            }
        }
    }

    private static void configureIngressTslIfEnabled(ApplicationInfoBuildItem applicationInfo,
            CertificateConfig certificateConfig,
            BuildProducer<DecoratorBuildItem> decorators) {
        Config config = ConfigProvider.getConfig();
        Optional<Boolean> isIngressExposed = config.getOptionalValue(QUARKUS_KUBERNETES_INGRESS_EXPOSE, Boolean.class);
        if (isIngressExposed.isPresent() && isIngressExposed.get()) {
            String[] tlsHosts = certificateConfig.dnsNames().map(l -> l.toArray(new String[0])).orElse(new String[0]);
            decorators.produce(new DecoratorBuildItem(KUBERNETES,
                    new AddIngressTlsDecorator(applicationInfo.getName(),
                            new IngressBuilder()
                                    .withTlsSecretName(certificateConfig.secretName())
                                    .withTlsHosts(tlsHosts)
                                    .build())));
        }
    }

    private static void configureQuarkusHttpSslWithKeystore(CertificateConfig config, KeystoreType type,
            LocalObjectReferenceConfig keystore, BuildProducer<DecoratorBuildItem> decorators) {
        decorators.produce(new DecoratorBuildItem(new AddEnvVarDecorator(new EnvBuilder()
                .withName(QUARKUS_HTTP_SSL_KEYSTORE_FILE)
                .withValue(config.volumeMountPath() + "/" + type.getGeneratedFile())
                .build())));
        decorators.produce(new DecoratorBuildItem(new AddEnvVarDecorator(new EnvBuilder()
                .withName(QUARKUS_HTTP_SSL_KEYSTORE_TYPE)
                .withValue(type.name())
                .build())));
        decorators.produce(new DecoratorBuildItem(new AddEnvVarDecorator(new EnvBuilder()
                .withName(QUARKUS_HTTP_SSL_KEYSTORE_PASSWORD)
                .withSecret(keystore.name())
                .withValue(keystore.key())
                .build())));

    }

    private static void configureDekorateToGenerateCertManagerResources(CertificateConfig config,
            BuildProducer<ConfigurationSupplierBuildItem> configurationSupplier) {
        configurationSupplier.produce(
                new ConfigurationSupplierBuildItem(
                        new PropertyConfiguration(
                                CertificateConfigAdapter.newBuilder(
                                        CertManagerConfigUtil.transformToDekorateProperties(config)))));
    }

    private void validate(CertificateConfig config) {
        long issuersConfigured = Stream.of(config.issuerRef(), config.ca(), config.vault(), config.selfSigned())
                .filter(Optional::isPresent)
                .count();
        if (issuersConfigured == 0) {
            throw new IllegalStateException("No issuer has been set in the certificate. " + ISSUERS_REQUIREMENT_MESSAGE);
        } else if (issuersConfigured > 1) {
            throw new IllegalStateException("More than one issuer has been set. " + ISSUERS_REQUIREMENT_MESSAGE);
        }
    }
}
