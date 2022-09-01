package io.quarkiverse.certmanager.deployment;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class IssuerRefConfig {
    /**
     * The name of the resource being referred to.
     */
    @ConfigItem
    String name;

    /**
     * The kind of the resource being referred to.
     */
    @ConfigItem
    String kind;

    /**
     * The group of the resource being referred to.
     */
    @ConfigItem
    String group;
}
