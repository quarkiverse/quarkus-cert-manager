package io.quarkiverse.certmanager.deployment;

public enum AutoConfigureMode {
    // It won't autoconfigure anything.
    NONE,
    // It will secure Ingress/Route resources if exposed, otherwise the HTTP endpoints.
    AUTOMATIC,
    // It will secure the HTTP endpoints and the Ingress/Route resources if exposed.
    ALL,
    // It will secure only the HTTP endpoints.
    HTTPS_ONLY,
    // It will secure only the Ingress/Route resources. It will throw an exception if it was not exposed.
    CLUSTER_ONLY
}
