package io.quarkiverse.certmanager.deployment;

import static io.dekorate.kubernetes.config.KubernetesConfigGenerator.KUBERNETES;
import static io.quarkiverse.certmanager.deployment.utils.KeystoreType.JKS;
import static io.quarkiverse.certmanager.deployment.utils.KeystoreType.PKCS12;
import static io.quarkus.deployment.Capability.OPENSHIFT;

import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import io.dekorate.certmanager.adapter.CertificateConfigAdapter;
import io.dekorate.config.PropertyConfiguration;
import io.dekorate.kubernetes.config.EnvBuilder;
import io.dekorate.kubernetes.config.IngressBuilder;
import io.dekorate.kubernetes.decorator.AddAnnotationDecorator;
import io.dekorate.kubernetes.decorator.AddEnvVarDecorator;
import io.dekorate.kubernetes.decorator.AddIngressTlsDecorator;
import io.quarkiverse.certmanager.deployment.utils.CertManagerAnnotations;
import io.quarkiverse.certmanager.deployment.utils.CertManagerConfigUtil;
import io.quarkiverse.certmanager.deployment.utils.KeystoreType;
import io.quarkus.deployment.Capabilities;
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
    private static final String QUARKUS_KUBERNETES_EXPOSE = "quarkus.kubernetes.expose";
    private static final String QUARKUS_KUBERNETES_NAME = "quarkus.kubernetes.name";
    private static final String QUARKUS_OPENSHIFT_ROUTE_EXPOSE = "quarkus.openshift.route.expose";
    private static final String QUARKUS_OPENSHIFT_EXPOSE = "quarkus.openshift.expose";
    private static final String QUARKUS_OPENSHIFT_NAME = "quarkus.openshift.name";
    private static final String QUARKUS_CONTAINER_IMAGE_NAME = "quarkus.container-image.name";
    private static final String OPENSHIFT_GROUP = "openshift";
    private static final String ROUTE = "Route";
    private static final String CLUSTER_ISSUER = "ClusterIssuer";
    private static final Logger LOGGER = Logger.getLogger(CertManagerProcessor.class);

    @BuildStep
    FeatureBuildItem feature(Capabilities capabilities, ApplicationInfoBuildItem applicationInfo, CertificateConfig config,
            BuildProducer<ConfigurationSupplierBuildItem> configurationSupplier,
            BuildProducer<DecoratorBuildItem> decorators) {
        validate(config);
        configureDekorateToGenerateCertManagerResources(config, configurationSupplier);
        configureSecuredEndpoints(capabilities, applicationInfo, config, decorators);
        return new FeatureBuildItem(FEATURE);
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

    private static void configureSecuredEndpoints(Capabilities capabilities, ApplicationInfoBuildItem applicationInfo,
            CertificateConfig config, BuildProducer<DecoratorBuildItem> decorators) {
        if (config.autoconfigure() == AutoConfigureMode.NONE) {
            return;
        }

        if (config.autoconfigure() == AutoConfigureMode.AUTOMATIC) {
            if (isOpenShift(capabilities) && isRouteExposed()) {
                configureRouteTsl(capabilities, applicationInfo, config, decorators);
            } else if (isIngressExposed()) {
                configureIngressTsl(capabilities, applicationInfo, config, decorators);
            } else {
                configureQuarkusHttpSsl(config, decorators);
            }
        } else if (config.autoconfigure() == AutoConfigureMode.ALL) {
            configureQuarkusHttpSsl(config, decorators);
            if (isOpenShift(capabilities) && isRouteExposed()) {
                configureRouteTsl(capabilities, applicationInfo, config, decorators);
            }

            if (isIngressExposed()) {
                configureIngressTsl(capabilities, applicationInfo, config, decorators);
            }
        } else if (config.autoconfigure() == AutoConfigureMode.CLUSTER_ONLY) {
            if (isOpenShift(capabilities) && isRouteExposed()) {
                configureRouteTsl(capabilities, applicationInfo, config, decorators);
            }

            if (isIngressExposed()) {
                configureIngressTsl(capabilities, applicationInfo, config, decorators);
            }
        } else if (config.autoconfigure() == AutoConfigureMode.HTTPS_ONLY) {
            configureQuarkusHttpSsl(config, decorators);
        }
    }

    private static boolean isOpenShift(Capabilities capabilities) {
        return capabilities.isPresent(OPENSHIFT);
    }

    private static boolean isRouteExposed() {
        Config config = ConfigProvider.getConfig();
        return config.getOptionalValue(QUARKUS_OPENSHIFT_ROUTE_EXPOSE, Boolean.class)
                .or(() -> config.getOptionalValue(QUARKUS_OPENSHIFT_EXPOSE, Boolean.class))
                .orElse(Boolean.FALSE);
    }

    private static boolean isIngressExposed() {
        Config config = ConfigProvider.getConfig();
        return config.getOptionalValue(QUARKUS_KUBERNETES_INGRESS_EXPOSE, Boolean.class)
                .or(() -> config.getOptionalValue(QUARKUS_KUBERNETES_EXPOSE, Boolean.class))
                .orElse(Boolean.FALSE);
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

    private static void configureRouteTsl(Capabilities capabilities, ApplicationInfoBuildItem applicationInfo,
            CertificateConfig certificateConfig, BuildProducer<DecoratorBuildItem> decorators) {
        if (certificateConfig.issuerRef().isPresent()) {
            String issuerName = certificateConfig.issuerRef().get().name();
            if (CLUSTER_ISSUER.equals(certificateConfig.issuerRef().get().kind())) {
                addAnnotationIntoRoute(CertManagerAnnotations.CLUSTER_ISSUER, issuerName, capabilities, applicationInfo,
                        decorators);
            } else {
                addAnnotationIntoRoute(CertManagerAnnotations.ISSUER, issuerName, capabilities, applicationInfo, decorators);
            }
        } else {
            addAnnotationIntoRoute(CertManagerAnnotations.ISSUER, getResourceName(capabilities, applicationInfo),
                    capabilities, applicationInfo, decorators);
        }
    }

    private static void configureIngressTsl(Capabilities capabilities, ApplicationInfoBuildItem applicationInfo,
            CertificateConfig certificateConfig,
            BuildProducer<DecoratorBuildItem> decorators) {
        String[] tlsHosts = certificateConfig.dnsNames().map(l -> l.toArray(new String[0])).orElse(new String[0]);
        decorators.produce(new DecoratorBuildItem(KUBERNETES,
                new AddIngressTlsDecorator(getResourceName(capabilities, applicationInfo),
                        new IngressBuilder()
                                .withTlsSecretName(certificateConfig.secretName())
                                .withTlsHosts(tlsHosts)
                                .build())));
    }

    private static void addAnnotationIntoRoute(String annotation, String value, Capabilities capabilities,
            ApplicationInfoBuildItem applicationInfo,
            BuildProducer<DecoratorBuildItem> decorators) {
        decorators.produce(new DecoratorBuildItem(OPENSHIFT_GROUP,
                new AddAnnotationDecorator(getResourceName(capabilities, applicationInfo), annotation, value, ROUTE)));
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

    public static String getResourceName(Capabilities capabilities, ApplicationInfoBuildItem info) {
        Config config = ConfigProvider.getConfig();
        Optional<String> resourceName;
        if (isOpenShift(capabilities)) {
            resourceName = config.getOptionalValue(QUARKUS_OPENSHIFT_NAME, String.class);
        } else {
            resourceName = config.getOptionalValue(QUARKUS_KUBERNETES_NAME, String.class);
        }

        return resourceName
                .or(() -> config.getOptionalValue(QUARKUS_CONTAINER_IMAGE_NAME, String.class))
                .orElse(info.getName());
    }
}
