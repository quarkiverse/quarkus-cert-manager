package io.quarkiverse.certmanager.deployment.utils;

public enum KeystoreType {
    JKS("keystore.jks"),
    PKCS12("keystore.p12");

    private final String generatedFile;

    KeystoreType(String generatedFile) {
        this.generatedFile = generatedFile;
    }

    public String getGeneratedFile() {
        return generatedFile;
    }
}
