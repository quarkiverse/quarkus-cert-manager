package io.quarkiverse.certmanager.deployment;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;

@ConfigGroup
public interface SubjectConfig {
    /**
     * The organizations to be used on the Certificate.
     */
    Optional<List<String>> organizations();

    /**
     * The countries to be used on the Certificate.
     */
    Optional<List<String>> countries();

    /**
     * The organizational Units to be used on the Certificate.
     */
    Optional<List<String>> organizationalUnits();

    /**
     * The cities to be used on the Certificate.
     */
    Optional<List<String>> localities();

    /**
     * The State/Provinces to be used on the Certificate.
     */
    Optional<List<String>> provinces();

    /**
     * The street addresses to be used on the Certificate.
     */
    Optional<List<String>> streetAddresses();

    /**
     * The postal codes to be used on the Certificate.
     */
    Optional<List<String>> postalCodes();

    /**
     * The serial number to be used on the Certificate.
     */
    Optional<String> serialNumber();
}
