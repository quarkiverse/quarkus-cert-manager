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
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.dekorate.utils.Serialization;
import io.fabric8.certmanager.api.model.v1.Certificate;
import io.fabric8.certmanager.api.model.v1.Issuer;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressTLS;

public class KubernetesIT {

    private static final String NAME = "quarkus-hello-world";

    @Test
    public void shouldCertificateAndIssuerBeGenerated() throws IOException {
        KubernetesList resources = Serialization
                .unmarshalAsList(new FileInputStream(Paths.get("target", "kubernetes", "kubernetes.yml").toFile()));
        // Certificate expected data:
        Certificate certificate = find(resources, Certificate.class);
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

        // Issuer expected data:
        Issuer issuer = find(resources, Issuer.class);
        assertNull(issuer.getSpec().getCa());
        assertNull(issuer.getSpec().getVault());
        assertNotNull(issuer.getSpec().getSelfSigned());

        // Deployment expected data:
        Deployment deployment = find(resources, Deployment.class);
        List<EnvVar> deploymentEnvVars = deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getEnv();
        assertTrue(deploymentEnvVars.stream()
                .anyMatch(envVar -> envVar.getName().equals("QUARKUS_HTTP_SSL_CERTIFICATE_KEY_STORE_FILE")
                        && envVar.getValue().equals("/etc/certs/keystore.p12")));
        assertTrue(deploymentEnvVars.stream()
                .anyMatch(envVar -> envVar.getName().equals("QUARKUS_HTTP_SSL_CERTIFICATE_KEY_STORE_FILE_TYPE")
                        && envVar.getValue().equals("PKCS12")));
        assertTrue(deploymentEnvVars.stream()
                .anyMatch(envVar -> envVar.getName().equals("QUARKUS_HTTP_SSL_CERTIFICATE_KEY_STORE_PASSWORD")
                        && envVar.getValueFrom().getSecretKeyRef().getName().equals("pkcs12-pass")
                        && envVar.getValueFrom().getSecretKeyRef().getKey().equals("password")));

        // Ingress expected data
        Ingress ingress = find(resources, Ingress.class);
        IngressTLS ingressTlsSpec = ingress.getSpec().getTls().get(0);
        assertTrue(ingressTlsSpec.getHosts().contains("kubernetes-example.com"));
        assertTrue(ingressTlsSpec.getHosts().contains("localhost"));
        assertTrue(ingressTlsSpec.getSecretName().contains("tls-secret"));

    }

    private static final <T> T find(KubernetesList list, Class<T> type) {
        return list.getItems().stream().filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElseGet(() -> {
                    Assertions.fail(type.getName() + " not generated!");
                    return null;
                });
    }
}
