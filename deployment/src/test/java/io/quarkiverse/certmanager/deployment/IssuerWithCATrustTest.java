package io.quarkiverse.certmanager.deployment;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.kubernetes.spi.DecoratorBuildItem;
import io.quarkus.test.QuarkusUnitTest;

public class IssuerWithCATrustTest {
    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withEmptyApplication()
            .withConfigurationResource("test-with-ca-trust.resources")
            .addBuildChainCustomizer(builder -> builder.addBuildStep(context -> {
                boolean found = context.consumeMulti(DecoratorBuildItem.class).stream()
                        .map(DecoratorBuildItem::getDecorator)
                        .anyMatch(d -> isEnvVar(d, "QUARKUS_TLS_TRUST_STORE_PEM_CERTS", "/etc/certs/ca.crt"));
                if (!found) {
                    throw new IllegalStateException("The trust store has not been configured with the CA certificate");
                }

                context.produce(new FeatureBuildItem("ca-trust-test"));
            }).consumes(DecoratorBuildItem.class).produces(FeatureBuildItem.class).build());

    @Test
    public void test() {
        Assertions.assertTrue(Boolean.TRUE, "Trust store is configured!");
    }

    // The decorators are loaded by a different class loader, so we need to use reflection to inspect them.
    private static boolean isEnvVar(Object decorator, String name, String value) {
        if (!"io.dekorate.kubernetes.decorator.AddEnvVarDecorator".equals(decorator.getClass().getName())) {
            return false;
        }

        try {
            Field field = decorator.getClass().getDeclaredField("env");
            field.setAccessible(true);
            Object env = field.get(decorator);
            return name.equals(env.getClass().getMethod("getName").invoke(env))
                    && value.equals(env.getClass().getMethod("getValue").invoke(env));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
