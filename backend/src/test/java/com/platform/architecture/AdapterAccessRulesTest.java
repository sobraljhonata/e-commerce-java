package com.platform.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.junit.jupiter.api.Test;

class AdapterAccessRulesTest extends ArchitectureBaseTest {

    @Test
    void adapters_should_only_be_accessed_by_application_adapters_config_or_tests() {
        classes()
            .that().resideInAPackage("..adapters..")
            .should().onlyBeAccessed().byAnyPackage(
                "..adapters..",
                "..application..",
                "..config..",
                "..tenant..",
                "..iam..",
                "..catalog..",
                "..architecture..",
                "..bdd.."
            )
            .check(imported);
    }
}