package io.quarkiverse.certmanager.deployment;

import java.util.Optional;

public interface IssuerRefConfig {
    /**
     * The name of the resource being referred to.
     */
    String name();

    /**
     * The kind of the resource being referred to.
     */
    Optional<String> kind();

    /**
     * The group of the resource being referred to.
     */
    Optional<String> group();
}
