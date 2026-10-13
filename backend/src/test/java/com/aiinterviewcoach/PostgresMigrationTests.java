package com.aiinterviewcoach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** CI verifies a clean PostgreSQL install with migrations and Hibernate validation. */
@EnabledIfEnvironmentVariable(named = "TEST_POSTGRES_URL", matches = ".+")
@SpringBootTest(properties = {
    "spring.datasource.url=${TEST_POSTGRES_URL}",
    "spring.datasource.username=${TEST_POSTGRES_USER:interview}",
    "spring.datasource.password=${TEST_POSTGRES_PASSWORD}",
    "jwt.secret=ci-only-secret-with-at-least-thirty-two-bytes",
    "gemini.api.key=ci-placeholder",
    "app.cors.origins=https://example.com",
    "app.file.upload-dir=build/postgres-test-uploads"
})
@ActiveProfiles("prod")
class PostgresMigrationTests {
    @Test void productionSchemaValidates() {}
}
