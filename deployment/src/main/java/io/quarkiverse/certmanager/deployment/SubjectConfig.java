package io.quarkiverse.certmanager.deployment;

import java.util.List;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class SubjectConfig {
    /**
     * The organizations to be used on the Certificate.
     */
    @ConfigItem
    List<String> organizations;

    /**
     * The countries to be used on the Certificate.
     */
    @ConfigItem
    List<String> countries;

    /**
     * The organizational Units to be used on the Certificate.
     */
    @ConfigItem
    List<String> organizationalUnits;

    /**
     * The cities to be used on the Certificate.
     */
    @ConfigItem
    List<String> localities;

    /**
     * The State/Provinces to be used on the Certificate.
     */
    @ConfigItem
    List<String> provinces;

    /**
     * The street addresses to be used on the Certificate.
     */
    @ConfigItem
    List<String> streetAddresses;

    /**
     * The postal codes to be used on the Certificate.
     */
    @ConfigItem
    String[] postalCodes;

    /**
     * The serial number to be used on the Certificate.
     */
    @ConfigItem
    String serialNumber;
}
