package io.quarkiverse.certmanager.deployment;

import java.util.function.BooleanSupplier;

import jakarta.inject.Inject;

public class IsEnabled implements BooleanSupplier {

    @Inject
    CertificateConfig config;

    @Override
    public boolean getAsBoolean() {
        return config.enabled();
    }
}
