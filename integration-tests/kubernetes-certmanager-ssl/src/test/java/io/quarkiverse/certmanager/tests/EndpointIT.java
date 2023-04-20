package io.quarkiverse.certmanager.tests;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.LocalPortForward;
import io.fabric8.kubernetes.client.okhttp.OkHttpClientFactory;

@EnabledIfSystemProperty(named = "run-endpoint-test-in-kubernetes", matches = "true")
public class EndpointIT {

    @Test
    public void shouldHttpsWork() throws IOException {
        KubernetesClient kubernetesClient = new KubernetesClientBuilder()
                .withHttpClientFactory(new OkHttpClientFactory())
                .build();
        try (LocalPortForward port = kubernetesClient.services()
                .withName("quarkus-hello-world")
                .portForward(8443)) {
            assertTrue(port.isAlive());
            String response = given().relaxedHTTPSValidation().when()
                    .get("https://localhost:" + port.getLocalPort())
                    .thenReturn().asString();
            assertEquals("Hello, World!", response);
        }

    }
}
