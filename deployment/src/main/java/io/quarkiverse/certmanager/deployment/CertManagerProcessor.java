package io.quarkiverse.certmanager.deployment;

import java.util.Optional;
import java.util.stream.Stream;

import io.dekorate.certmanager.adapter.CertificateConfigAdapter;
import io.dekorate.config.PropertyConfiguration;
import io.quarkiverse.certmanager.deployment.utils.CertManagerConfigUtil;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.kubernetes.spi.ConfigurationSupplierBuildItem;

public class CertManagerProcessor {
    private static final String FEATURE = "cert-manager";
    private static final String ISSUERS_REQUIREMENT_MESSAGE = "You need to set only one of the following issuers: `issuerRef`, "
            + "`selfSigned`, "
            + "`ca`, "
            + "`vault`.";

    @BuildStep
    FeatureBuildItem feature(CertificateConfig config,
            BuildProducer<ConfigurationSupplierBuildItem> configurationSupplier) {
        validate(config);
        configurationSupplier.produce(
                new ConfigurationSupplierBuildItem(
                        new PropertyConfiguration(
                                CertificateConfigAdapter.newBuilder(
                                        CertManagerConfigUtil.transformToDekorateProperties(config)))));
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
}
