package com.platform.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

class ApplicationIndependenceTest extends ArchitectureBaseTest {

  @Test
  void application_should_not_depend_on_web_adapters() {
    noClasses()
        .that()
        .resideInAPackage("..application..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..adapters.in.web..")
        .check(imported);
  }
}
