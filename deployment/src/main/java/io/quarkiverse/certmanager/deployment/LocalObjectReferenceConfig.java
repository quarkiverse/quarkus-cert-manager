package io.quarkiverse.certmanager.deployment;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class LocalObjectReferenceConfig {
    /**
     * The name of the resource being referred to.
     */
    @ConfigItem
    String name;

    /**
     * The key of the entry in the Secret resource’s data field to be used.
     */
    @ConfigItem
    String key;
}
