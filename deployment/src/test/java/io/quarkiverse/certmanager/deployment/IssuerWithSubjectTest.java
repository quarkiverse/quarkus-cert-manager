package io.quarkiverse.certmanager.deployment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class IssuerWithSubjectTest {
    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withEmptyApplication()
            .withConfigurationResource("test-with-subject.resources");

    @Test
    public void test() {
        Assertions.assertTrue(Boolean.TRUE, "Config is ok!");
    }
}
