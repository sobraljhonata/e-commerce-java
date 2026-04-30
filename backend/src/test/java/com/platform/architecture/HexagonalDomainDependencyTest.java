package com.platform.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

class HexagonalDomainDependencyTest extends ArchitectureBaseTest {

  @Test
  void domain_should_not_depend_on_adapters_or_spring() {
    noClasses()
        .that()
        .resideInAPackage("..domain..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..adapters..", "org.springframework..")
        .check(imported);
  }
}
