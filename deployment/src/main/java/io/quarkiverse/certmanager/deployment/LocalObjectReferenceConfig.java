package io.quarkiverse.certmanager.deployment;

public interface LocalObjectReferenceConfig {
    /**
     * The name of the resource being referred to.
     */
    String name();

    /**
     * The key of the entry in the Secret resource’s data field to be used.
     */
    String key();
}
