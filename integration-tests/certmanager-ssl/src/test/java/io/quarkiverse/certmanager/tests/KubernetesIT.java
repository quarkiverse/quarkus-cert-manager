package io.quarkiverse.certmanager.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.dekorate.utils.Serialization;
import io.fabric8.certmanager.api.model.v1.Certificate;
import io.fabric8.certmanager.api.model.v1.Issuer;
import io.fabric8.kubernetes.api.model.KubernetesList;

public class KubernetesIT {

    private static final String NAME = "quarkus-certmanager-integration-tests-certmanager-ssl";

    @Test
    public void shouldCertificateAndIssuerBeGenerated() throws IOException {
        KubernetesList resources = Serialization
                .unmarshalAsList(new FileInputStream(Paths.get("target", "kubernetes", "kubernetes.yml").toFile()));
        Certificate certificate = (Certificate) resources.getItems().stream().filter(Certificate.class::isInstance)
                .findFirst()
                .orElseGet(() -> {
                    Assertions.fail("Certificate not generated!");
                    return null;
                });
        assertEquals("tls-secret", certificate.getSpec().getSecretName());
        assertEquals(Arrays.asList("kubernetes-example.com", "localhost"), certificate.getSpec().getDnsNames());
        assertTrue(certificate.getSpec().getEncodeUsagesInRequest());
        assertFalse(certificate.getSpec().getIsCA());
        assertEquals(NAME, certificate.getSpec().getIssuerRef().getName());
        assertNull(certificate.getSpec().getKeystores().getJks());
        assertTrue(certificate.getSpec().getKeystores().getPkcs12().getCreate());
        assertEquals("password", certificate.getSpec().getKeystores().getPkcs12().getPasswordSecretRef().getKey());
        assertEquals("pkcs12-pass", certificate.getSpec().getKeystores().getPkcs12().getPasswordSecretRef().getName());
        assertEquals("RSA", certificate.getSpec().getPrivateKey().getAlgorithm());
        assertEquals("PKCS8", certificate.getSpec().getPrivateKey().getEncoding());
        assertEquals(2048, certificate.getSpec().getPrivateKey().getSize());
        assertEquals(Arrays.asList("Quarkus", "Community"), certificate.getSpec().getSubject().getOrganizations());
        assertEquals(Arrays.asList("server auth", "client auth"), certificate.getSpec().getUsages());

        Issuer issuer = (Issuer) resources.getItems().stream().filter(Issuer.class::isInstance)
                .findFirst()
                .orElseGet(() -> {
                    Assertions.fail("Issuer not generated!");
                    return null;
                });
        assertNull(issuer.getSpec().getCa());
        assertNull(issuer.getSpec().getVault());
        assertNotNull(issuer.getSpec().getSelfSigned());
    }
}
