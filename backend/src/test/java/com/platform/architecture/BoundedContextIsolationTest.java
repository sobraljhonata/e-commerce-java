package com.platform.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

class BoundedContextIsolationTest extends ArchitectureBaseTest {

  @Test
  void catalog_should_not_access_tenant_persistence() {
    noClasses()
        .that()
        .resideInAPackage("..catalog..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..tenant.adapters.out.persistence..")
        .allowEmptyShould(true)
        .check(imported);
  }

  @Test
  void order_should_not_access_pricing_persistence() {
    noClasses()
        .that()
        .resideInAPackage("..order..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..pricing.adapters.out.persistence..")
        .allowEmptyShould(true)
        .check(imported);
  }

  @Test
  void iam_should_not_depend_on_tenant_domain() {
    noClasses()
        .that()
        .resideInAPackage("..iam..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..tenant.domain..")
        .check(imported);
  }

  @Test
  void catalog_should_not_depend_on_tenant_domain() {
    noClasses()
        .that()
        .resideInAPackage("..catalog..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..tenant.domain..")
        .check(imported);
  }
}
