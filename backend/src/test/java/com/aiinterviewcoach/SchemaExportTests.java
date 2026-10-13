package com.aiinterviewcoach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** Produces a PostgreSQL schema from the mappings for migration review. */
@SpringBootTest(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
    "spring.jpa.properties.jakarta.persistence.schema-generation.database.action=none",
    "spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create",
    "spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=build/postgresql-schema.sql",
    "spring.jpa.properties.hibernate.hbm2ddl.schema-generation.script.append=false"
})
@ActiveProfiles("test")
class SchemaExportTests {
    @Test void exportSchema() {
        org.junit.jupiter.api.Assertions.assertTrue(java.nio.file.Files.exists(java.nio.file.Path.of("build/postgresql-schema.sql")));
    }
}
