package io.quarkiverse.certmanager.deployment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class TooManyIssuersTest {
    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withEmptyApplication()
            .withConfigurationResource("too-many-issuers.resources")
            .setExpectedException(IllegalStateException.class);

    @Test
    public void test() {
        Assertions.fail("Should not be invoked because no issuers has not being set.");
    }
}
