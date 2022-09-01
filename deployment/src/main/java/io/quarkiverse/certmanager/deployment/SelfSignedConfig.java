package io.quarkiverse.certmanager.deployment;

import java.util.List;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class SelfSignedConfig {
    /**
     * The CRL distribution points is an X.509 v3 certificate extension which identifies the location of the CRL from which the
     * revocation of this certificate can be checked.
     */
    @ConfigItem
    List<String> crlDistributionPoints;
}
